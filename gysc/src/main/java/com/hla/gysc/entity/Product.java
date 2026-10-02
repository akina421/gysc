package com.hla.gysc.entity;

import lombok.Data;

import java.util.List;

/**
 * 产品
 */
@Data
public class Product {
    private Integer product_id;
    /**
     * 产品名称
     */
    private String product_name;
    /**
     * 产品标题
     */
    private String product_title;
    /**
     * 原价
     */
    private Double product_price;
    /**
     * 促销价
     */
    private Double product_sale_price;
    /**
     * 创建日期
     */
    private String product_create_date;
    /**
     * 类别
     */
    private Category product_category;
    /**
     * 删除标识(1删除 0未删除）
     */
    private Integer product_isEnabled;
    /**
     * 单一产品图片
     */
    private List<ProductImage> singleProductImageList;
    /**
     * 详细产品图片
     */
    private List<ProductImage> detailProductImageList;
    /**
     * 产品订单项
     */
    private List<ProductOrderItem> productOrderItemList;
    /**
     * 销量数
     */
    private Integer product_sale_count;
    
    /**
     * 是否精确匹配商品名称（true:精确匹配, false或null:模糊匹配）
     */
    private Boolean exact_match;
}
