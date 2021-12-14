layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on("submit(saveBtn)", function(data) {
    // 获取表单元素的内容
    var fieldData = data.field;
    //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/setting",
            data:{
                id:fieldData.id,
                trueName:fieldData.trueName,
                email:fieldData.email,
                phone:fieldData.phone,
                userName:fieldData.userName
            },
            dataType:"json",
            success:function (data){
                //resultInfo结果信息
                if(data.code==200){
                    layer.msg("保存成功了,3秒后退出",function (){
                        //清除cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    });
                }else {
                    //提示信息
                    layer.msg(data.msg);
                }
            }
        });
        //取消默认行为
        return false;
    });
});