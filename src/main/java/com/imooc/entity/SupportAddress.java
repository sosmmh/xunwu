package com.imooc.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:39
 */
@Data
@Entity
@Table(name = "support_address")
public class SupportAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "cn_name")
    private String cnName;

    private String level;

    /**
     * 行政级别
     */
    @Getter
    public enum Level {

        CITY("city"),

        REGION("region");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
