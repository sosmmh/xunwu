package com.imooc.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 19:26
 */
@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String name;
}
