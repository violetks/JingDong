package com.lanqiao.jd.controller;

import com.alibaba.fastjson.JSONObject;
import com.lanqiao.jd.annotations.UserLoginToken;
import com.lanqiao.jd.entity.Comment;
import com.lanqiao.jd.entity.User;
import com.lanqiao.jd.service.CommentService;
import com.lanqiao.jd.service.TokenService;
import com.lanqiao.jd.service.UserService;
import com.lanqiao.jd.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    TokenService tokenService;
    //注册功能->向数据库中插入一条user记录
    //need:userName password  phoneNumber
    @PostMapping("/register")
    public Result register(User user) {
        return userService.register(user);
    }

    //    @PostMapping("/login")
//    public Result login(User user){
//        return userService.login(user);
//    }


    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        JSONObject jsonObject = new JSONObject();
        User userForBase = userService.findByUsername(user);
        if (userForBase == null) {
            jsonObject.put("message", "登录失败,用户不存在");
            return jsonObject;
        } else {
            if (!userForBase.getPassword().equals(user.getPassword())) {
                jsonObject.put("message", "登录失败,密码错误");
                return jsonObject;
            } else {
                String token = tokenService.getToken(userForBase);
                jsonObject.put("token", token);
                jsonObject.put("user", userForBase);
                return jsonObject;
            }
        }
    }

    @UserLoginToken
    @PostMapping("/getMessage")
    public String getMessage(){
        return "你已通过验证";
    }



    //评论相关
    @PostMapping("/insertComment")
    public Result insertComment(Comment comment){return commentService.insertComment(comment);}
    //展示某个商品的评论
    @PostMapping("/showCommentByItemId")
    public Result showCommentByItemId(@RequestParam(name = "productItemId") int productItemId){
        return commentService.showCommentByItemID(productItemId);
    }

}
