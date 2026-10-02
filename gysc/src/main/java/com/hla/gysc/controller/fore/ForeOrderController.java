package com.hla.gysc.controller.fore;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.*;
import com.hla.gysc.service.*;
import com.hla.gysc.util.Constants;
import com.hla.gysc.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 购物车
 */
@Api(tags = "购物车")
@Controller
public class ForeOrderController extends BaseController {
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private ProductOrderItemService productOrderItemService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ProductImageService productImageService;
    public static final String LOGIN = "redirect:/login";
    public static final String CART = "redirect:/cart";

    // 转到购物车页
    @ApiOperation(value = "转到购物车页", notes = "转到购物车页")
    @GetMapping(value = "cart")
    public String goToCartPage(Map<String, Object> map, HttpSession session) {
        // 检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            // 获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        // 获取用户购物车信息
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        Integer orderItemTotal = 0;
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            // 获取用户购物车的商品总数
            orderItemTotal = productOrderItemService.getTotalByUserId(Integer.valueOf(userId.toString()));
            // 获取用户购物车内的商品信息
            for (ProductOrderItem orderItem : orderItemList) {
                Integer product_id = orderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, null));
                product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
                orderItem.setProductOrderItem_product(product);
            }
        }
        map.put("orderItemList", orderItemList);
        map.put("orderItemTotal", orderItemTotal);
        // 转到购物车页
        return "fore/productBuyCarPage";
    }

    // 更新购物车订单项数量-ajax
    @ApiOperation(value = "更新购物车订单项数量", notes = "更新购物车订单项数量")
    @ResponseBody
    @PutMapping(value = "orderItem", produces = "application/json;charset=utf-8")
    public String updateOrderItem(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                  @RequestParam String orderItemMap) {
        JSONObject object = new JSONObject();
        // 检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        JSONObject orderItemString = JSON.parseObject(orderItemMap);
        Set<String> orderItemIDSet = orderItemString.keySet();
        if (CollectionUtil.isNotEmpty(orderItemIDSet)) {
            // 更新产品订单项数量
            for (String key : orderItemIDSet) {
                ProductOrderItem productOrderItem = productOrderItemService.get(Integer.valueOf(key));
                if (productOrderItem == null || !productOrderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
                    // 订单项为空或用户状态不一致！
                    object.put(Constants.SUCCESS, false);
                    return String.valueOf(object);
                }
                Short number = Short.valueOf(orderItemString.getString(key));
                if (number <= 0 || number > 500) {
                    // 订单项产品数量不合法！
                    object.put(Constants.SUCCESS, false);
                    return String.valueOf(object);
                }
                double price = productOrderItem.getProductOrderItem_price() / productOrderItem.getProductOrderItem_number();
                ProductOrderItem productOrderItem1 = new ProductOrderItem();
                productOrderItem1.setProductOrderItem_id(Integer.valueOf(key));
                productOrderItem1.setProductOrderItem_number(number);
                productOrderItem1.setProductOrderItem_price(number * price);
                Boolean yn = productOrderItemService.update(productOrderItem1);
                if (!yn) {
                    throw new RuntimeException();
                }
            }
            Object[] orderItemIDArray = orderItemIDSet.toArray();
            object.put(Constants.SUCCESS, true);
            object.put("orderItemIDArray", orderItemIDArray);
            return String.valueOf(object);
        } else {
            // 无订单项可以处理
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
    }

    // 创建订单项-购物车-ajax
    @ApiOperation(value = "创建订单项-购物车", notes = "创建订单项-购物车")
    @ResponseBody
    @PostMapping(value = "orderItem/create/{product_id}", produces = "application/json;charset=utf-8")
    public String createOrderItem(@PathVariable("product_id") Integer product_id,
                                  @RequestParam(required = false, defaultValue = "1") Short product_number,
                                  HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        // 检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        // 通过产品ID获取产品信息：{}, product_id
        Product product = productService.get(product_id);
        if (product == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        // 产品订单项
        ProductOrderItem productOrderItem = new ProductOrderItem();
        // 检查用户的购物车项
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            for (ProductOrderItem orderItem : orderItemList) {
                if (orderItem.getProductOrderItem_product().getProduct_id().equals(product_id)) {
                    // 找到已有的产品，进行数量追加
                    int number = orderItem.getProductOrderItem_number();
                    number += 1;
                    productOrderItem.setProductOrderItem_id(orderItem.getProductOrderItem_id());
                    productOrderItem.setProductOrderItem_number((short) number);
                    productOrderItem.setProductOrderItem_price(number * product.getProduct_sale_price());
                    boolean yn = productOrderItemService.update(productOrderItem);
                    if (yn) {
                        object.put(Constants.SUCCESS, true);
                    } else {
                        object.put(Constants.SUCCESS, false);
                    }
                    return String.valueOf(object);
                }
            }
        }
        // 封装订单项对象
        productOrderItem.setProductOrderItem_product(product);
        productOrderItem.setProductOrderItem_number(product_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price() * product_number);
        User user = new User();
        user.setUser_id(Integer.valueOf(userId.toString()));
        productOrderItem.setProductOrderItem_user(user);
        boolean yn = productOrderItemService.add(productOrderItem);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    // 删除订单项-购物车-ajax
    @ApiOperation(value = "删除订单项-购物车", notes = "删除订单项-购物车")
    @ResponseBody
    @DeleteMapping(value = "orderItem/{orderItem_id}", produces = "application/json;charset=utf-8")
    public String deleteOrderItem(@PathVariable("orderItem_id") Integer orderItem_id,
                                  HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        // 检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        // 检查用户的购物车项
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        boolean isMine = false;
        for (ProductOrderItem orderItem : orderItemList) {
            // 找到匹配的购物车项
            if (orderItem.getProductOrderItem_id().equals(orderItem_id)) {
                isMine = true;
                break;
            }
        }
        if (isMine) {
            // 删除订单项信息
            boolean yn = productOrderItemService.deleteList(new Integer[]{orderItem_id});
            if (yn) {
                object.put(Constants.SUCCESS, true);
            } else {
                object.put(Constants.SUCCESS, false);
            }
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    // 批量删除订单项-购物车-ajax
    @ApiOperation(value = "批量删除订单项-购物车", notes = "批量删除订单项-购物车")
    @ResponseBody
    @DeleteMapping(value = "orderItem/batch", produces = "application/json;charset=utf-8")
    public String batchDeleteOrderItem(@RequestBody Integer[] orderItem_ids,
                                       HttpSession session) {
        JSONObject object = new JSONObject();
        // 检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            object.put("message", "请先登录");
            return String.valueOf(object);
        }
        
        if (orderItem_ids == null || orderItem_ids.length == 0) {
            object.put(Constants.SUCCESS, false);
            object.put("message", "请选择要删除的商品");
            return String.valueOf(object);
        }
        
        // 检查用户的购物车项
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        List<Integer> validIds = new ArrayList<>();
        
        for (Integer orderItemId : orderItem_ids) {
            for (ProductOrderItem orderItem : orderItemList) {
                if (orderItem.getProductOrderItem_id().equals(orderItemId)) {
                    validIds.add(orderItemId);
                    break;
                }
            }
        }
        
        if (validIds.isEmpty()) {
            object.put(Constants.SUCCESS, false);
            object.put("message", "没有权限删除这些商品");
            return String.valueOf(object);
        }
        
        // 批量删除订单项
        boolean yn = productOrderItemService.deleteList(validIds.toArray(new Integer[0]));
        if (yn) {
            object.put(Constants.SUCCESS, true);
            object.put("message", "删除成功");
        } else {
            object.put(Constants.SUCCESS, false);
            object.put("message", "删除失败");
        }
        return String.valueOf(object);
    }
}