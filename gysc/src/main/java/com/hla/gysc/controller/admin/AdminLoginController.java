package com.hla.gysc.controller.admin;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.Admin;
import com.hla.gysc.entity.ApiVerCodeResp;
import com.hla.gysc.service.AdminService;
import com.hla.gysc.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 后台管理-登录页
 */
@Api(tags = "后台管理-登录页")
@Controller
@RequiredArgsConstructor
public class AdminLoginController extends BaseController {
    private final AdminService adminService;

    // 转到后台管理-登录页
    @ApiOperation(value = "转到后台管理-登录页", notes = "转到后台管理-登录页")
    @GetMapping("admin/login")
    public String goToPage() {
        // 转到后台管理-登录页
        return "admin/loginPage";
    }

    // 登陆验证-ajax
    @ApiOperation(value = "登陆验证", notes = "登陆验证")
    @ResponseBody
    @PostMapping(value = "admin/login/doLogin", produces = "application/json;charset=utf-8")
    public String checkLogin(HttpSession session, @RequestParam String username, @RequestParam String password) {
        // 管理员登录验证
        Admin admin = adminService.getAdmin(username, password);
        JSONObject object = new JSONObject();
        if (ObjectUtil.isEmpty(admin)) {
            // 登录验证失败
            object.put(Constants.SUCCESS, false);
        } else {
            // 登录验证成功，管理员ID传入会话
            session.setAttribute(Constants.ADMIN_ID, admin.getId());
            object.put(Constants.SUCCESS, true);
        }
        return String.valueOf(object);
    }

    // 获取管理员头像路径-ajax
    @ApiOperation(value = "获取管理员头像路径", notes = "获取管理员头像路径")
    @ResponseBody
    @GetMapping(value = "admin/login/profile_picture", produces = "application/json;charset=utf-8")
    public String getAdminProfilePicture(@RequestParam String username) {
        // 根据用户名获取管理员头像路径
        Admin admin = adminService.get(username, null);
        JSONObject object = new JSONObject();
        if (admin == null) {
            // 未找到头像路径
            object.put(Constants.SUCCESS, false);
        } else {
            // 成功获取头像路径
            object.put(Constants.SUCCESS, true);
            object.put("srcString", admin.getAdmin_profile_picture_src());
        }
        return String.valueOf(object);
    }


}