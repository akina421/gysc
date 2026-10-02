//Cookie工具
var cookieUtil = {
    //设置Cookie
    setCookie:
        function (name,value,days) {
            var date = new Date();
            date.setDate(date.getDate() + days);
            document.cookie = name + "=" + decodeURI(value) + ";expires=" + date.toUTCString();
            return this;
        },
    //获取Cookie
    getCookie:
        function (name) {
            var arr;
            var reg = new RegExp("(^| )" + name + "=([^;]*)($|;)");
            if (arr = document.cookie.match(reg)) {
                return decodeURIComponent(arr[2]);
            }
            return null;
        },
    //移除Cookie
    removeCookie:
        function (name) {
            cookieUtil.setCookie(name, "", -1);
            return this;
        }
};
//样式统一工具
var styleUtil = {
    //显示表单验证错误提示
    errorShow:
        function (obj,text) {
            if (obj !== undefined) {
                obj.text(text).attr("title", text);
                if (obj.css("opacity") !== "1") {
                    obj.animate({
                        left: "0",
                        opacity: 1
                    }, 200);
                } else {
                    obj
                        .css("opacity", "0.5")
                        .animate({
                            opacity: 1
                        }, 100);
                }
                return this;
            }
        },
    //隐藏表单验证错误提示
    errorHide:
        function (obj) {
            if (obj !== undefined) {
                if (obj.css("opacity") !== "0") {
                    obj.animate({
                        left: "20px",
                        opacity: 0
                    }, 200);
                }
                return this;
            }
        },
    //显示基础的表单验证错误提示
    basicErrorShow:
        function (obj) {
            obj
                .css("color","#c33")
                .css("opacity", "0.5")
                .animate({
                    opacity: 1
            }, 100)
                .next("input,textarea")
                .css("border-color", "#c33");
            return this;
        },
    //显示一种特殊的基础表单验证错误提示
    specialBasicErrorShow:
        function (obj) {
            obj
                .css("color", "#c33")
                .css("opacity", "0.5")
                .animate({
                    opacity: 1
                }, 100)
                .next("span").next("input,textarea")
                .css("border-color", "#c33");
            return this;
        },
    //隐藏基础的表单验证错误提示
    basicErrorHide:
        function (obj) {
            obj
                .css("color","#666")
                .css("opacity","1")
                .next("input,textarea")
                .css("border-color", "");
            return this;
        },
    //隐藏一种特殊的基础表单验证错误提示
    specialBasicErrorHide:
        function (obj) {
            obj
                .css("color", "#333")
                .css("opacity", "1")
                .next("span")
                .next("input,textarea")
                .css("border-color","");
            return this;
        }
};
//页面和数据交互工具
var ajaxUtil = {
    getPage: function (url, data, isChild) {
        if(url !== null && url !== ""){
            $.ajax({
                url: "/gysc/admin/" + url,  // 完整URL：/gysc/admin/product/123
                type: "get",
                data: data,
                contentType: "text/html;charset=UTF-8",
                success : function (data) {
                    // ⭐ 核心：将返回的HTML内容插入到主内容区域
                    $("#div_home_context_main").html(data);

                    // 滚动到页面顶部
                    window.scrollTo(0, 0);

                    if(!isChild){
                        // 绑定通用事件（全选框、重置按钮等）
                        // ...
                    } else {
                        // 清除已选中的菜单项样式
                        $(".menu_li_select").removeClass("menu_li_select");
                    }
                },
                beforeSend: function () {
                    // 显示加载动画
                    $(".loader").css("display","block");
                },
                error: function () {
                    // 错误处理
                }
            });
        }
    }
};
