$(function () {
    $(".J_GoodsDetails").addClass("tab-selected");
    $(".context_img_li").eq(0).addClass("context_img_li_hover");

    //搜索框验证
    $('form').submit(function () {
        if ($(this).find("input[name='product_name']").val() === "") {
            alert("请输入关键字！");
            return false;
        }
    });
    //移入预览图片列表时
    $(".context_img_li").mouseenter(function () {
        const img = $(this).children("img");
        $(".context_img_main").attr("src", img.attr("src"));
        $(".context_img_ks").children("img").attr("src", img.attr("src"));
        $(".context_img_li").removeClass("context_img_li_hover");
        $(this).addClass("context_img_li_hover");
    });
    //产品数量框验证
    $(".amount_value_up").click(function () {
        let number = parseInt($(".context_buymember").val());
        number++;
        $(".context_buymember").val(number);
    });
    $(".amount_value-down").click(function () {
        let number = parseInt($(".context_buymember").val());
        if (number > 1) {
            number--;
            $(".context_buymember").val(number);
        }
    });
    $(".context_buymember").on("input", function () {
        if ($(this).val() === "") {
            $(this).val(1);
        }
        if (parseInt($("#stock").val()) < parseInt($(this).val())) {
            $(".context_buyNow").addClass("context_notBuy").attr("disabled", "disabled");
            $(".context_addBuyCar").addClass("context_notCar").attr("disabled", "disabled");
        } else {
            $(".context_buyNow").removeClass("context_notBuy").attr("disabled", null);
            $(".context_addBuyCar").removeClass("context_notCar").attr("disabled", null);
        }
    });
    //模态窗口登录
    $(".loginForm").unbind("submit").submit(function () {
        let yn = true;
        $(this).find(":text,:password").each(function () {
            if ($.trim($(this).val()) === "") {
                styleUtil.errorShow($("#error_message_p"), "请输入用户名和密码！");
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
                        location.reload();
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
    //关闭模态窗口
    $(".closeLoginDiv").click(function () {
        $(".loginModel").hide();
        $(".loginDiv").hide();
    });
});

function getDetailsPage(obj, className) {
    $(".J_TabBarBox").find("li").removeClass("tab-selected");
    $(obj).parent("li").addClass("tab-selected");
    $(".J_choose").children("div").hide();
    $("." + className).show();
}
