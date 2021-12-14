layui.use(['form', 'jquery', 'jquery_cookie', 'layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on("submit(saveBtn)", function (data) {
        $.ajax({
            type: "post",
            url: ctx + "/user/setting",
            data: {
                userName: data.field.userName,
                phone: data.field.phone,
                email: data.field.email,
                trueName: data.field.trueName,
                id: data.field.id
            },
            dataType: "json",
            success: function (msg) {
                if (msg.code == 200) {
                    layer.msg("保存成功", function () {
                        $.removeCookie("userIdStr", {domain: "localhost"}, {path: "/crm"})
                        $.removeCookie("userName", {domain: "localhost"}, {path: "/crm"})
                        $.removeCookie("trueName", {domain: "localhost"}, {path: "/crm"})
                        window.parent.location.href = ctx + "/index";
                    });
                } else {
                    //修改失败
                    layer.msg(msg.msg);
                }
            }
        });
        return false;
    });
});