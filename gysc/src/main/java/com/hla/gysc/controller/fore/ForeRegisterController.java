package com.hla.gysc.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.User;
import com.hla.gysc.service.UserService;
import com.hla.gysc.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 前台商城 - 用户注册
 */
@Api(tags = "前台商城-用户注册")
@Controller
public class ForeRegisterController extends BaseController {
    @Resource
    private UserService userService;

    // 转到前台商城-用户注册页
    @ApiOperation(value = "转到前台商城-用户注册页", notes = "转到前台商城-用户注册页")
    @GetMapping(value = "register")
    public String goToPage() {
        return "fore/register";
    }

    // 商城前台-用户注册-ajax
    @ApiOperation(value = "商城前台-用户注册", notes = "商城前台-用户注册")
    @ResponseBody
    @PostMapping(value = "register", produces = "application/json;charset=utf-8")
    public String register(@RequestParam String user_name,
                           @RequestParam String user_password,
                           @RequestParam String user_nickname,
                           @RequestParam(required = false) String user_birthday,
                           @RequestParam(required = false) Byte user_gender) {
        JSONObject object = new JSONObject();
        try {
            logger.info("开始注册用户: {}", user_name);
            
            User queryUser = new User();
            queryUser.setUser_name(user_name);
            Integer total = userService.getTotal(queryUser);
            if (total > 0) {
                object.put(Constants.SUCCESS, false);
                object.put("message", "用户名已存在");
                return String.valueOf(object);
            }
            
            User user = new User();
            user.setUser_name(user_name);
            user.setUser_password(user_password);
            user.setUser_nickname(user_nickname);
            user.setUser_gender(user_gender != null ? user_gender : (byte) 0);
            user.setUser_birthday(user_birthday != null && !user_birthday.isEmpty() ? user_birthday : "2000-01-01");
            user.setDel_flag(0);
            
            logger.info("准备插入用户数据: {}", user);
            boolean yn = userService.add(user);
            logger.info("用户注册结果: {}", yn);
            
            if (yn) {
                object.put(Constants.SUCCESS, true);
            } else {
                object.put(Constants.SUCCESS, false);
                object.put("message", "注册失败，请稍后重试");
            }
        } catch (Exception e) {
            logger.error("用户注册异常", e);
            object.put(Constants.SUCCESS, false);
            object.put("message", "系统错误: " + e.getMessage());
        }
        return String.valueOf(object);
    }
}
