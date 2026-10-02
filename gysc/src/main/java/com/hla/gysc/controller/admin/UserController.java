package com.hla.gysc.controller.admin;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hla.gysc.controller.BaseController;
import com.hla.gysc.entity.User;
import com.hla.gysc.service.UserService;
import com.hla.gysc.util.Constants;
import com.hla.gysc.util.OrderUtil;
import com.hla.gysc.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * 后台管理-用户页
 */
@Api(tags = "后台管理-用户页")
@Controller
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    // 转到后台管理-用户页-ajax
    @ApiOperation(value = "转到后台管理-用户页", notes = "转到后台管理-用户页")
    @GetMapping(value = "admin/user")
    public String goUserManagePage(HttpSession session, Map<String, Object> map) {
        // 检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        // 获取前十条用户信息
        PageUtil pageUtil = new PageUtil(0, 10);
        List<User> userList = userService.getList(null, null, pageUtil);
        map.put("userList", userList);
        // 获取用户总数量
        Integer userCount = userService.getTotal(null);
        map.put("userCount", userCount);
        // 获取分页信息
        pageUtil.setTotal(userCount);
        map.put("pageUtil", pageUtil);
        // 转到后台管理-用户页-ajax方式
        return "admin/userManagePage";
    }


    // 转到后台管理-用户详情页-ajax
    @ApiOperation(value = "转到后台管理-用户详情页", notes = "转到后台管理-用户详情页")
    @GetMapping(value = "admin/user/{uid}")
    public String getUserById(HttpSession session, Map<String, Object> map, @PathVariable Integer uid/* 用户ID */) {
        // 检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        // 获取user_id为{}的用户信息, uid
        User user = userService.get(uid);
        if (!StringUtils.isEmpty(user.getUser_realname())) {
            // 用户隐私加密
            user.setUser_realname(user.getUser_realname().charAt(0) + "*");
        } else {
            user.setUser_realname("未命名");
        }
        map.put("user", user);
        // 转到后台管理-用户详情页-ajax方式
        return "admin/include/userDetails";
    }

    // 按条件查询用户-ajax
    @ApiOperation(value = "按条件查询用户", notes = "按条件查询用户")
    @ResponseBody
    @GetMapping(value = "admin/user/{index}/{count}", produces = "application/json;charset=UTF-8")
    public String getUserBySearch(@RequestParam(required = false) String user_name/* 用户名称 */,
                                  @RequestParam(required = false) Byte[] user_gender_array/* 用户性别数组 */,
                                  @RequestParam(required = false) String orderBy/* 排序字段 */,
                                  @RequestParam(required = false, defaultValue = "true") Boolean isDesc/* 是否倒序 */,
                                  @PathVariable Integer index/* 页数 */,
                                  @PathVariable Integer count/* 行数 */) throws UnsupportedEncodingException {
        // 移除不必要条件
        Byte gender = null;
        if (user_gender_array != null && user_gender_array.length == 1) {
            gender = user_gender_array[0];
        }
        if (user_name != null) {
            // 如果为非空字符串则解决中文乱码
            user_name = "".equals(user_name) ? null : URLDecoder.decode(user_name, "UTF-8");
        }
        if (orderBy != null && "".equals(orderBy)) {
            orderBy = null;
        }
        // 封装查询条件
        User user = new User();
        user.setUser_name(user_name);
        user.setUser_gender(gender);
        OrderUtil orderUtil = null;
        if (orderBy != null) {
            // 根据{}排序，是否倒序:{}, orderBy, isDesc
            orderBy = orderBy.replaceAll("[()]", "");
            orderUtil = new OrderUtil(orderBy, isDesc);
        }
        JSONObject object = new JSONObject();
        // 按条件获取第{}页的{}条用户, index + 1, count
        PageUtil pageUtil = new PageUtil(index, count);
        List<User> userList = userService.getList(user, orderUtil, pageUtil);
        object.put("userList", JSON.parseArray(JSON.toJSONString(userList)));
        // 按条件获取用户总数量
        Integer userCount = userService.getTotal(user);
        object.put("userCount", userCount);
        // 获取分页信息
        pageUtil.setTotal(userCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);
        return String.valueOf(object);
    }

    // 按ID删除用户并返回最新结果-ajax
    @ApiOperation(value = "按ID删除用户并返回最新结果", notes = "按ID删除用户并返回最新结果")
    @ResponseBody
    @GetMapping(value = "admin/user/del/{id}", produces = "application/json;charset=utf-8")
    public String deleteProductById(@PathVariable Integer id) {
        JSONObject object = new JSONObject();
        User user = userService.get(id);
        user.setDel_flag(1);
        boolean yn = userService.update(user);
        if (yn) {
            // 删除成功！
            object.put(Constants.SUCCESS, true);
        } else {
            // 删除失败！事务回滚
            object.put(Constants.SUCCESS, false);
            throw new RuntimeException();
        }
        return String.valueOf(object);
    }

    // 更新用户信息-ajax
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @ResponseBody
    @PutMapping(value = "admin/user/{id}", produces = "application/json;charset=utf-8")
    public String updateUserById(@PathVariable Integer id,
                                  @RequestParam(required = false) String user_nickname,
                                  @RequestParam(required = false) String user_realname,
                                  @RequestParam(required = false) Byte user_gender,
                                  @RequestParam(required = false) String user_birthday) {
        JSONObject object = new JSONObject();
        
        // 检查管理员权限
        // Object adminId = checkAdmin(session);
        // if (adminId == null) {
        //     object.put(Constants.SUCCESS, false);
        //     object.put("message", "未登录或无权限");
        //     return String.valueOf(object);
        // }
        
        try {
            // 获取用户
            User user = userService.get(id);
            if (user == null) {
                object.put(Constants.SUCCESS, false);
                object.put("message", "用户不存在");
                return String.valueOf(object);
            }
            
            // 更新字段
            if (user_nickname != null && !user_nickname.trim().isEmpty()) {
                user.setUser_nickname(user_nickname.trim());
            }
            
            if (user_realname != null && !user_realname.trim().isEmpty()) {
                user.setUser_realname(user_realname.trim());
            }
            
            if (user_gender != null) {
                user.setUser_gender(user_gender);
            }
            
            if (user_birthday != null && !user_birthday.trim().isEmpty()) {
                user.setUser_birthday(user_birthday.trim());
            }
            
            // 执行更新
            boolean success = userService.update(user);
            
            if (success) {
                object.put(Constants.SUCCESS, true);
                logger.info("用户信息更新成功，用户ID: {}", id);
            } else {
                object.put(Constants.SUCCESS, false);
                object.put("message", "更新失败");
                logger.warn("用户信息更新失败，用户ID: {}", id);
            }
            
        } catch (Exception e) {
            object.put(Constants.SUCCESS, false);
            object.put("message", "系统错误：" + e.getMessage());
            logger.error("更新用户信息异常", e);
        }
        
        return String.valueOf(object);
    }
}
