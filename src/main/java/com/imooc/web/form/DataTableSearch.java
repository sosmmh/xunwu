package com.imooc.web.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 20:00
 */
@Data
public class DataTableSearch {

    /** dataTable 要求回显 */
    private int draw;

    /** 规定分页的字段 */
    private int start;

    private int length;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMax;

    private String city;

    private String title;

    private String direction;

    private String orderBy;

}
