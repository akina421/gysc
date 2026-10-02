package com.hla.gysc.controller.admin;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.Category;
import com.hla.gysc.entity.Product;
import com.hla.gysc.entity.ProductImage;
import com.hla.gysc.service.CategoryService;
import com.hla.gysc.service.ProductImageService;
import com.hla.gysc.service.ProductService;
import com.hla.gysc.util.Constants;
import com.hla.gysc.util.OrderUtil;
import com.hla.gysc.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Api(tags = "后台管理-产品页")
@Controller
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;

    @ApiOperation(value = "转到后台管理-产品列表页", notes = "转到后台管理-产品列表页")
    @GetMapping(value = "admin/product")
    public String goProductManagePage(HttpSession session, Map<String, Object> map) {
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        
        logger.info("加载产品列表页");
        
        List<Category> categoryList = categoryService.getList(null, null);
        map.put("categoryList", categoryList);
        
        // 默认按product_id降序排列，最新的产品在前
        OrderUtil orderUtil = new OrderUtil("product_id", true);
        PageUtil pageUtil = new PageUtil(0, 10);
        List<Product> productList = productService.getList(null, null, orderUtil, pageUtil);
        map.put("productList", productList);
        Integer productCount = productService.getTotal(null, null);
        map.put("productCount", productCount);
        pageUtil.setTotal(productCount);
        map.put("pageUtil", pageUtil);
        
        logger.info("产品列表加载完成，总数: {}, 当前页数量: {}", productCount, productList != null ? productList.size() : 0);
        
        return "admin/productManagePage";
    }

    @ApiOperation(value = "转到后台管理-产品添加页", notes = "转到后台管理-产品添加页")
    @GetMapping(value = "admin/product/new")
    public String goProductAddPage(HttpSession session, Map<String, Object> map) {
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        List<Category> categoryList = categoryService.getList(null, null);
        map.put("categoryList", categoryList);
        return "admin/include/productDetails";
    }

    @ApiOperation(value = "转到后台管理-产品编辑页", notes = "转到后台管理-产品编辑页")
    @GetMapping(value = "admin/product/{pid}")
    public String goProductEditPage(HttpSession session, Map<String, Object> map, @PathVariable Integer pid) {
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        Product product = productService.get(pid);
        if (product == null) {
            return "admin/include/loginMessage";
        }
        map.put("product", product);
        List<Category> categoryList = categoryService.getList(null, null);
        map.put("categoryList", categoryList);
        
        List<ProductImage> singleProductImageList = productImageService.getList(pid, (byte) 0, null);
        map.put("singleProductImageList", singleProductImageList);
        
        List<ProductImage> detailProductImageList = productImageService.getList(pid, (byte) 1, null);
        map.put("detailProductImageList", detailProductImageList);
        
        return "admin/include/productDetails";
    }

    @ApiOperation(value = "添加产品", notes = "添加产品")
    @ResponseBody
    @PostMapping(value = "admin/product", produces = "application/json;charset=UTF-8")
    public String addProduct(HttpSession session,
                             @RequestParam("product_category_id") Integer category_id,
                             @RequestParam String product_name,
                             @RequestParam String product_title,
                             @RequestParam Float product_sale_price,
                             @RequestParam(required = false) Float product_stock,
                             @RequestParam(required = false) String product_details) {
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            JSONObject object = new JSONObject();
            object.put(Constants.SUCCESS, false);
            object.put("message", "未登录或无权限");
            return String.valueOf(object);
        }
        
        try {
            JSONObject jsonObject = new JSONObject();
            Product product = new Product();
            Category category = new Category();
            category.setCategory_id(category_id);
            product.setProduct_category(category);
            product.setProduct_name(product_name);
            product.setProduct_title(product_title);
            product.setProduct_sale_price(product_sale_price.doubleValue());
            product.setProduct_price(product_sale_price.doubleValue());
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            product.setProduct_create_date(sdf.format(new java.util.Date()));
            product.setProduct_isEnabled(0);
            
            logger.info("准备添加产品: name={}, category_id={}, sale_price={}", product_name, category_id, product_sale_price);
            
            boolean success = productService.add(product);
            
            if (success) {
                jsonObject.put(Constants.SUCCESS, true);
                jsonObject.put("product_id", product.getProduct_id());
                logger.info("产品添加成功, ID: {}", product.getProduct_id());
            } else {
                jsonObject.put(Constants.SUCCESS, false);
                jsonObject.put("message", "添加失败");
                logger.warn("产品添加失败");
            }
            
            return String.valueOf(jsonObject);
        } catch (Exception e) {
            logger.error("添加产品异常", e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.SUCCESS, false);
            jsonObject.put("message", "服务器异常: " + e.getMessage());
            return String.valueOf(jsonObject);
        }
    }

    @ApiOperation(value = "更新产品信息", notes = "更新产品信息")
    @ResponseBody
    @PutMapping(value = "admin/product/{product_id}", produces = "application/json;charset=utf-8")
    public String updateProduct(@RequestParam String product_name,
                                @RequestParam String product_title,
                                @RequestParam Integer product_category_id,
                                @RequestParam Double product_sale_price,
                                @RequestParam Double product_price,
                                @RequestParam Integer product_isEnabled,
                                @RequestParam(required = false) String[] productSingleImageList,
                                @RequestParam(required = false) String[] productDetailsImageList,
                                @PathVariable("product_id") Integer product_id) {
        JSONObject jsonObject = new JSONObject();
        
        Product product = new Product();
        product.setProduct_id(product_id);
        product.setProduct_name(product_name);
        product.setProduct_title(product_title);
        Category category = new Category();
        category.setCategory_id(product_category_id);
        product.setProduct_category(category);
        product.setProduct_sale_price(product_sale_price);
        product.setProduct_price(product_price);
        product.setProduct_isEnabled(product_isEnabled);
        
        boolean yn = productService.update(product);
        if (!yn) {
            jsonObject.put(Constants.SUCCESS, false);
            throw new RuntimeException();
        }
        
        System.out.println("=== 保存图片信息 ===");
        System.out.println("productSingleImageList: " + (productSingleImageList != null ? productSingleImageList.length : "null"));
        System.out.println("productDetailsImageList: " + (productDetailsImageList != null ? productDetailsImageList.length : "null"));
        
        if (productSingleImageList != null && productSingleImageList.length > 0) {
            List<ProductImage> productImageList = new ArrayList<>(5);
            for (String imageUrl : productSingleImageList) {
                System.out.println("处理预览图: " + imageUrl);
                ProductImage productImage = new ProductImage();
                productImage.setProductImage_type((byte) 0);
                productImage.setProductImage_src(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
                productImage.setProductImage_product(product);
                productImageList.add(productImage);
            }
            System.out.println("准备保存 " + productImageList.size() + " 张预览图");
            yn = productImageService.addList(productImageList);
            System.out.println("预览图保存结果: " + yn);
            if (!yn) {
                jsonObject.put(Constants.SUCCESS, false);
                throw new RuntimeException();
            }
        }
        
        if (productDetailsImageList != null && productDetailsImageList.length > 0) {
            List<ProductImage> productImageList = new ArrayList<>(5);
            for (String imageUrl : productDetailsImageList) {
                System.out.println("处理详情图: " + imageUrl);
                ProductImage productImage = new ProductImage();
                productImage.setProductImage_type((byte) 1);
                productImage.setProductImage_src(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
                productImage.setProductImage_product(product);
                productImageList.add(productImage);
            }
            System.out.println("准备保存 " + productImageList.size() + " 张详情图");
            yn = productImageService.addList(productImageList);
            System.out.println("详情图保存结果: " + yn);
            if (!yn) {
                jsonObject.put(Constants.SUCCESS, false);
                throw new RuntimeException();
            }
        }

        System.out.println("==================");
        jsonObject.put(Constants.SUCCESS, true);
        jsonObject.put("product_id", product_id);
        return String.valueOf(jsonObject);
    }

    @ApiOperation(value = "上传产品图片", notes = "上传产品图片")
    @ResponseBody
    @PostMapping(value = "admin/uploadProductImage", produces = "application/json;charset=UTF-8")
    public String uploadProductImage(@RequestParam MultipartFile file, @RequestParam String imageType, HttpSession session) {
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFileName)) {
            throw new RuntimeException("上传失败！");
        }
        
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = UUID.randomUUID() + extension;
        
        String subPath;
        if ("details".equals(imageType)) {
            subPath = "res/images/item/productDetailsPicture/" + fileName;
        } else {
            subPath = "res/images/item/productSinglePicture/" + fileName;
        }
        
        String realPath = session.getServletContext().getRealPath("/");
        String filePath;
        
        if (realPath.contains("tomcat-docbase")) {
            String projectRoot = System.getProperty("user.dir");
            filePath = projectRoot + "/target/classes/META-INF/resources/" + subPath;
        } else {
            filePath = realPath + subPath;
        }
        
        JSONObject object = new JSONObject();
        try {
            File destFile = new File(filePath);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.transferTo(destFile);
            object.put(Constants.SUCCESS, true);
            object.put("fileName", fileName);
        } catch (IOException e) {
            logger.warn("文件上传失败！");
            e.printStackTrace();
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    @ApiOperation(value = "按ID删除产品", notes = "按ID删除产品")
    @ResponseBody
    @GetMapping(value = "admin/product/del/{id}", produces = "application/json;charset=utf-8")
    public String deleteProductById(@PathVariable Integer id) {
        JSONObject object = new JSONObject();
        boolean yn = productService.delete(id);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    @ApiOperation(value = "按条件查询产品", notes = "按条件查询产品")
    @ResponseBody
    @GetMapping(value = "admin/product/{index}/{count}", produces = "application/json;charset=UTF-8")
    public String getProductBySearch(@RequestParam(required = false) String product_name,
                                     @RequestParam(required = false) Integer category_id,
                                     @RequestParam(required = false) Boolean exact_match,
                                     @RequestParam(required = false) String orderBy,
                                     @RequestParam(required = false, defaultValue = "true") Boolean isDesc,
                                     @PathVariable Integer index,
                                     @PathVariable Integer count) {
        Product product = new Product();
        if (product_name != null && !product_name.trim().isEmpty()) {
            product.setProduct_name(product_name);
            // 设置精确匹配标识，默认为false（模糊查询）
            product.setExact_match(exact_match != null ? exact_match : false);
        }
        if (category_id != null && category_id > 0) {
            Category category = new Category();
            category.setCategory_id(category_id);
            product.setProduct_category(category);
        }
        
        JSONObject object = new JSONObject();
        PageUtil pageUtil = new PageUtil(index, count);
        
        OrderUtil orderUtil;
        if (orderBy != null && !orderBy.isEmpty()) {
            orderUtil = new OrderUtil(orderBy, isDesc);
        } else {
            // 默认按product_id降序排列，最新的产品在前
            orderUtil = new OrderUtil("product_id", true);
        }
        
        List<Product> productList = productService.getList(product, null, orderUtil, pageUtil);
        object.put("productList", productList);
        
        Integer productCount = productService.getTotal(product, null);
        object.put("productCount", productCount);
        pageUtil.setTotal(productCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);
        
        logger.info("查询产品列表: index={}, count={}, total={}, 返回数量={}, exact_match={}", 
            index, count, productCount, productList != null ? productList.size() : 0, exact_match);
        
        return String.valueOf(object);
    }
}