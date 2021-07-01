package com.dutao.constant;

public class GitHubConstant {
    // 这里填写在GitHub上注册应用时候获得 CLIENT ID
    public static final String  CLIENT_ID="104473867";
    //这里填写在GitHub上注册应用时候获得 CLIENT_SECRET
    public static final String CLIENT_SECRET="168b91675686bb6a93719423551e890e23b794986975f69b7b09a32422d0ec33";
    // 回调路径 网站上设置的回调路径没有;但是会传回来;为了一致必须加上;
    public static final String CALLBACK = "http://127.0.0.1:8443/oauth/callback/huawei;";

    //获取code的url
    public static final String CODE_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize?response_type=code&access_type=offline&state=state_parameter_passthrough_value&scope=openid+profile&client_id="+CLIENT_ID+"&state=STATE&redirect_uri="+CALLBACK+"";
    //获取token的url
    public static final String TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token HTTP/1.1?grant_type=authorization_code&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&code=CODE&redirect_uri="+CALLBACK+"";
    //获取用户信息的url
    public static final String USER_INFO_URL = "https://oauth-login.cloud.huawei.com//oauth2/v3/tokeninfo?id_token=TOKEN";

}
