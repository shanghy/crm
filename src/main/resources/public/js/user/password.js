layui.use(['form', 'jquery', 'jquery_cookie', 'layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on("submit(saveBtn)", function (data) {
        var filedData=data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePwd",
            data:{
                oldPwd:filedData.old_password,
                newPwd:filedData.new_password,
                confirmPwd:filedData.again_password
            },
            dataType:"json",
            success:function (data){
                if(data.code == 200){
                    layer.msg("修改密码成功了,系统三秒后退出",function (){
                        //清空cookie
                        $.removeCookie("userIdStr",{domain:"localhost"},{path:"/crm"})
                        $.removeCookie("userName",{domain:"localhost"},{path:"/crm"})
                        $.removeCookie("trueName",{domain:"localhost"},{path:"/crm"})
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    });
                }else {
                    layer.msg(data.msg);
                }
            }
        })
        //取消默认行为
        return false;
    });
});