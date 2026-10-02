$(function () {
    //搜索框验证
    $('form').submit(function () {
        if ($(this).find("input[name='product_name']").val() === "") {
            alert("请输入关键字！");
            return false;
        }
    });
    $(".tbody_checkbox>.cbx_select").click(function () {
        const obj = $(this).parents("tr.orderItem_info").toggleClass("orderItem_selected");
        sumPrice();
    });
    $("#cbx_select_all,#J_SelectAllCbx2").click(function () {
        const yn = $(this).prop("checked");
        const obj = $("tr.orderItem_info");
        if (!yn) {
            $(".tbody_checkbox>.cbx_select").prop("checked", false);
            sumPrice();
            obj.removeClass("orderItem_selected");
            $("#J_Go").removeClass("selected");
        } else {
            $(".tbody_checkbox>.cbx_select").prop("checked", true);
            sumPrice();
            obj.addClass("orderItem_selected");
            $("#J_Go").addClass("selected");
        }
    });
    $(".item_amount>input[type=text]").bind('input propertychange', function () {
        let number = $(this).val();
        if (isNaN(number) || $.trim(number) === "") {
            $(this).val(1);
            $(this).prev("a").addClass("no_minus");
            return;
        }
        if (parseInt(number) >= 500) {
            $(this).val(500);
            number = 500;
            $(this).next("a").addClass("no_minus");
        } else if (parseInt(number) > 1 && parseInt(number) < 500) {
            $(this).prev("a").removeClass("no_minus");
            $(this).next("a").removeClass("no_minus");
        } else if (parseInt(number) <= 1) {
            $(this).val(1);
            number = 1;
            $(this).prev("a").addClass("no_minus");
        }
        let price = $(this).parents("tr").find(".orderItem_product_price").text();
        price = parseFloat(price.substring(1));
        const price_sum = price * parseInt(number);
        $(this).parents("tr").find(".orderItem_product_realPrice").text("￥" + price_sum.toFixed(2));
        sumPrice();
    });
});

function up(obj) {
    obj = $(obj);
    const number = obj.next("input");
    let value = parseInt(number.val());
    if (value > 1) {
        obj.removeClass("no_minus");
    } else {
        obj.addClass("no_minus");
    }
    if (obj.hasClass("no_minus")) {
        return true;
    } else {
        if (isNaN(number.val()) || $.trim(number.val()) === "" || parseInt(number.val()) <= 1) {
            number.val("1");
            obj.addClass("no_minus");
            return true;
        }
        value--;
        if (value < 500) {
            number.next("a").removeClass("no_minus");
        } else {
            number.next("a").addClass("no_minus");
        }
        let price = obj.parents("tr").find(".orderItem_product_price").text();
        price = parseFloat(price.substring(1));
        const price_sum = price * value;
        number.val(value);
        obj.parents("tr").find(".orderItem_product_realPrice").text("￥" + price_sum.toFixed(2));
        if (value === 1) {
            obj.addClass("no_minus");
        }
        sumPrice();
    }
}

function down(obj) {
    obj = $(obj);
    const number = obj.prev("input");
    let value = parseInt(number.val());
    if (value < 500) {
        obj.removeClass("no_minus");
    } else {
        obj.addClass("no_minus");
    }
    if (obj.hasClass("no_minus")) {
        return true;
    } else {
        if (isNaN(number.val()) || $.trim(number.val()) === "" || parseInt(number.val()) < 1) {
            number.val("1");
            return true;
        }
        obj.prevAll(".J_Minus").removeClass("no_minus");
        value++;
        let price = obj.parents("tr").find(".orderItem_product_price").text();
        price = parseFloat(price.substring(1));
        const price_sum = price * value;
        obj.parents("tr").find(".orderItem_product_realPrice").text("￥" + price_sum.toFixed(2));
        number.val(value);
        sumPrice();
    }
}

function sumPrice() {
    let price_sum = 0.00;
    const obj = $("input.cbx_select:checked").parents("tr.orderItem_info");
    obj.each(function () {
        price_sum += parseFloat($(this).find(".orderItem_product_realPrice").text().substring(1));
    });
    $(".total_value").text(price_sum.toFixed(2));

    if (obj.length > 0) {
        $("#J_Go").addClass("selected");
    } else {
        $("#J_Go").removeClass("selected");
    }
    $("#J_SelectedItemsCount").text(obj.length);
}

function remove() {
    const checkedItems = $("input.cbx_select:checked");
    if (checkedItems.length === 0) {
        alert("请先选择要删除的商品！");
        return;
    }
    
    if (!confirm("确定要删除选中的 " + checkedItems.length + " 件商品吗？")) {
        return;
    }
    
    const orderItemIds = [];
    checkedItems.each(function () {
        const orderItemId = $(this).attr("id").replace("cbx_orderItem_select_", "");
        orderItemIds.push(orderItemId);
    });
    
    if (orderItemIds.length === 0) {
        alert("未获取到商品信息，请刷新页面后重试！");
        return;
    }
    
    $.ajax({
        url: "/gysc/orderItem/batch",
        type: "DELETE",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(orderItemIds),
        success: function (data) {
            if (data.success) {
                alert("删除成功！");
                location.reload();
            } else {
                alert(data.message || "删除失败，请稍后重试！");
            }
        },
        error: function () {
            alert("网络请求失败，请稍后再试！");
        }
    });
}

function create(obj) {
    alert("结算功能暂未开放，敬请期待！");
}
