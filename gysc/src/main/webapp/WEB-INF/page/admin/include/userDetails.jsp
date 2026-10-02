<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script>
        $(function () {
            /******
             * event
             ******/
            //单击取消按钮时
            $("#btn_user_cancel").click(function () {
                $(".menu_li[data-toggle=user]").click();
            });

            //单击保存按钮时
            $("#btn_user_save").click(function () {
                const user_id = $("#details_user_id").val();
                const user_nickname = $.trim($("#input_user_nickname").val());
                const user_realname = $.trim($("#input_user_realname").val());
                const user_gender = $("input[name='user_gender']:checked").val();
                const user_birthday = $.trim($("#input_user_birthday").val());

                // 校验数据
                if (!user_nickname) {
                    alert("昵称不能为空");
                    return;
                }

                const dataList = {
                    "user_nickname": user_nickname,
                    "user_realname": user_realname,
                    "user_gender": user_gender,
                    "user_birthday": user_birthday
                };

                $.ajax({
                    url: "${pageContext.request.contextPath}/admin/user/" + user_id,
                    type: "PUT",
                    data: dataList,
                    success: function (data) {
                        if (data.success) {
                            alert("保存成功！");
                            $(".menu_li[data-toggle=user]").click();
                        } else {
                            alert("保存失败：" + (data.message || "未知错误"));
                        }
                    },
                    error: function () {
                        alert("保存失败，请重试");
                    },
                    beforeSend: function () {
                        $("#btn_user_save").attr("disabled", true).val("保存中...");
                    },
                    complete: function () {
                        $("#btn_user_save").attr("disabled", false).val("保存");
                    }
                });
            });
        });
    </script>
    <style rel="stylesheet">
        #user_profile_picture {
            border-radius: 5px;
        }

        .frm_input {
            width: 300px;
            padding: 5px 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .frm_input:focus {
            outline: none;
            border-color: #FF0036;
        }

        .radio_group {
            display: inline-block;
        }

        .radio_group input[type="radio"] {
            margin-right: 5px;
        }

        .radio_group label {
            margin-right: 15px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="details_div_first">
    <input type="hidden" value="${requestScope.user.user_id}" id="details_user_id"/>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_id">用户编号</label>
        <span class="details_value" id="span_user_id">${requestScope.user.user_id}</span>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_name">用户名</label>
        <span class="details_value" id="span_user_name">${requestScope.user.user_name}</span>
    </div>
</div>
<div class="details_div">
    <span class="details_title text_info">基本信息</span>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_profile_picture">用户头像</label>
        <img
                src="${pageContext.request.contextPath}/res/images/item/userProfilePicture/${requestScope.user.user_profile_picture_src}"
                id="user_profile_picture" width="84px" height="84px"
                onerror="this.src='${pageContext.request.contextPath}/res/images/admin/loginPage/default_profile_picture-128x128.png'"/>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_nickname" for="input_user_nickname">用户昵称</label>
        <input class="frm_input" id="input_user_nickname" type="text" maxlength="50"
               value="${requestScope.user.user_nickname}"/>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_realname" for="input_user_realname">用户姓名</label>
        <input class="frm_input" id="input_user_realname" type="text" maxlength="50"
               value="${requestScope.user.user_realname}"/>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_gender">性别</label>
        <span class="radio_group">
            <input type="radio" name="user_gender" id="radio_gender_male" value="0"
                   <c:if test="${user.user_gender==0}">checked</c:if>>
            <label for="radio_gender_male">男</label>

            <input type="radio" name="user_gender" id="radio_gender_female" value="1"
                   <c:if test="${user.user_gender==1}">checked</c:if>>
            <label for="radio_gender_female">女</label>
        </span>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_user_birthday" for="input_user_birthday">出生日期</label>
        <input class="frm_input" id="input_user_birthday" type="date"
               value="${requestScope.user.user_birthday}"/>
    </div>
</div>
<div class="details_tools_div">
    <input class="frm_btn" id="btn_user_save" type="button" value="保存"/>
    <input class="frm_btn frm_clear" id="btn_user_cancel" type="button" value="取消"/>
</div>
<div class="loader"></div>
</body>
</html>
