// 
function settingTypeInit() {
  // var optionHtml='';
  // $analysisCategory.forEach(function (item,i) {
  //   optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  // });
  // $('#analysisType').html(optionHtml);

  $('#setting_typeSelect').chosen();
}
settingTypeInit();

var setting_Table='';
function setting_TableInit() {
  setting_Table = $('#settingTable').DataTable({
    // "destroy": true,
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
    "ordering": true,
    "autoWidth": false,
    "aaSorting": [ // 清除默认第一列小箭头
      [ 0 ]
    ],
    "order": [[ 2, "desc" ]],// 有一个箭头样式被页面中的css隐藏 @qyc
    "columnDefs": [
      { "orderable": false, "targets": [0,1,2,3,4,5,6,7]},
      { "width": "2%", "targets": 0 },
      // { "width": "20%", "targets": 1 }
    ],
    "lengthMenu": [ 10 ], // 默认分页选择第一个
    "lengthChange": false, // 分页可调
    "searching": true,
    "dom":'lBrtip', // 不显示搜索框
    "fnDrawCallback": function (Settings) {
      // console.log(Settings);
      $('#setting_tbodyId [data-toggle="tooltip"]').tooltip();
    },
  });
}
setting_TableInit();

// 表格
function initTable() {
  $.ajax({
    type: "get",
    url: "setting_al/mod1/getDisplayConfigList",
    data: {
      status: 1,
      path: 'share_info_list.html'
    },
    beforeSend: function () {
      $('#settingTable_wrapper').hide();
      $('.refresh-preloader').show();
    },
    success: function (data) {
      // console.log(data);
      setting_Table.destroy(false);

      // var trHtml='';
      // data.forEach(function (item,i) {
      //   var displayStr='';
      //   if(item.analysis_display){
      //     $.each(item.analysis_display,function (i,dis) {
      //       if(i==0){
      //         displayStr += dis.name;
      //       }else {
      //         displayStr += ','+dis.name;
      //       }
      //     });
      //   }
      //   trHtml+='<tr data-auto-id="'+item.auto_id+'" data-sid="'+item.sid+'" data-layout="" data-modules="'+item.modules+'"><td class="inbox-rating textCenter analysisTdFunc"></td><td class="inbox-title analysisTdFunc">'+item.name+'</td><td class="inbox-message textCenter analysisTdFunc">'+(!item.analysis_display?'<i class="iconfont icon-diannao" style="cursor:default;" title="未展示"></i>':'<i class="iconfont icon-diannao" style="color: #01c0c8; cursor:default;" title="'+displayStr+'"></i>')+'</td><td class="inbox-attachment textCenter analysisTdFunc">'+item.insert_time+'</td><td class="inbox-date textCenter analysisTdFunc">'+item.user_name+'</td><td class="inbox-icon textCenter"><div class="btn-group"><a data-toggle="dropdown" href="#" class="" aria-expanded="false"><svg t="1526958579337" class="icon" style="fill:#01c0c8;" viewBox="0 0 5120 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2393" xmlns:xlink="http://www.w3.org/1999/xlink" width="24" height="24"><defs><style type="text/css"></style></defs><path d="M3963.904 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512zM1981.44 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512z" fill="" p-id="2394"></path><path d="M1981.952 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512-273.92 0-495.616 229.376-495.616 512zM0 512c0 283.136 221.696 512 495.616 512 273.408 0 495.104-228.864 495.104-512 0-282.624-221.696-512-495.104-512C221.696 0 0 229.888 0 512z" fill="" p-id="2395"></path></svg></a><ul class="dropdown-menu dropdown-menu-right"><li><a href="" data-autoId="'+item.auto_id+'" data-name="'+item.name+'" class="analysisTableAdd">添加到案例库</a></li><li><a class="analysisTableDel" data-autoId="'+item.auto_id+'" data-analysis-display="'+item.analysis_display+'" href="#">删除</a></li></ul></div></td></tr>';
      // });
      // $('#setting_tbodyId').html(trHtml);
      $('#setting_tbodyId').html(data);

      setting_TableInit();
      $('#settingTable_wrapper').show();
      $('.refresh-preloader').hide();

    }
  });
}
initTable();

animateInputInit('.tableSearhInput','.tableSearhIcon','.tableSearhDelIcon',function () {
  setting_Table.search($('.tableSearhInput').val().trim()).draw();
},'',function () {
  setting_Table.search($('.tableSearhInput').val().trim()).draw();
});

var start_time='';
var end_time='';
var expire_time='';
var share_class='';
var auto_id='';
var real_time='';
// 密码回填
$('#settingTable').off('click','.passwordBtn').on('click','.passwordBtn',function () {
  $('#passwordInput').val($(this).attr('data-password'));
  if($(this).text().indexOf('修改分享密码')>=0){
    $('#cancelpassword').show();
  }else {
    $('#cancelpassword').hide();
  }
  start_time=$(this).attr('data-start-time');
  end_time=$(this).attr('data-end-time');
  expire_time=$(this).attr('data-expire-time');
  share_class=$(this).attr('data-share-class');
  auto_id=$(this).attr('data-auto-id');
  real_time=$(this).attr('data-real-time'); 
});
$('#passwordModal').off('click','#passwordBtn').on('click','#passwordBtn',function () {
  if($.trim($('#passwordInput').val())==''||$.trim($('#passwordInput').val()).length<4){
    toastr["info"]("请正确输入4位密码");
    return ;
  }
  $.ajax({
    type: "post",
    url: "setting_al/mod1/updateDisplayConfig",
    data: {
      autoId : auto_id,
      startTime : start_time,
      endTime : end_time,
      expireTime : expire_time,
      shareClass : share_class,
      realTime : real_time,
      sharePassword : $.trim($('#passwordInput').val()),
      shareLevle : 2
    },
    success: function (data) {
      // console.log(data);
      if(data=='fail'){
        toastr["error"]("修改失败，请刷新重试");
      }else if(data=='success') {
        toastr["success"]("修改成功");
        $('#passwordInput').val('');
        initTable();
      }
      $('#passwordModal').modal('hide');
    },
    error: function (res) {
      // console.log(res);
      toastr["error"]("修改失败，请刷新重试");
      $('#passwordModal').modal('hide');
    }
  });
});
$('#passwordModal').off('click','#cancelpassword').on('click','#cancelpassword',function () {
  $.ajax({
    type: "post",
    url: "setting_al/mod1/updateDisplayConfig",
    data: {
      autoId : auto_id,
      startTime : start_time,
      endTime : end_time,
      expireTime : expire_time,
      shareClass : share_class,
      realTime : real_time,
      shareLevle : 1
    },
    success: function (data) {
      // console.log(data);
      if(data=='fail'){
        toastr["error"]("取消加密失败，请刷新重试");
      }else if(data=='success') {
        toastr["success"]("取消加密成功");
        initTable();
      }
      $('#delTagModal').modal('hide');
    },
    error: function (res) {
      // console.log(res);
      toastr["error"]("取消加密失败，请刷新重试");
      $('#delTagModal').modal('hide');
    }
  });
});

// 删除回填
$('#settingTable').off('click','.delTag').on('click','.delTag',function () {
  start_time=$(this).attr('data-start-time');
  end_time=$(this).attr('data-end-time');
  expire_time=$(this).attr('data-expire-time');
  share_class=$(this).attr('data-share-class');
  auto_id=$(this).attr('data-auto-id');
  real_time=$(this).attr('data-real-time');
});
$('#delTagModal').off('click','#delTagBtn').on('click','#delTagBtn',function () {
  $.ajax({
    type: "get",
    url: "setting_al/mod1/updateDisplayConfig",
    data: {
      autoId : auto_id,
      startTime : start_time,
      endTime : end_time,
      expireTime : expire_time,
      shareClass : share_class,
      realTime : real_time,
      status: 0
    },
    success: function (data) {
      // console.log(data);
      if(data=='fail'){
        toastr["error"]("删除失败，请刷新重试");
      }else if(data=='success') {
        toastr["success"]("删除成功");
        initTable();
      }
      $('#delTagModal').modal('hide');
    },
    error: function (res) {
      // console.log(res);
      toastr["error"]("删除失败，请刷新重试");
      $('#delTagModal').modal('hide');
    }
  });
});

// 复制
$('#settingTable').off('click','.copyBtn').on('click','.copyBtn',function () {
  copyUrl2($(this).attr('data-url'));
  return false;
});
function copyUrl2(url) {
  var Url2 = window.location.origin+'/'+url;
  // var pwd = $('.share-password').val();
  var oInput = document.createElement('input');
  // if($('input[name="share-method"]:checked').val()==2) {
  //   oInput.value = '链接：' + Url2 + ' 密码：' + pwd;
  // } else {
    oInput.value = Url2;
  // }
	// document.body.appendChild(oInput);
  $("body").prepend(oInput);
  oInput.select(); // 选择对象
  oInput.focus();
  document.execCommand("SelectAll");
  document.execCommand("Copy"); // 执行浏览器复制命令
  oInput.className = 'oInput';
  oInput.style.display = 'none';
  try {
    if(document.execCommand('copy', false, null)) {
      document.execCommand("Copy");
      document.body.removeChild(oInput);
      toastr["success"]("复制成功");
    } else {
      toastr["error"]("复制失败，请手动复制");
    }
  } catch(err) {
    toastr["error"]("复制失败，请手动复制");
  }

}

// 二维码
$('#settingTable').off('click','.qrcodeBtn').on('click','.qrcodeBtn',function (e) {
  e.stopPropagation();
  if($(this).hasClass('open')){
    $(this).removeClass('open').find('i').removeClass('icon-quxiaoyulan').addClass('icon-qrCode').end().find('.qrcodeBox').html('');
  }else {
    $('.qrcodeBtn').removeClass('open').find('i').removeClass('icon-quxiaoyulan').addClass('icon-qrCode').end().find('.qrcodeBox').html('');
    $(this).addClass('open').find('i').removeClass('icon-qrCode').addClass('icon-quxiaoyulan');
    var href = window.location.href.substr(0,window.location.href.indexOf('setting_al'))+$(this).attr('data-url');
    // console.log(href);
    if (typeof qrcodeNot!="undefined") {
      toastr["error"]("生成失败，请刷新重试");
      $(this).removeClass('open').find('i').removeClass('icon-quxiaoyulan').addClass('icon-qrCode');
    }else{
      $(this).find('.qrcodeBox').html('').qrcode({
        render: "canvas",
        text: href,
        width: "160", //二维码的宽度
        height: "160", //二维码的高度
        background: "#ffffff", //二维码的后景色
        foreground: "#000000", //二维码的前景色
        src: 'img/wh/logoNew1_w.png' //二维码中间的图片
      });
    }
  }
  
  return false;
});
$('body').on('click',function () {
  if($('.qrcodeBtn').hasClass('open')){
    $('.qrcodeBtn').removeClass('open').find('i').removeClass('icon-quxiaoyulan').addClass('icon-qrCode').end().find('.qrcodeBox').html('');
  }
});

