package com.hla.gysc.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.Admin;
import com.hla.gysc.service.AdminService;
import com.hla.gysc.service.ProductService;
import com.hla.gysc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 后台管理-主页
 */
@Api(tags = "后台管理-主页")
@Controller
@RequiredArgsConstructor
public class AdminHomeController extends BaseController {
    private final AdminService adminService;
    private final ProductService productService;
    private final UserService userService;

    // 转到后台管理-主页
    @ApiOperation(value = "转到后台管理-主页", notes = "转到后台管理-主页")
    @GetMapping(value = "admin")
    public String goToPage(HttpSession session, Map<String, Object> map) throws ParseException {
        // 检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        // 获取管理员信息
        Admin admin = adminService.get(null, Integer.parseInt(adminId.toString()));
        map.put("admin", admin);
        // 获取统计信息
        Integer productTotal = productService.getTotal(null, new Byte[]{0, 2});
        Integer userTotal = userService.getTotal(null);
        // 获取图表信息
        map.put("jsonObject", getChartData(null, null));
        map.put("productTotal", productTotal);
        map.put("userTotal", userTotal);
        // 转到后台管理-主页
        return "admin/homePage";
    }

    // 转到后台管理-home主页-ajax
    @ApiOperation(value = "转到后台管理-home主页", notes = "转到后台管理-home主页")
    @GetMapping(value = "admin/home")
    public String goToPageByAjax(HttpSession session, Map<String, Object> map) throws ParseException {
        // 检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        // 获取管理员信息
        Admin admin = adminService.get(null, Integer.parseInt(adminId.toString()));
        map.put("admin", admin);
        // 获取统计信息
        Integer productTotal = productService.getTotal(null, new Byte[]{0, 2});
        Integer userTotal = userService.getTotal(null);
        // 获取图表信息
        map.put("jsonObject", getChartData(null, null));
        map.put("productTotal", productTotal);
        map.put("userTotal", userTotal);
        // 转到后台管理-主页-ajax方式
        return "admin/homeManagePage";
    }

    // 按日期查询图表数据-ajax
    @ApiOperation(value = "按日期查询图表数据", notes = "按日期查询图表数据")
    @ResponseBody
    @GetMapping(value = "admin/home/charts", produces = "application/json;charset=utf-8")
    public String getChartDataByDate(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate) throws ParseException {
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return getChartData(simpleDateFormat.parse(beginDate), simpleDateFormat.parse(endDate)).toJSONString();
        } else {
            return getChartData(null, null).toJSONString();
        }
    }

    // 获取图表的JSON数据
    private JSONObject getChartData(Date beginDate, Date endDate) throws ParseException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "订单功能已删除");
        return jsonObject;
    }
}