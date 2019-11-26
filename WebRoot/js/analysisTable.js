
var analysis_Table='';
var analysisType='';
function analysis_TableInit() {
//	$('#analysis_tbodyId').html("");
  analysis_Table = $('#analysisTable').DataTable({
    // "destroy": true,
    "processing": true,
      "serverSide": true,
	"ajax": {
        "url": 'analysis_al/mod1/analysisInfos',
        "dataSrc": 'data',
        "data":function(data){
        	data.name=$('.tableSearhInput').val().trim();
        	data.analysisType = $('#analysis_typeSelect option:selected').val();
        }
//        },
//        "data":{name:$('.tableSearhInput').val(),"analysisType":$('#analysis_typeSelect option:selected').val()}
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
				return '<td onclick="ChangeData('+ meta.row +')" class="inbox-message textCenter analysisTdFunc xyClassFon" >'+(!data.analysis_display?'<i class="iconfont icon-diannao" style="cursor:default;" data-toggle="tooltip" data-placement="bottom" title="未发布"></i>':'<i class="iconfont icon-diannao" style="color: #01c0c8; cursor:default;" data-toggle="tooltip" data-placement="bottom" title="案例名称：'+data.name+'"></i>')+'</td>'
			}
		},
		{ "data": null,
	    	render:function(data, type, full, meta){
	    		return  '<span class="xyClassFon" onclick="ChangeData('+ meta.row +')">'+data.insert_time+'</span>'
	    	}
	    },
		{ "data": null,
			render:function(data){
				return "<td class='inbox-icon textCenter'>"+
        // '<div class="btn-group"><a data-toggle="dropdown" href="#" class="" aria-expanded="false"><svg t="1526958579337" class="icon" style="fill:#01c0c8;" viewBox="0 0 5120 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2393" xmlns:xlink="http://www.w3.org/1999/xlink" width="24" height="24"><defs><style type="text/css"></style></defs><path d="M3963.904 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512zM1981.44 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512z" fill="" p-id="2394"></path><path d="M1981.952 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512zM0 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512C221.696 0 0 229.888 0 512z" fill="" p-id="2395"></path></svg></a><ul class="dropdown-menu dropdown-menu-right"><li><a href="" data-autoId="'+item.auto_id+'" data-name="'+item.name+'" class="analysisTableAdd">发布</a></li><li><a class="analysisTableDel" data-autoId="'+item.auto_id+'" data-analysis-display="'+item.analysis_display+'" href="#">删除</a></li></ul></div>'+

        "<span class='tdOptionBtn add1 analysisTableAdd' data-autoId='"+data.auto_id+"' data-sid='"+data.sid+"' data-layout='"+data.layout+"' data-name='"+data.name+"' data-toggle='tooltip' data-placement='bottom' title='发布'><i class='iconfont icon-fabu1'></i></span>"+
        "<span class='tdOptionBtn tableCopyBtn copy' data-autoId='"+data.auto_id+"' data-toggle='tooltip' data-placement='bottom' title='复制'><i class='iconfont icon-fuzhi'></i></span>"+
        "<span class='tdOptionBtn del analysisTableDel' data-autoId='"+data.auto_id+"' data-analysis-display='"+data.analysis_display+"' data-toggle='tooltip' data-placement='bottom' title='删除'><i class='iconfont icon-chuyidong'></i></span>"+
        "</td>"
			}
		}
	],
  "fnDrawCallback": function (Settings) {
    // console.log(Settings);
    $('#analysis_tbodyId [data-toggle="tooltip"]').tooltip();
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
    "bStateSave" : true,
    // "order": [[ 3, "desc" ]],
    "columnDefs": [
      { "orderable": false, "targets": [0,2,4]},
      { "width": "3%", "targets": 0 },
      { "width": "20%", "targets": 1 }
    ],
    "lengthMenu": [ 10 ], // 默认分页选择第一个
    "lengthChange": false, // 分页可调
    "searching": true,
    "dom":'lBrtip', // 不显示搜索框
  });
   
}
analysis_TableInit();
function ChangeData(Row) {
      var data = $('#analysisTable').DataTable().rows(Row).data()[0]
      window.location.href = 'analysis_al/mod2?autoId='+data.auto_id+(analysisType ? "&analysisType="+analysisType:"");
 };

// 分析类型选择
function analysisTypeSelectInit() {
  var optionHtml='<option value="" >全部</option>';
  $analysisCategory.forEach(function (item,i) {
    optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  });
  $('#analysis_typeSelect').html(optionHtml);

  $('#analysis_typeSelect').chosen();
}
analysisTypeSelectInit();
$('#analysis_typeSelect').on('change', function(e, params) {
  analysisType=$('#analysis_typeSelect').val();
  analysis_Table.draw(true);
});

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


// 分析类型选择
function analysisTypeInit() {
  var optionHtml='<option value="">全部</option>';
  $analysisCategory.forEach(function (item,i) {
    optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  });
  $('#analysisType').html(optionHtml);

  $('#analysisType').chosen();
}


// 表格
var tableSuccessData = {};
// 
$('#analysisBox').on('click','.analysisTdFunc',function () {
  window.location.href = 'analysis_al/mod2?autoId='+$(this).parent('tr').attr('data-auto-id')+(analysisType ? "&analysisType="+analysisType:"");
});
// 删除 
$('#analysisBox').on('click','.analysisTableDel',function () {
  // console.log($(this).attr('data-analysis-display'));
  if(!$(this).attr('data-analysis-display')||$(this).attr('data-analysis-display')=='undefined'){
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
        url: "analysis_al/mod1/deleteAnalysisSearchConfig",
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
            analysis_Table.draw(false);
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
  }else {
    toastr["info"]("该分析存在案例，不能直接删除");
  }
  
  return false;
});
// 复制
$('#analysisBox').on('click','.tableCopyBtn',function () {
  // console.log($(this).attr('data-autoId'));
  $('.refresh-preloader').show();
  $.ajax({
    type: "get",
    url: "analysis_al/mod1/copyAnalysisConfig",
    data: {
      autoId: $(this).attr('data-autoId')
    },
    success: function (data) {
      $('.refresh-preloader').hide();
      if(data.status=='ok'){
        toastr["success"]("复制成功");
        analysis_Table.draw(false);
//        debugger;
//        analysis_TableInit();
      }else{
        toastr["error"]("复制失败,请刷新重试");
      }
    },
    error: function (res) {
      $('.refresh-preloader').hide();
      toastr["error"]("复制失败,请刷新重试");
    }
  });
});

// input search
animateInputInit('.tableSearhInput','.tableSearhIcon','.tableSearhDelIcon',function () {
  analysis_Table.draw(true);
},'',function () {
  analysis_Table.draw(true);
});

// 主题选择
function initAnalysisChosen() {
  $.ajax({
    type: "get",
    url: "analysis_al/mod1/analysisSearchConfigs",
    beforeSend: function () {

    },
    success: function (data) {
      // console.log(data);
      var optionHtml='';
      data.forEach(function (item,i) {
        // if(item.is_analysis==0){
          optionHtml+='<option value="'+item.auto_id+'" >'+item.name+'</option>';
        
      });
      $('#analysis_select').html(optionHtml);
      $('#analysis_select').chosen();
      $('#analysisName').val($('#analysis_select').find('option:selected').text()).focus();
      $('#analysis_select').off('change').on('change',function () {
        // console.log($(this).find('option:selected').text());
        $('#analysisName').val($(this).find('option:selected').text()).focus();
      })
    }
  });
}
initAnalysisChosen();
$('#analysisBox').on('click','#mod1_analysisModalBtn',function () {
  initAnalysisChosen();
});


// modules 添加
var modulesArr=[];
var modulesDataArr=[];
var modulesAllData={};
$('#mod1_analysisModal').off('click','.file-item').on('click','.file-item',function () {
  $(this).addClass('active');
  var id=$(this).data('id');
  var classify=$(this).data('classify');
  var name=$.trim($(this).find('.madalName').text());
  modulesAllData[classify]=modulesAllData[classify]?modulesAllData[classify]:[];
  modulesAllData[classify].push({
    name: name,
    id: id,
  });
  modulNumInit(modulesAllData);
  // console.log(modulesAllData);
});

// 数量详情事件
$('#mod1_analysisModal').off('click','.targetBar .modulNum').on('click','.targetBar .modulNum',function (e) {
  e.stopPropagation();
  $(this).parents('.targetBar').find('.modulListBox').slideToggle(200);
})

// 数量删除
$('#mod1_analysisModal').off('click','.targetBar .modulList .delBtn').on('click','.targetBar .modulList .delBtn',function (e) {
  e.stopPropagation();
  var that=this;
  $.map(modulesAllData,function (val,key) {
    if(key==$(that).attr('data-classify')){
      for (var i = val.length-1; i >= 0 ; i--) {
        if(val[i].id==$(that).attr('data-id')){
          val.splice(i,1);
          break ;
        }
      }
    }
  });
  $(that).parents('.modulList').remove();
  modulListInit(modulesAllData);
  modulNumInit(modulesAllData);
});

// 添加分析
$('#mod1_analysisModal').on('click','#addAnalysisBtn',function () {
  $.map(modulesAllData,function (val,key) {
    val.forEach(function (item,i) {
      modulesArr.push(item.id);
    });
  });
  modulesArr.forEach(function (item,i) {
    modulesDataArr[i]=$layoutObj[item];
  });
  if($.trim($('#analysisName').val())){
    $.ajax({
      type: "post",
      url: "analysis_al/mod1/addAnalysisSearchConfig",
      data: {
        sid: $('#analysis_select').val(),
        analysisCategory: $('#analysisType').val(),
        name: $('#analysisName').val(),
        modules: modulesArr.join(','),
        layout: JSON.stringify(modulesDataArr)
      },
      success: function (data) {
        if(data.status=='ok'){
          $('#analysisModal .modalSelectHiddenBox').stop().animate({scrollTop: 0},0);
          $('#analysisModal').modal('hide');
          analysis_Table.draw(true);
          // 清空模态框
          $('#analysisModal #analysisName').val('');
          $('#analysisModal .targetBar').find('.modulNum').html('').hide();
          $('#analysisModal .targetBar').find('.modulListBox').html('').hide();
          $('#analysisModal .modalSelectList .file-item').removeClass('active');
          $("#analysisType").val("").trigger("chosen:updated");
          
          swal({
						title: "是否跳转到分析",
						type: "warning",
						showCancelButton: true,
						confirmButtonColor: "#DD6B55",
						confirmButtonText: "跳转",
						cancelButtonText: "取消",
						closeOnConfirm: false
					},
					function() {
						swal.close();
						window.location.href = 'analysis_al/mod2?autoId='+data.autoId;
					});
        }else if(data.status=='exist'){
          toastr["error"]("存在相同分析名称");
        }else {
          toastr["error"]("添加失败,请刷新重试");
        }
      },
      error: function (res) {
        toastr["error"]("添加失败,请刷新重试");
      }
    });
  }else {
    toastr["info"]("分析名称不能为空");
  }
});

// 分析主题切换
function initDisplayChosen() {
   $.ajax({
     type: "get",
     url: "analysis_al/mod1/analysisInfos",
     data: {
       'analysisType':$('#analysis_typeSelect').val()
     },
  //   beforeSend: function () {

  //   },
    success: function (data) {
      // console.log(data);
      var optionHtml='';
      data.forEach(function (item,i) {
        optionHtml+='<option value="'+item.auto_id+'" >'+item.name+'</option>';
      });
      $('#display_selectInput').html(optionHtml);

      $('#display_selectInput').chosen();
      $('#display_selectInput').prop('disabled', true).trigger("chosen:updated");
     }
   });
}

// 添加案例库
$('#analysisBox').on('click','.analysisTableAdd',function () {
  if(!$(this).attr('data-layout')||$(this).attr('data-layout')==''||$(this).attr('data-layout')=='[]'){
    toastr["info"]("该分析没有分析模块");
    return ;
  }
  $('#addDisplayBtn').attr('data-auto-id',$(this).attr('data-autoId'));
  $('#addDisplayBtn').attr('data-sid',$(this).attr('data-sid'));
//  var that=this;
//  $($(that).parent().siblings()[1]).children().text()
//  $('#display_selectInput').find('option').each(function () {
//    if($(that).attr('data-name')==$(this).text()){
//      $(this).prop("selected",true);
//      $("#display_selectInput").trigger("chosen:updated");
//    }
//  });
  
  $("#display_selectInput").html("<option selected='true'>"+$(this).attr('data-name')+"<option>");
  $("#display_selectInput").chosen();
  $('#display_selectInput').prop('disabled', true).trigger("chosen:updated");
  
  $('.eventImgBox').html('').load('dynamic/eventImg.html');
  
//  initDisplayChosen();
  $('#displayName').val($(this).parents('tr').find('td').eq(1).text());
  $('#addDisplayModal').modal('show');
  return false;
});

// 展示选择
$('#addDisplayModalBox').on('click','.modalSelectList',function () {
  if($(this).find('.file-preview').hasClass('active')){
    // $(this).find('.file-preview').removeClass('active');
  }else {
    $(this).find('.file-preview').addClass('active');
    $(this).siblings('.modalSelectList').find('.file-preview').removeClass('active');
  }
  if ($('#addDisplayModal').find('.modalSelectList .file-preview.active').attr('data-id') == "specialTopic") {
  	$('.eventImgBox').show()
  }else{
  	$('.eventImgBox').hide()
  }
});

// 展示添加
$('#addDisplayModalBox').on('click','#addDisplayBtn',function () {
  if($.trim($('#displayName').val())==''){
    toastr["info"]("发布名称不能为空");
    return ;
  }
  var displayType='';
  $('#addDisplayModal').find('.modalSelectList .file-preview').each(function (item,i) {
    if($(this).hasClass('active')){
      displayType=$(this).attr('data-id');
    }
  });
  var time = $('input[name="addDispalyDate"]:checked ').val();
  var sTime = '';
  var eTime = '';
  if (!time) {
  	var times = $('#display_date').val().split(' 至 ');
		sTime = times[0]+' 00:00:00';
		eTime = times[1]+' 23:59:59';
  }
  $.ajax({
    type: "post",
    url: "display_al/mod1/addDisplayConfig",
    data: {
      aid: $('#addDisplayBtn').attr('data-auto-id'),
      name: $.trim($('#displayName').val()),
      displayType: displayType,
      realTime:time,
      startTime:sTime,
      endTime:eTime     
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
	        analysis_Table.draw(false);
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
	          let type = getPathDisplayType($("span.file-preview.active").data("id"));
//	          if(displayType=='report'){
//	            window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#addDisplayBtn').attr('data-sid')+'&path=new-report.html&displayType=3';
//	          }else {
//	            window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#addDisplayBtn').attr('data-sid')+'&path='+displayType+'.html';
//	          }
            if(type=='large-screen'){
              window.open('display_al/mod2?autoId='+data.autoId+'&sid='+$('#addDisplayBtn').attr('data-sid')+'&path='+type+'.html&displayType='+type);
            }else {
	            window.location.href = 'display_al/mod2?autoId='+data.autoId+'&sid='+$('#addDisplayBtn').attr('data-sid')+'&path='+type+'.html&displayType='+type;
            }
	        });
        }
      }else if(data.status=='exist'){
        $('.refresh-preloader').hide();
        $('#addDisplayModal').modal('hide');
        toastr["error"]("添加失败,该发布名称已存在");
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

