// 
function tagTypeInit() {
  // var optionHtml='';
  // $analysisCategory.forEach(function (item,i) {
  //   optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  // });
  // $('#analysisType').html(optionHtml);

  $('#tag_typeSelect').chosen();
  $("#tag_levelSelect").chosen();
}
tagTypeInit();

var tag_Table='';
function tag_TableInit() {
	var param = getTagInfoSearchParam();
	
//	param = JSON.parse( param);
  tag_Table = $('#tagTable').DataTable({
      "processing": true,
      "serverSide": true,
	"ajax": {
        "url": 'tag_al/mod1/getTagJsonList',
        "dataSrc": 'data',
        "data":param
    },
	
	"columns": [
	    { "data": null },
		{ "data": 'name' },
		{ 
      "data": 'tag_level',
      render:function(data, type, full, meta){
        return  data==1?'一级':data==2?'二级':data==3?'三级':data==4?'四级':data==4?'五级':data==4?'六级':'';
      }
    },
		{ "data": 'tag_type' },
		{ "data": 'note' },
//	    { "data": 'auto_id' }
	],
	"ordering": false,
	"columnDefs": [
	  	  {
	  	    "targets": [0],
	  	    "render": function(data, type, full) {
	  	      return "";
	  	    }
	  	  },
	  	  {
	           "targets": [3],
	           "data":"tag_type",
	           "render": function(data, type, full) {
	        	   if (1==data) {
	        		   return "新闻标签";
	        	   } else {
	        		   return "其他";
	        	   }
	           }
	         },
         /*{
           "targets": [5],
           "data":"auto_id",
           "render": function(data, type, full) {
             return "<span data-toggle='modal' data-target='#delTagModal' class='tdOptionBtn del delTag' data-auto-id='"+data+"' data-toggle='tooltip' data-placement='bottom' title='取消分享'><i class='iconfont icon-chuyidong'></i></span>";
           }
         },*/
         { "orderable": false, "targets": [0,4]},
         { "width": "3%", "targets": 0 },
         { "width": "20%", "targets": 1 }
      ],
    "language": {
      "sProcessing": "处理中...",
      "sLengthMenu": "显示 _MENU_ 项结果",
      "sZeroRecords": "没有匹配结果",
      "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
      "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
      "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
      "sInfoPostFix":  "",
      "sSearch": "搜索:",
      "sUrl": "",
      "sEmptyTable": "暂无数据",
      "sLoadingRecords": "载入中...",
      "sInfoThousands": ",",
      "oPaginate": {
        "sFirst": "首页",
        "sPrevious": "上页",
        "sNext": "下页",
        "sLast": "末页"
      },
      "oAria": {
        "sSortAscending": ": 以升序排列此列",
        "sSortDescending": ": 以降序排列此列"
      }
    },
    // "ordering": false,
    "autoWidth": false,
    "aaSorting": [ // 清除默认第一列小箭头
      [ 0 ]
    ],
    //"order": [[ 3, "desc" ]],
    /*"columnDefs": [
      { "orderable": false, "targets": [0,5]},
      { "width": "3%", "targets": 0 },
      { "width": "20%", "targets": 1 }
    ],*/
    "lengthMenu": [ 10 ], // 默认分页选择第一个
    "lengthChange": false, // 分页可调
    "searching": true,
    "dom":'lBrtip', // 不显示搜索框
  });
}
tag_TableInit();

// 表格
function initTable() {
  $.ajax({
    type: "get",
    url: "tag_al/mod1/getTagList",
    data: {
      'analysisType':$('#analysis_typeSelect').val()
    },
    beforeSend: function () {
      $('#analysisTable_wrapper').hide();
      $('.refresh-preloader').show();
    },
    success: function (data) {
      tag_Table.destroy(false);
      $('#tagBox_tbodyId').html(data);
      tag_TableInit();
      $('#analysisTable_wrapper').show();
      $('.refresh-preloader').hide();
    }
  });
  
}
// initTable();

animateInputInit('.tableSearhInput','.tableSearhIcon','.tableSearhDelIcon',function () {
  tag_Table.search($('.tableSearhInput').val().trim()).draw();
},'',function () {
  tag_Table.search($('.tableSearhInput').val().trim()).draw();
});

function tagInfoSearchButtonInit(){
	$("#tag_info_search_button").unbind("click").click(function(){
		if (null!=tag_Table) {
			tag_Table.destroy(false);
		}
		tag_TableInit();
	});
}
function tagSelectInit() {
	var $container = $(".searchElementContainer");
	$container.find('#tag_nameInput').bind('keypress',function(event){ 
        if(event.keyCode == 13) {  
        	$("#tag_info_search_button").click();
        }  
    });
	$container.find("#tag_levelSelect").unbind("change").change(function(){
		$("#tag_info_search_button").click();
	});
	$container.find("#tag_typeSelect").unbind("change").change(function(){
		$("#tag_info_search_button").click();
	});
}

tagInfoSearchButtonInit();
tagSelectInit();

function getTagInfoSearchParam(){
	var $container = $(".searchElementContainer");
	var data = {};
	data.name = $.trim($container.find("#tag_nameInput").val());
	data.tagLevel = $container.find("#tag_levelSelect").val();
	data.tagType = $container.find("#tag_typeSelect").val();
	return data;
}
