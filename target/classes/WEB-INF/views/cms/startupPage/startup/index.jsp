<%--
  Created by IntelliJ IDEA.
  User: 1553280431@qq.com
  Date: 2017/5/25
  Time: 20:20
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="../../../common/taglib.jsp" %>
<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${webTitle }-图片管理</title>
    <%@ include file="../../../common/header.jsp" %>
</head>

<body class="no-skin">
<%@ include file="../../../common/top.jsp" %>
<div class="main-container" id="main-container">
    <%@ include file="../../../common/menu.jsp" %>
    <div class="main-content">
        <div class="main-content-inner">
            <!-- #section:basics/content.breadcrumbs -->
            <div class="breadcrumbs  breadcrumbs-fixed" id="breadcrumbs">
            </div>
            <!-- /section:basics/content.breadcrumbs -->
            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="widget-box" id="widget-box-search">
                            <div class="widget-header">
                                <div class="widget-toolbar">
                                    <a href="#" data-action="collapse">
                                        <i class="ace-icon fa fa-chevron-up"></i>
                                    </a>
                                </div>
                            </div>
                            <div class="widget-body">
                                <div class="widget-main no-padding">
                                    <form:form action="/" method="post"
                                               cssClass="form-horizontal"
                                               cssStyle="padding-top: 10px;">
                                        <div class="row">
                                            <div class="col-xs-3 hidden">
                                                <div class="form-group">
                                                    <label class="control-label col-xs-6 col-lg-5">类型 ：</label>
                                                    <div class="col-xs-6 col-lg-7">
                                                        <input id="typeSearch" name="typeSearch" value="${type}">
                                                        <%--<select name="typeSearch" id="typeSearch" class="col-sm-12 col-lg-7" title="">--%>
                                                            <%--<option value="" selected="selected">请选择</option>--%>
                                                            <%--<option value="0">启动页</option>--%>
                                                            <%--<option value="1">轮播图</option>--%>
                                                            <%--<option value="2">app分享</option>--%>
                                                            <%--<option value="3">PC侧页</option>--%>
                                                        <%--</select>--%>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-xs-3">
                                                <div class="form-group">
                                                    <label class="control-label col-xs-6 col-lg-5">状态：</label>
                                                    <div class="col-xs-6 col-lg-7">
                                                        <select name="stateSearch" id="stateSearch" class="col-sm-12 col-lg-7" title="">
                                                            <option value="" selected="selected">请选择</option>
                                                            <option value="0">暂不生效</option>
                                                            <option value="1">立即生效</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="col-xs-3">
                                                <div class="form-group">
                                                    <button type="button" class="btn btn-primary btn-sm"
                                                            id="btn-search">
                                                        <i class="ace-icon fa fa-search"></i> 查询
                                                    </button>
                                                    &nbsp;&nbsp;
                                                    <a type="button" class="btn btn-success btn-sm"
                                                       data-backdrop="static" onclick="addTeacher(${type})"
                                                       id="btn-add">
                                                        <i class="ace-icon fa fa-search"></i> 新增
                                                    </a>
                                                    <c:if test="${type==2||type==4}">
                                                    &nbsp;&nbsp;
                                                    <a type="button" class="btn btn-success btn-sm"
                                                       href="#modal-edit" data-toggle="modal"
                                                       data-backdrop="static" onclick="updateImgStatus(${type})"
                                                       id="btn-update">更新图片
                                                    </a>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                        <div class="hr hr-18 dotted hr-double"></div>
                        <div class="row">
                            <div class="col-xs-12">
                                <table id="lwTeacher" style="width: 100%"
                                       class="table table-striped table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th style="background-color: #E5E5E5">图片</th>
                                        <%--<th style="background-color: #E5E5E5">课程名称</th>--%>
                                        <th style="background-color: #E5E5E5">排序</th>
                                        <%--<th style="background-color: #E5E5E5">链接</th>--%>
                                        <th style="background-color: #E5E5E5">类型</th>
                                        <th style="background-color: #E5E5E5">状态</th>
                                        <th style="background-color: #E5E5E5">操作</th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            <!-- /.span -->
                        </div>
                    </div>
                    <!-- /.span -->
                </div>
            </div>
            <!-- /.main-content -->
        </div>
        <!-- /.main-container -->
    </div>
</div>


<div id="modal-edit" class="modal fade" tabindex="-21">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header no-padding">
                <div class="table-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        <span class="white">&times;</span>
                    </button>
                    更新结果
                </div>
            </div>
            <div class="modal-body no-padding">
                <div class="widget-box" style="border: none">
                    <div class="widget-body">
                        <div class="widget-main">
                            <div class="form-group">
                            </div>
                            <form:form action="" id="import-result-form">
                                <div class="form-group">
                                    <div class="col-xs-12" id="result-text">

                                    </div>
                                    <div class="col-xs-12" align="center">
                                        <button class="btn btn-white btn-primary" type="button" id="result-btn">
                                            <i class="ace-icon fa fa-check bigger-110"></i> 确认
                                        </button>
                                    </div>
                                </div>
                            </form:form>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer no-margin-top"></div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<%@ include file="../../../common/js.jsp" %>
<script src="${staticServer }/assets/js/jquery.form.js"></script>
<script src="${staticServer }/assets/js/jquery.validation/jquery.validate.js"></script>
<script src="${staticServer }/assets/js/jquery.validation/jquery.validate.zh-CN.js"></script>
<script src="${staticServer }/assets/components/jquery-ui/jquery-ui.js"></script>


<script>


    function addTeacher(type) {
        window.location.href = "${dynamicServer}/cms/startupPage/toAdd.do?type="+type;
    }

    function updateTeacher(id) {
        window.location.href = "${dynamicServer}/cms/startupPage/toUpdate.do?id=" + id;
    }

    function updateImgStatus(type) {
        if(($("#btn-update").prop("disabled"))){
            return;
        }
//        /*获取按钮点击状态*/
//        var status=btn.attr("disabled", true);
        $.ajax({
            type: "get",
            url: "${dynamicServer}/cms/startupPage/updataImgStatus.do",
            data: {
                "type": type
            },
            contentType: "application/json",
            success: function (data) {
                if(data.status==1){
                    $("#result-text").html(data.message);
                }else{
                    $("#result-text").html(data.message);
                }
            }
        });
        //调用方法
        timer(5);
    }


    /*延时方法*/
    function timer(time) {
        var btn = $("#btn-update");

        btn.html(time <= 0 ? "更新图片" : ("" + (time) + "秒后可点击"));
        var hander = setInterval(function() {
            if (time <= 0) {
                clearInterval(hander); //清除倒计时
                btn.html("更新图片");
                btn.prop("disabled", false);
                return false;
            }else {
                btn.prop("disabled", true); //按钮禁止点击
                btn.html("" + (time--) + "秒后可点击");
            }
        }, 1000);
    }

    $("#result-btn").on('click', function () {
        $("#modal-edit").modal("hide");
    });
</script>
<script>


    $(function () {

        /*把要初始化的table的对象赋值*/
        var $table_id = $("#lwTeacher");

        /*自定义查询参数*/
        var userParam = {
            getQueryCondition: function (data) {
                var param = {};
                param.type = $("#typeSearch").val();
                param.state = $("#stateSearch").val();

                param.page = data.start;
                param.length = data.length;
                return param;
            }
        };
        //object可以如下初始化表格
        window.table = $table_id.DataTable({
            dom: '<".table_area_top" B and >t<".table_area_bottom" pi>',
            Filter: false, //列筛序功能
            searching: false,//本地搜索
            ordering: false, //排序功能
            Info: true,//页脚信息
            lengthMenu: [15],
            scrollY: "auto",
            width: "auto",
            Paginate: true, //翻页功能
            sortable: false,
            processing: true,
            serverSide: true,
            ajax: function (data, callback) {//ajax配置为function,手动调用异步查询
                $.ajax({
                    type: "GET",
                    url: '${dynamicServer}/cms/startupPage/getList.do',//请求数据的参数
                    data: userParam.getQueryCondition(data),
                    cache: false,  //禁用缓存
                    dataType: "json",
                    success: function (result) {

                        callback(result);
                    }
                });
            },

            //使用对象数组，一定要配置columns，告诉 DataTables 每列对应的属性
            //data 这里是固定不变的，id，name，age，sex等为你数据里对应的属性
            columns: [

                {
                    data: "imgUrl",
                    render: function (data) {
//                        var img = '<img src="http://www.932edu.net/lwgk/upload' + data + '"  alt="图像" style="height: 150px;width: 150px" />';
                        var img = '<img src="${imageServer}' + data + '"  alt="图像" style="height: 150px;width: 150px" />';

                        return img || "";
                    }
                },
//                {
//                    data: "courseName",
//                    render: function (data) {
//                        return data || "无";
//                    }
//                },
                {
                    data: "sort",
                    render: function (data) {
                        return data || "无";
                    }
                },
//                {
//                    data: "url",
//                    render: function (data) {
//                        return data || "无";
//                    }
//                },
                {
                    data: "type",
                    render: function (data) {
                        var state = "";
                        switch (data) {
                            case 0:
                                state = "启动页";
                                break;
                            case 1:
                                state = "轮播图";
                                break;
                            case 2:
                                state = "app分享";
                                break;
                            case 3:
                                state = "PC侧页";
                                break;
                            case 4:
                                state = "app轮播图";
                                break;
                            default :
                                state = "启动页";
                                break;
                        }
                        return state;
                    }
                },
                {
                    data: "state",
                    render: function (data) {
                        var state = "";
                        switch (data) {
                            case 0:
                                state = "暂不生效";
                                break;
                            case 1:
                                state = "马上生效";
                                break;
                            default :
                                state = "暂不生效";
                                break;
                        }
                        return state;
                    }
                },


                {
                    data: "id",
                    width: "155px",
                    render: function (data, type, row) {
                        return '<a class="btn  btn-sm btn-info" ' +
                            'onclick="updateTeacher(\'' + data + '\')">' +
                            '<i class="ace-icon fa fa-pencil-square-o "></i>修改</a>' +
                            '&nbsp;&nbsp;' +
                            '<a class="btn btn-sm btn-danger" onclick="delUser(\'' + data + '\')">' +
                            '<i class="ace-icon fa fa-pencil-square-o "></i>删除</a>';
                    }
                }
            ],
            language: { //国际语言转化
                aria: {
                    sortAscending: " - click/return to sort ascending",
                    sortDescending: " - click/return to sort descending"
                },
                lengthMenu: "显示 _MENU_ 记录",
                zeroRecords: "对不起，查询不到任何相关数据",
                emptyTable: "未有相关数据",
                loadingRecords: "正在加载数据-请等待...",
                info: "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
                infoEmpty: "当前显示0到0条，共0条记录",
                infoFiltered: "（数据库中共为 _MAX_ 条记录）",
                processing: " 正在加载数据...",
                //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
                paginate: {
                    first: "首页",
                    previous: " 上一页 ",
                    next: " 下一页 ",
                    last: " 尾页 "
                }
            }
        });
        /*条件查询*/
        $("#btn-search").on("click", function () {
            table.draw();
        });

        /*添加手机号验证方法*/
        jQuery.validator.addMethod("phone", function (value, element) {
            var length = value.length;
            var mobile = /^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$/;
            return this.optional(element) || (length == 11 && mobile.test(value));
        }, "请正确填写您的手机号码");

        /*validate验证*/
        $("#lwTeacherForm").validate({
            errorElement: "label",
            errorClass: "validError",
            errorPlacement: function (error, element) {
                error.appendTo($("#" + element.attr('id') + "Tip"));
            },
            rules: {
                sort: {
                    required: true,
                    maxlength: 255
                }

            },
            submitHandler: function (form) {
                $("#lwTeacherForm").ajaxSubmit({
                    success: function () {
                        $('#modal-edit').modal('hide');
                        table.draw();
                    }
                });
            }
        });
    })
    ;

    /*新增时清空表单数据*/
    function flushForm() {
        $("#lwTeacherForm").find("input").each(function () {
            var id = $(this).attr("id");
            $("#" + id).val("");
        });
        $.ajax({
            type: "get",
            url: "${dynamicServer}/cms/startupPage/findAll.do",
            contentType: "application/json",
            success: function (data) {

            }
        })
    }
    /*修改时需要回显数据*/
    function updateUser(id) {
        flushForm();
        $.ajax({
            type: "GET",
            url: "${dynamicServer}/cms/startupPage/findOne.do",
            data: {
                "id": id
            },
            contentType: 'application/json',
            success: function (data) {
                for (var key in data) {
                    $("#" + key).val(data[key]);
                }
            }
        });
    }

    /*删除*/
    function delUser(id) {
        bootbox.confirm("<a style='font-size: 17px;color: red'>确定要删除该用户信息？</a>", function (result) {
            if (result) {
                $.ajax({
                    type: "GET",
                    url: "${dynamicServer}/cms/startupPage/delete.do",
                    data: {
                        "id": id
                    },
                    contentType: 'application/json',
                    success: function () {
                        table.draw();
                    }
                });
            }
        });
    }



</script>
</body>
</html>


