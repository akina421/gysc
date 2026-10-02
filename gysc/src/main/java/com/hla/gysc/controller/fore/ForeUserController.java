package com.hla.gysc.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.User;
import com.hla.gysc.service.UserService;
import com.hla.gysc.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 前台商城-用户
 */
@Api(tags = "前台商城-用户")
@Controller
public class ForeUserController extends BaseController {
    @Resource
    private UserService userService;

    // 转到前台商城-用户详情页
    @ApiOperation(value = "转到前台商城-用户详情页", notes = "转到前台商城-用户详情页")
    @GetMapping(value = "userDetails")
    public String goToUserDetail(Map<String, Object> map, HttpSession session) {
        Object userId = checkUser(session);
        if (userId == null) {
            return "redirect:/login";
        }
        User user = userService.get(Integer.valueOf(userId.toString()));
        map.put("user", user);
        return "fore/userDetails";
    }

    // 前台商城-用户更换头像
    @ApiOperation(value = "前台商城-用户更换头像", notes = "前台商城-用户更换头像")
    @ResponseBody
    @PostMapping(value = "uploadUserHeadImage", produces = "application/json;charset=utf-8")
    public String uploadUserHeadImage(@RequestParam MultipartFile file, HttpSession session) {
        JSONObject object = new JSONObject();
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        try {
            String fileName = System.currentTimeMillis() + ".jpg";
            String dest = session.getServletContext().getRealPath("/res/images/user/") + fileName;
            file.transferTo(new File(dest));
            User user = new User();
            user.setUser_id(Integer.valueOf(userId.toString()));
            user.setUser_profile_picture_src(fileName);
            boolean yn = userService.update(user);
            if (yn) {
                object.put(Constants.SUCCESS, true);
                object.put("fileName", fileName);
            } else {
                object.put(Constants.SUCCESS, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    // 前台商城-用户详情更新
    @ApiOperation(value = "前台商城-用户详情更新", notes = "前台商城-用户详情更新")
    @ResponseBody
    @PostMapping(value = "user/update", produces = "application/json;charset=utf-8")
    public String userUpdate(@RequestParam String user_nickname,
                             @RequestParam String user_realname,
                             @RequestParam Byte user_gender,
                             @RequestParam String user_birthday,
                             HttpSession session) {
        JSONObject object = new JSONObject();
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        User user = new User();
        user.setUser_id(Integer.valueOf(userId.toString()));
        user.setUser_nickname(user_nickname);
        user.setUser_realname(user_realname);
        user.setUser_gender(user_gender);
        user.setUser_birthday(user_birthday);
        boolean yn = userService.update(user);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }
}
