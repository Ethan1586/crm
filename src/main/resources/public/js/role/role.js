layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function(){
        tableIns.reload({
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                roleName: $("input[name='roleName']").val()
            }
        })
    });

    //头部工具栏
    //触发事件
    table.on('toolbar(roles)', function(obj){
      var checkStatus = table.checkStatus(obj.config.id);
      switch(obj.event){
        case 'add':
            openAddOrUpdateRoleDialog();
          //layer.msg('添加');
        break;
        case 'grant':
            openAddGrantDialog(checkStatus.data);
          //layer.msg('授权');
        break;
      }
    });

    function openAddGrantDialog(datas) {
        //校验
        if (datas.length==0){
            layer.msg("请选择要授权的数据");
            return;
        }
        if (datas.length>1){
            layer.msg("暂不支持批量授权")
            return;
        }
        var title = "<h2>角色模块--授权<h2/>";
        var url = ctx + "/role/toRoleGrantPage?roleId="+datas[0].id;
        layui.layer.open({
            title:title,
            type:2,
            area: ["600px","280px"],
            maxmin: true,
            content: url
        });
    }



    function openAddOrUpdateRoleDialog(userId){
        var url = ctx + "/role/addOrUpdateRolePage";
        var title = "<h2>角色模块--添加<h2/>"
        //判断是添加还是修改
        if (userId){
            url = url + "?userId=" + userId;
            title = "<h2>角色模块--更新<h2/>"
        }
        layui.layer.open({
            title:title,
            type: 2,
            area:["600px","280px"],
            maxmin:true,
            content:url
        });

    }

    //行内工具栏
    //工具条事件
    table.on('tool(roles)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
      var data = obj.data; //获得当前行数据
        if(obj.event === 'del'){ //删除
        layer.confirm('真的删除行么', function(index){
          //发送ajax
            $.post(ctx+"/role/delete", {id:obj.data.id},function (result){
                //校验
                if (result.code==200){
                    //删除
                    layer.msg("删除成功")
                    //重载
                    tableIns.reload();
                }else {
                    layer.msg(result.msg);
                }
            },"json");
          layer.close(index);
          //向服务端发送删除指令
        });
      } else if(obj.event === 'edit'){ //编辑
            openAddOrUpdateRoleDialog(obj.data.id);
          //layer.msg("编辑")
      }
    });
});