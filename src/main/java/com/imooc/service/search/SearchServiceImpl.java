package com.imooc.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.entity.House;
import com.imooc.entity.HouseDetail;
import com.imooc.entity.HouseTag;
import com.imooc.repository.HouseDetailRepository;
import com.imooc.repository.HouseRepository;
import com.imooc.repository.HouseTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/09 22:52
 */
@Service
@Slf4j
public class SearchServiceImpl implements ISearchService {

    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    private static final String INDEX_TOPIC = "house_build";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository tagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            HouseIndexMessage houseIndexMessage = objectMapper.readValue(content, HouseIndexMessage.class);
            switch (houseIndexMessage.getOperation()) {
                case HouseIndexMessage.INDEX:
                    this.createOrUpdateIndex(houseIndexMessage);
                    break;
                case HouseIndexMessage.REMOVE:
                    this.removeIndex(houseIndexMessage);
                    break;
                default:
                    log.warn("Not support message content: {}", content);

            }
        } catch (IOException e) {
            log.error("Cannot parse json for: {}, {}", content, e);
        }
    }

    private void createOrUpdateIndex(HouseIndexMessage message) {

        Long houseId = message.getHouseId();

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            log.error("index house {} does not exist!", houseId);
            this.index(houseId);
            return;
        }

        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, indexTemplate);

        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        if (detail == null) {
            // TODO 异常情况
        }

        modelMapper.map(detail, indexTemplate);

        List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);
        if (!CollectionUtils.isEmpty(tags)) {
            List<String> tagStrings = tags.stream().map(e -> e.getName()).collect(Collectors.toList());
            indexTemplate.setTags(tagStrings);
        }

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
        log.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();

        boolean success;
        long totalHits = searchResponse.getHits().getTotalHits();
        if (totalHits == 0) {
            success = create(indexTemplate);
        } else if (totalHits == 1) {
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, indexTemplate);
        } else {
            success = deleteAndCreate(totalHits, indexTemplate);
        }

        if (success) {
            log.debug("Index success with house {}", houseId);
        }
    }

    private void removeIndex(HouseIndexMessage message) {

        Long houseId = message.getHouseId();

        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
                .source(INDEX_NAME);

        log.debug("Delete by query for house: {}", builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        log.debug("Delete total {}", deleted);

        if (deleted == 0) {
            this.remove(houseId, message.getRetry() + 1);
        }
    }

    @Override
    public void index(Long houseId) {
        this.index(houseId, 0);
    }

    private void index(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            log.error("Retry index times over 3 for house {}, please check it!", houseId);
            return;
        }
        HouseIndexMessage houseIndexMessage = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry + 1);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(houseIndexMessage));
        } catch (JsonProcessingException e) {
            log.error("Json encode error for {}", houseIndexMessage);
        }
    }

    private boolean create(HouseIndexTemplate indexTemplate) {
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            log.debug("Create index with house: {}", indexTemplate.getHouseId());

            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index house {} {}", indexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean update(String esId, HouseIndexTemplate indexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            log.debug("Update index with house: {}", indexTemplate.getHouseId());

            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index house {} {}", indexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))
                .source(INDEX_NAME);

        log.debug("Delete by query for house: {}", builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            log.warn("Need delete {}, but {} was deleted", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }

    @Override
    public void remove(Long houseId) {
        this.remove(houseId, 0);
    }

    private void remove(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            log.error("Retry remove times over 3 for house {}, please check it!", houseId);
            return;
        }
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry + 1);
        try {
            this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Cannot encode json for {}, {}", message, e);
        }
    }
}
