$(function () {
    //初始化
    initialCookie();
    initialData();

    /******
     * event
     ******/
    //点击表单登录按钮时
    $("#btn_login").click(function () {
        //表单验证
        let username = $.trim($("#input_username").val());
        let password = $.trim($("#input_password").val());
        if(username === "" || password === "") {
            styleUtil.errorShow($("#txt_error_msg"),"请输入用户名和密码");
            return;
        }
        $.ajax({
            url: "/gysc/admin/login/doLogin",
            type:"post",
            data: {"username":username,"password":password},
            success:function (data) {
                $("#btn_login").val("登录");
                if (data.success) {
                    cookieUtil.setCookie("username", username, 7);
                    location.href = "/gysc/admin";
                } else {
                    styleUtil.errorShow($("#txt_error_msg"), "用户名或密码错误!");
                }
            },
            beforeSend:function () {
                $("#btn_login").val("登录中...");
            },
            error:function (data) {
                styleUtil.errorShow($("#txt_error_msg"), "请重新登录!");
            }
        });
    });
    //获得文本框焦点时
    $("#input_username,#input_password").focus(function () {
        //移除校验错误
        const msg = $("#txt_error_msg");
        styleUtil.errorHide(msg);
    });
    //失去用户名文本框焦点时
    $("#input_username").blur(function () {
        getUserProfilePicture($(this).val());
    });
});

//初始化Cookie数据
function initialCookie() {
    let username;
    if(document.cookie.length>0) {
        username = cookieUtil.getCookie("username");
        if(username !== null){
            $("#input_username").val(username);
            getUserProfilePicture(username);
        }
    }
}
//初始化页面数据
function initialData() {
    //表单焦点
    const txt_username = $("#input_username");
    const username = $.trim(txt_username.val());
    if(username !== null && username !== ""){
        $("#input_password").focus();
        return;
    }
    txt_username.focus();
}

//获取用户头像
function getUserProfilePicture(username) {
    if(username !== null && username !== ""){
        $.getJSON("/gysc/admin/login/profile_picture",{"username":username},function (data) {
            if(data.success){
                if(data.srcString !== null){
                    $("#img_profile_picture").attr("src", "/gysc/res/images/item/adminProfilePicture/" + data.srcString);
                }else{
                    $("#img_profile_picture").attr("src","/gysc/res/images/admin/loginPage/default_profile_picture-128x128.png");
                }
            }
        });
    }
}



