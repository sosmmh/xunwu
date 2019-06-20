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
@Table(name = "subway")
public class Subway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "city_en_name")
    private String cityEnName;

}
