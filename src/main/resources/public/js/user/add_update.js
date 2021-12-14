layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
        formSelects = layui.formSelects;

    //添加|更新用户
    form.on("submit(addOrUpdateUser)",function (data){
        //判断添加还是修改
        var url = ctx+"/user/save";
        if ($("input[name=id]").val()){
            url = ctx+"/user/update";
        }
        //发送ajax
        $.post(url,data.field,function (result){
            if (result.code==200){
                //刷新
                parent.location.reload();
            }else {
                layer.msg(result.msg);
            }
        },"json")
        //取消默认跳转
        return false;
    })

    //关闭弹出层
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    formSelects.config('selectId',{
        type:'post',
        searchUrl:ctx+'/role/findRoles?userId='+$("input[name=id]").val(),
        keyName: 'roleName',
        keyVal: 'id',
    },true);
});