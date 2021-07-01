package com.dutao.controller;



import com.alibaba.fastjson.JSONObject;
import com.dutao.constant.GitHubConstant;
import com.dutao.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;




@Controller
@RequestMapping("/oauth")
public class GithubLoginController {


    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    //回调地址

    @RequestMapping(value = "/callback/huawei")
    public String callback(String code, String state, Model model, HttpServletRequest req) throws Exception{

        if(!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(state)){
            //拿到我们的code,去请求token
            //发送一个请求到
            JSONObject tokens = HttpClientUtils.getTokenByCode(GitHubConstant.CALLBACK, "https://oauth-login.cloud.huawei.com/oauth2/v3/token", code, GitHubConstant.CLIENT_SECRET, GitHubConstant.CLIENT_ID, "authorization_code");
            System.out.println("the output of oauth2/v3/token is : \n" + tokens.toJSONString());
            // 调用方法从map中获得返回的--》 令牌
             String token = (String) tokens.get("id_token");
            //根据token发送请求获取登录人的信息  ，通过令牌去获得用户信息
            String userinfo_url = GitHubConstant.USER_INFO_URL.replace("TOKEN", token);
            String responseStr = HttpClientUtils.doGet(userinfo_url);//json
            System.out.println(responseStr);

            Map<String, String> responseMap = HttpClientUtils.getMapByJson(responseStr);
            // 成功则登陆
            return "/main";
        }

        return "/main";
    }

}

