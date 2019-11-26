
//表格
var displayType=iFunc.UrlParse().displayType;displayType
var display_Table='';
function display_TableInit() {
  display_Table = $('#displayTable').DataTable({
    // "destroy": true,
     "processing": true,
      "serverSide": true,
	"ajax": {
        "url": 'display_al/mod1/displayConfigs',
        "dataSrc": 'data',
        "data":function(data){
        	data.name=$('.tableSearhInput').val().trim();
        	data.displayType=displayType;
        	data.analysisCategory=$('#display_typeSelect option:selected').val();
        }
    },
	
	"columns": [
	    { "data": null,
	    render:function(){
	    	return ''
	    }},
	    { "data": null,
	    	render:function(data, type, full, meta){
	    		return  '<span class="xyClassFon" onclick="ChangeData('+ meta.row +')">'+data.name+'</span>'
	    	}
	    },
		{ "data": null,
			render:function(data, type, full, meta){
				return '<td onclick="ChangeData('+ meta.row +')" class="inbox-message textCenter analysisTdFunc xyClassFon" >'+(!data.analysisName?"":data.analysisName)+'</td>'
			}
		},
		{ "data": null,
	    	render:function(data, type, full, meta){
	    		return  '<span class="xyClassFon" onclick="ChangeData('+ meta.row +')">'+data.insert_time+'</span>'
	    	}
	    },
		{ "data": null,
			render:function(item){
				return '<td class="inbox-icon textCenter">'+(displayType=='designedFor'||displayType=='specialTopic'?(item.pub_status==0?'<span class="tdOptionBtn mima displayTableShare" data-autoId="'+item.auto_id+'" data-type="1" data-toggle="tooltip" data-placement="bottom" title="公开到小程序"><i class="iconfont icon-gongkai"></i></span>':'<span class="tdOptionBtn mima displayTableShare" data-autoId="'+item.auto_id+'" data-type="0" data-toggle="tooltip" data-placement="bottom" title="取消公开"><i class="iconfont icon-baomi"></i></span>'):'')+
        '<span class="tdOptionBtn del displayTableDel" data-autoId="'+item.auto_id+'" data-toggle="tooltip" data-placement="bottom" title="删除"><i class="iconfont icon-chuyidong"></i></span>'+
        '</td>'
			}
		}
	],
  "fnDrawCallback": function (Settings) {
    // console.log(Settings);
    $('#display_tbodyId [data-toggle="tooltip"]').tooltip();
  },
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
    "ordering": false,
    "autoWidth": false,
    "aaSorting": [ // 清除默认第一列小箭头
      [ 0 ]
    ],
    "order": [[ 3, "desc" ]],
    "columnDefs": [
      { "orderable": false, "targets": [0,4]},
      { "width": "3%", "targets": 0 },
      { "width": "20%", "targets": 1 }
    ],
    "lengthMenu": [ 10 ], // 默认分页选择第一个
    "lengthChange": false, // 分页可调
    "searching": true,
    "dom":'lBrtip', // 不显示搜索框
  });
}
display_TableInit();

// 分析类型选择
function analysisTypeInit() {
  var optionHtml='<option value="" >全部</option>';
  $diaplayCategory.forEach(function (item,i) {
    optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  });
  $('#display_typeSelect').html(optionHtml);

  $('#display_typeSelect').chosen({
    disable_search: true,
    max_selected_options: 1,
    search_contains: true,
    width: '220px'
  });
}
analysisTypeInit();
$('#display_typeSelect').on('change', function(e, params) {
    display_Table.draw(true);
});

$('#displayBox').on('click','#displayModalBtn',function () {
  if(displayType=='designedFor'){
    $('#displayName').val($('#display_searchSelectInput').find('option:selected').text());
  }else {
    $('#displayName').val($('#display_selectInput').find('option:selected').text()).focus();
  } 
});

/**
 * 点击事件
 * @param Row
 * @returns
 */
function ChangeData(Row) {
    let data = $('#displayTable').DataTable().rows(Row).data()[0];
    let type = getPathDisplayType(displayType);
    if (type == 'large-screen') {
		 window.open('display_al/mod2?autoId='+data.auto_id+(type ? "&displayType="+type:"")+"&sid="+data.sid+"&path="+type+".html")
	}else{
		window.location.href = 'display_al/mod2?autoId='+data.auto_id+(type ? "&displayType="+type:"")+"&sid="+data.sid+"&path="+type+".html";
	}
}
/**
 * 根据type获取页面path名称
 * @param type
 * @returns
 */
function getPathDisplayType(type){
	switch (type) {
		case 'report':
			return 'new-report';
			break;
		case 'bigScreen':
			return 'large-screen';
			break;
		default:
			return type;
			break;
	}
}




// 删除
$('#displayBox').on('click','.displayTableDel',function () {
	 var aoid = $(this).attr('data-autoId');
  swal({
    title: "确定删除吗？",
    type: "warning",
    showCancelButton: true,
    confirmButtonColor: "#DD6B55",
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    closeOnConfirm: false
  },
 
  function() {
    swal.close();
		
    $.ajax({
      type: "get",
      url: "display_al/mod1/deleteDisplayConfigs",
      data: {
        autoId:aoid
      },
      beforeSend: function () {
        $('.refresh-preloader').show();
      },
      success: function (data) {
        if(data.status=='ok'){
          $('.refresh-preloader').hide();
          toastr["success"]("删除成功");
         display_Table.draw(false);
        }else{
          $('.refresh-preloader').hide();
          toastr["error"]("删除失败,请刷新重试");
        }
      },
      error: function (res) {
        $('.refresh-preloader').hide();
        toastr["error"]("删除失败,请刷新重试");
      }
    });
  });
  return false;
});

// 公开/私有
$('#displayBox').on('click','.displayTableShare',function () {
  $.ajax({
    type: "get",
    url: "display_al/mod1/updateDisplayConfs",
    data: {
      autoId: $(this).attr('data-autoId'),
      pubStatus: $(this).attr('data-type')
    },
    beforeSend: function () {
      $('.refresh-preloader').show();
    },
    success: function (data) {
      if(data.status=='ok'){
        $('.refresh-preloader').hide();
        toastr["success"]("操作成功，请到小程序查看");
         display_Table.draw(false);
      }else{
        $('.refresh-preloader').hide();
        toastr["error"]("操作失败,请刷新重试");
      }
    },
    error: function (res) {
      $('.refresh-preloader').hide();
      toastr["error"]("操作失败,请刷新重试");
    }
  });
  return false;
});

// input search
animateInputInit('.tableSearhInput','.tableSearhIcon','.tableSearhDelIcon',function () {
  display_Table.draw(true);
},'',function () {
  display_Table.draw(true);
});


// 分析主题选择
function initDisplayChosen() {
  $.ajax({
    type: "get",
    url: "analysis_al/mod1/analysisInfos",
    beforeSend: function () {

    },
    success: function (data) {
      let optionHtml = "";
      data.forEach(function (item,i) {
        optionHtml+="<option value='"+item.auto_id+"' data-sid='"+item.sid+"' data-layout='"+item.layout+"'>"+item.name+"</option>";
      });
      $('#display_selectInput').html(optionHtml);

      $('#display_selectInput').chosen().off('change').on('change',function () {
        $('#displayName').val($(this).find('option:selected').text()).focus();
      });
      
    }
  });
}
initDisplayChosen();
// 头条搜索条件选择
function initSearchChosen() {
  $.ajax({
    type: "get",
    url: "analysis_al/mod1/analysisSearchConfigs",
    beforeSend: function () {

    },
    success: function (data) {
      let optionHtml = "";
      data.forEach(function (item,i) {
        optionHtml+="<option value='"+item.auto_id+"'>"+item.name+"</option>";
      });
      $('#display_searchSelectInput').html(optionHtml);
      $('#display_searchSelectInput').chosen();
    }
  });
}
initSearchChosen();

// 展示选择
$('#addDisplayModalBox').on('click','.modalSelectList',function () {
	if ($(this).find('.file-preview').data('id') == '3') {
		$('input[name="addDispalyDate"]').prop("disabled",true)
		$('input[name="addDispalyDate"][value=""]').prop("checked","checked").prop("disabled",false);
	}else{
		$('input[name="addDispalyDate"]').prop("disabled",false)
	}
  if($(this).find('.file-preview').hasClass('active')){
    // $(this).find('.file-preview').removeClass('active');
  }else {
    $(this).find('.file-preview').addClass('active');
    $(this).siblings('.modalSelectList').find('.file-preview').removeClass('active');
  }
});

// 展示添加
$('#addDisplayModalBox').on('click','#addDisplayBtn',function () {
  var layoutTemp=$('#display_selectInput option:selected').attr('data-layout');
  if(!layoutTemp||layoutTemp==''||layoutTemp=='[]'){
    toastr["info"]("该分析没有分析模块");
    return ;
  }
  var tipText=$("#displayLabelName").text().substring(0,4);
  if($.trim($('#displayName').val())==''){
    toastr["info"](tipText+"不能为空");
    return ;
  }
  var displayType='';
  $('#addDisplayModal').find('.modalSelectList .file-preview').each(function (item,i) {
    //  console.log($(this).attr('data-id'));
    if($(this).hasClass('active')){
      displayType=$(this).attr('data-id');
    }
  });
  if(displayType==undefined||!displayType){
    displayType=iFunc.UrlParse().displayType;
  }
  
  var time = $('input[name="addDispalyDate"]:checked ').val();
  var sTime = '';
  var eTime = '';
  if (!time) {
  	var times = $('#display_date').val().split(' 至 ');
		sTime = times[0]+' 00:00:00';
		eTime = times[1]+' 23:59:59';
  }
  var type = $("#addDisplayModalBox").find("#headlineTypeSelect").val();
  var tags = $("#addDisplayModalBox").find("#headlineTagsInput").val();
  var aid;
  var url='display_al/mod1/addDisplayConfig';
  var autoId;
  if($('#display_searchSelectInput').parents('.topInputBox').is(':visible')){
    autoId=$('#display_searchSelectInput').val();
    url='search_al/mod1/addSearchTopInfo';
  }else {
    aid=$('#display_selectInput').val();
  }
  $.ajax({
    type: "post",
    url: url,
    data: {
      aid: aid,
      autoId: autoId,
      name: $.trim($('#displayName').val()),
      displayType: displayType,
      realTime:time,
      startTime:sTime,
      endTime:eTime,
      analysisType:type,
      tags:tags
    },
    beforeSend: function () {
      $('.refresh-preloader').show();
    },
    success: function (data) {
      if(data.status=='ok'){
      	if ($('#addDisplayModal').find('.modalSelectList .file-preview.active').attr('data-id') == "specialTopic") {
      		updateDisplayConfs(data.autoId,function (result) {
      		if (result == 'ok') {
      			resultOk ()
      		}
      	})
      	}else{
      		resultOk ()
      	}
      	function resultOk () {
      		 $('.refresh-preloader').hide();
       	 $('#addDisplayModal').modal('hide');
       	 toastr["success"]("添加成功");
        	display_Table.draw(true);
        	swal({
          title: "是否跳转到案例",
          type: "warning",
          showCancelButton: true,
          confirmButtonColor: "#DD6B55",
          confirmButtonText: "跳转",
          cancelButtonText: "取消",
          closeOnConfirm: false
        },
        function() {
          swal.close();
          /*if(displayType=='report'){
            window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#display_selectInput').find('option:selected').attr('data-sid')+'&path=new-report.html&displayType=3';
          }else {
            window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#display_selectInput').find('option:selected').attr('data-sid')+'&path='+displayType+'.html';
          }*/
          let type = getPathDisplayType(displayType);
          window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#display_selectInput').find('option:selected').attr('data-sid')+'&path='+type+'.html&displayType='+type;
        });
      	}
       
        
      }else if(data.status=='exist'){
        $('.refresh-preloader').hide();
        $('#addDisplayModal').modal('hide');
        toastr["error"]("添加失败,该"+tipText+"已存在");
      }else{
        $('.refresh-preloader').hide();
        $('#addDisplayModal').modal('hide');
        toastr["error"]("添加失败,请刷新重试");
      }
    },
    error: function (res) {
      $('.refresh-preloader').hide();
      $('#addDisplayModal').modal('hide');
      toastr["error"]("添加失败,请刷新重试");
    }
  });
});
