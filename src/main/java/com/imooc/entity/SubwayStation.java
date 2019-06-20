package com.imooc.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:05
 */
@Data
@Entity
@Table(name = "subway_station")
public class SubwayStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subway_id")
    private Long subwayId;

    private String name;
}

