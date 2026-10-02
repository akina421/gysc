$(function () {
    //登录验证
    $(".loginForm").submit(function () {
        let yn = true;
        $(this).find(":text,:password").each(function () {
            if ($("#name").val() === "") {
                styleUtil.errorShow($("#error_message_p"), "请输入用户名！");
                yn = false;
                return yn;
            }
            if ($("#password").val() === "") {
                styleUtil.errorShow($("#error_message_p"), "请输入密码！");
                yn = false;
                return yn;
            }
        });

        if (yn) {
            $.ajax({
                type: "POST",
                url: "/gysc/login/doLogin",
                data: {"username": $.trim($("#name").val()), "password": $.trim($("#password").val())},
                dataType: "json",
                success: function (data) {
                    $(".loginButton").val("登 录");
                    if (data.success) {
                        location.href = "/gysc";

                    } else {
                        styleUtil.errorShow($("#error_message_p"), "用户名和密码错误！");
                    }
                },
                error: function (data) {
                    $(".loginButton").val("登 录");
                    styleUtil.errorShow($("#error_message_p"), "服务器异常，请刷新页面再试！");
                },
                beforeSend: function () {
                    $(".loginButton").val("正在登录...");
                }
            });
        }
        return false;
    });
  /*  $(".loginForm :text,.loginForm :password").focus(function () {
        styleUtil.errorHide($("#error_message_p"));
    });*/
});
