package com.hla.gysc.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.Category;
import com.hla.gysc.entity.Product;
import com.hla.gysc.entity.ProductImage;
import com.hla.gysc.entity.User;
import com.hla.gysc.service.CategoryService;
import com.hla.gysc.service.ProductImageService;
import com.hla.gysc.service.ProductService;
import com.hla.gysc.service.impl.UserServiceImpl;
import com.hla.gysc.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产品详情页
 */
@Api(tags = "产品详情页")
@Controller
@RequiredArgsConstructor
public class ForeProductDetailsController extends BaseController {
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final UserServiceImpl userService;

    @ApiOperation(value = "转到产品详情页", notes = "转到产品详情页")
    @GetMapping(value = "product/{pid}")
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @PathVariable("pid") String pid) {
        Object userId = checkUser(session);
        if (userId != null) {
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        
        Integer product_id = Integer.parseInt(pid);
        Product product = productService.get(product_id);
        if (product == null || product.getProduct_isEnabled() == 1) {
            return "redirect:/404";
        }
        
        product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
        
        List<ProductImage> productImageList = productImageService.getList(product_id, null, null);
        List<ProductImage> singleProductImageList = new ArrayList<>(5);
        List<ProductImage> detailsProductImageList = new ArrayList<>(8);
        for (ProductImage productImage : productImageList) {
            if (productImage.getProductImage_type() == 0) {
                singleProductImageList.add(productImage);
            } else {
                detailsProductImageList.add(productImage);
            }
        }
        product.setSingleProductImageList(singleProductImageList);
        product.setDetailProductImageList(detailsProductImageList);
        
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 3));
        map.put("categoryList", categoryList);
        map.put("product", product);
        map.put("pageUtil", new PageUtil(0, 10));
        
        return "fore/productDetailsPage";
    }
}
