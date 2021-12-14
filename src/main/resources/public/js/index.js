layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    
    //监听提交
    form.on("submit(login)", function(data) {
    //获取表单元素的内容
    var fieldData = data.field;
    //用户参数校验
        if(fieldData.username=='undefinded' || fieldData.username==''){
            layer.msg("用户名不能为空");
            return false;
        }
        if(fieldData.password=='undefinded' || fieldData.password==''){
            layer.msg("用户密码不能为空");
            return false;
        }
    //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                "userName":fieldData.username,
                "userPwd":fieldData.password
            },
            dataType:"json",
            success:function (msg){
                //resultInfo结果信息
                if(msg.code==200){
                    // //提示信息
                    // layer.msg("登陆成功！",{icon:5});
                    // //跳转
                    // window.location.href=ctx+"/main";

                    layer.msg("登陆成功了",function (){
                        //将用户的信息存储到cookie
                        $.cookie("userIdStr",msg.result.userIdStr);
                        $.cookie("userName",msg.result.userName);
                        $.cookie("trueName",msg.result.trueName);
                        //如果用户选择记住我 保存七天
                        if ($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr",msg.result.userIdStr,{expires:7});
                            $.cookie("userName",msg.result.userName,{expires:7});
                            $.cookie("trueName",msg.result.trueName,{expires:7});
                        }
                        //跳转页面
                        window.location.href=ctx+"/main";
                    });
                }else {
                    //提示信息
                    layer.msg(msg.msg);
                }
            }
        });
        //取消默认行为
        return false;
    });
});