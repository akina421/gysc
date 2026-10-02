package com.hla.gysc.entity;

import lombok.Data;

/**
 * 产品订单项（购物车项）
 */
@Data
public class ProductOrderItem {
    private Integer productOrderItem_id;
    /**
     * 数量
     */
    private Short productOrderItem_number;
    /**
     * 单价
     */
    private Double productOrderItem_price;
    /**
     * 关联产品
     */
    private Product productOrderItem_product;
    /**
     * 关联用户
     */
    private User productOrderItem_user;
    /**
     * 用户备注
     */
    private String productOrderItem_userMessage;

}
