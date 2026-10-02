package com.hla.gysc.util;

import lombok.Data;

/**
 * 排序工具类
 */
@Data
public class OrderUtil {
    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否倒序
     */
    private Boolean isDesc;

    public OrderUtil(String orderBy, Boolean isDesc) {
        this.orderBy = orderBy;
        this.isDesc = isDesc;
    }
}
