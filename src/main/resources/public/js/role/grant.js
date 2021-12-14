var treeObj;
$(function () {
    loadModuleInfo();
});


function loadModuleInfo() {
    //发送Ajax查询所有的资源模块信息
    //{id:1,pId:-1,name:zhangs}
    $.ajax({
        type: "post",
        url: ctx + "/module/queryAllModules",
        data:{"roleId":$("#roleId").val()},
        dataType: "json",
        success: function (datas) {
            console.log(datas);
            var setting = {
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                view: {
                    showLine: false
                },
                check: {
                    enable: true
                },
                callback: {
                    onCheck: zTreeOnCheck
                }
            };

            var zNodes = datas;

            $(document).ready(function () {
                 treeObj=  $.fn.zTree.init($("#test1"), setting, zNodes);
            });

        }
    });
}

function zTreeOnCheck(event, treeId, treeNode) {
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    var nodes = treeObj.getCheckedNodes(true);
    var roleId=$("#roleId").val();
    //roleId,mid---t-permission;

    //收集数据
    var mids="mids=";
    //遍历
    for(var x in nodes){
        //判断是否到最后一个元素
        if(x<nodes.length-1){
            mids=mids+nodes[x].id+"&mids="
        }else{
            mids=mids+nodes[x].id;
        }
    }
    /*发送ajaxg添加授权*/
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+roleId,
        dataType:"json",
        success:function(data){
            alert(data.msg);
        }
    });
};
