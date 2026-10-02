<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script>
        let myChart;
        $(function () {
            //设置日期控件约束
            /******
             * event
             ******/
            //点击统计数据时
            $("#productTotal").click(function () {
                $(".menu_li[data-toggle=product]").click();
            });
            $("#userTotal").click(function () {
                $(".menu_li[data-toggle=user]").click();
            });
        });


        /**
         * @return {string}
         * */
    </script>
    <style rel="stylesheet">
        #chartByDate {
            padding: 0;
            margin-bottom: 40px;
        }

        #chartByDate > li {
            display: inline-block;
            list-style: none;
        }

        #chartByDate > li > .frm_input {
            color: #999;
            height: 29px;
            cursor: pointer;
        }

        #chartTotal {
            width: 100%;
            margin-bottom: 30px;
            padding: 0 20px;
        }

        #chartTotal > li {
            width: 35%;
            display: inline-block;
            list-style: none;
            padding: 0 40px;
            cursor: pointer;
        }

        #chartTotal > li + li {
            border-left: 2px solid #eee;
        }

        #chartTotal > li:first-child {
            width: 31.5%;
            padding-left: 0;
        }

        #chartTotal > li:last-child {
            width: 31.5%;
            padding-right: 0;
        }

        #chartTotal .chartTotalTitle {
            height: 20px;
            text-indent: 3px;
        }

        .chartTotalTitle .chartTitleText {
            float: left;
            font-weight: bold;
            font-size: 14px;
            color: #666;
        }

        .chartTotalTitle .chartTitleUnit {
            float: right;
            font-weight: bold;
            font-size: 14px;
            color: #666;
        }

        #chartTotal .chartTotalValue {
            font-size: 28px;
            font-weight: bold;
            color: #666;
        }

        #chartTotal .chartTotalStyle {
            width: 100%;
            height: 6px;
            border-radius: 10px;
            margin-top: 10px;
        }

        .chartDateBtn {
            display: inline-block;
            padding: 5px 10px;
            color: #999;
            margin: 0 20px 0 0;
            border-radius: 3px;
            border: 1px solid #e9ebef;
            font-size: 12px;
            cursor: pointer;
        }

        span.chartDateBtn.select {
            background: #70BBF4;
            border: 1px solid #70BBF4;
            color: white;
        }

        .chartDateInput {
            color: #999;
            outline: none;
            border: 0;
        }

        input[type=date]::-webkit-inner-spin-button {
            display: none
        }

        input[type=date]::-webkit-clear-button {
            display: none
        }

        #btn_chart_search {
            position: relative;
            top: 1px;
        }

        .split {
            color: #999;
            padding-right: 10px;
        }

        #chartDiv {
            border: 1px solid #eee;
            padding: 20px;
        }

    </style>
</head>
<body>
<ul id="chartTotal">
    <li id="productTotal">
        <p class="chartTotalTitle"><span class="chartTitleText">上架产品数量</span><span class="chartTitleUnit">(单位：件)</span>
        </p>
        <span class="chartTotalValue">${requestScope.productTotal}</span>
        <div class="chartTotalStyle" style="background-color: #f89e9e"></div>
    </li>
    <li id="userTotal">
        <p class="chartTotalTitle"><span class="chartTitleText">注册用户数量</span><span class="chartTitleUnit">(单位：人)</span>
        </p>
        <span class="chartTotalValue">${requestScope.userTotal}</span>
        <div class="chartTotalStyle" style="background-color: #9ea7f5"></div>
    </li>
</ul>
</body>
</html>
