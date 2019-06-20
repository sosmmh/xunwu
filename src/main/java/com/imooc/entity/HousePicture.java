package com.imooc.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:25
 */

@Data
@Entity
@Table(name = "house_picture")
public class HousePicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "house_id")
    private Long houseId;

    private String path;

    @Column(name = "cdn_prefix")
    private String cdnPrefix;

    private int width;

    private int height;

    private String location;
}
