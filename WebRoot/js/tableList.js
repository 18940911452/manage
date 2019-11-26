var taskid='';
	scale=function (btn,bar,title){
	this.btn=document.getElementById(btn);
	this.bar=document.getElementById(bar);
	this.title=document.getElementById(title);
	this.step=this.bar.getElementsByTagName("DIV")[0];
	this.init();
};
scale.prototype={
	init:function (){
		var f=this,g=document,b=window,m=Math;
		f.btn.onmousedown=function (e){
			var x=(e||b.event).clientX;
			var l=this.offsetLeft;
			var max=f.bar.offsetWidth-this.offsetWidth;
			g.onmousemove=function (e){
				var thisX=(e||b.event).clientX;
				var to=m.min(max,m.max(-2,l+(thisX-x)));
				f.btn.style.left=to+'px';
				f.ondrag(m.round(m.max(0,to/max)*100),to);
				b.getSelection ? b.getSelection().removeAllRanges() : g.selection.empty();
			};
			g.onmouseup=new Function('this.onmousemove=null');
		};
	},
	ondrag:function (pos,x){
		this.step.style.width=Math.max(0,x)+'px';
		this.title.innerHTML=pos+'%';
	}
}
//任务主题　选择标签
$(".tagsinputval").tagsinput();
//任务进度特效
new scale('btn3','bar3','title3');
new scale('btn2','bar2','title2');
//点击查询列表
$('#subMitBtn').click(function(){
	list()	
});
$('#subMitBtn').keyup(function(event){
	if(event.keyCode ==13){
		list()	
	}
});
list();
//获取任务列表
function list(){
	let option = {};
	option.param = {
		task_name : $.trim($("#exampleInputEmail2").val()),
		path : "page/report/mod2/dynamic/taskList.html",
		principal:"11"
	};
	option.callback = function () {
		//编辑
		$('.tableEdit').click(function(event){
			event.stopPropagation();
			$('#myModal2').modal('show');
			 taskid = $(this).attr('b_id');
			$.post('report/mod2/taskList',{task_id:taskid},function(data){
				var res = data.data[0];
				$('#myModal2 .title').val(res.title);
				$('#myModal2 .task_desc').val(res.task_desc);
				$('#myModal2 .bootstrap-tagsinput input').prev().remove();
				$('#myModal2 .bootstrap-tagsinput input').before('<span class="badge badge badge-info">'+res.top_name+'<span data-role="remove" class="remove"></span></span>')
				$('#myModal2 #title3').css('width',res.plan);
				$('#myModal2 #title3').html(res.plan);
				$('#myModal2 #start_time').val(res.start_time);
				$('#myModal2 #btn3').css('left',res.plan);
				if(res.fileList.length>0){
					$('.fileLoadBox .file-input').hide();
					let str='';
					for(var i=0;i<res.fileList.length;i++){
						str+='<li>'+res.fileList[i].file_name+'<span class="deleteList" data-id="'+res.fileList[i].id+'"><img src="img/delete.png "></span></li>'
					}
					$('.fileLoadBox ul').html(str)
				}
				let html ='';
				let html2 ='';
				var arr = $('.addPropList li').length;
				for(var i=0;i<res.principal.length;i++){
				    for(var j=0;j<arr;j++){
				      	if($('.addPropList li').eq(j).html()==res.principal[i].nick_name){
				      		$('.addPropList li').eq(j).hide()
				      	}
				    }
					html+='<li data-id="'+res.principal[i].user_id+'"><span><i class="iconfont icon-ren1"></i></span>'+res.principal[i].nick_name+'<span class="delete"><img src="img/delete.png "></span></li>'
				}
				for(var i=0;i<res.participant.length;i++){
				    for(var j=0;j<arr;j++){
				      	if($('.addPropList li').eq(j).html()==res.participant[i].nick_name){
				      		$('.addPropList li').eq(j).hide()
				      	}
				    }
					html2+='<li data-id="'+res.participant[i].user_id+'"><span><i class="iconfont icon-ren1"></i></span>'+res.participant[i].nick_name+'<span class="delete"><img src="img/delete.png "></span></li>'
				}
				$('#myModal2 .principal ul').html(html)
				$('#myModal2 .participant ul').html(html2)
			})
		});
		//删除
		$('.tableDelete').click(function(event){
			event.stopPropagation();
			let taskid2 = $(this).attr('b_id');
			swal({ 
			   title: "确定删除此条任务吗？", 
			   type: "warning",
			   showCancelButton: true, 
			   confirmButtonColor: "#DD6B55",
			   confirmButtonText: "确定！", 
			   cancelButtonText: "取消！",
			   closeOnConfirm: false, 
			   closeOnCancel: false	
			},function(isConfirm){ 
			  if (isConfirm) {
				 	$.post('report/mod2/deleteTask',{task_id:taskid2},function(data){
						if(data.code == "1") {
						swal({ 
						   title: "成功!",
						   type: "success",
						   timer: 1500, 
						   showConfirmButton: false 
						});
						$('#subMitBtn').click()
					}else {
						swal({ 
						   title: "删除失败!",
						   type: "error",
						   timer: 2000,
						   showConfirmButton: false 
						});
					}
				})
			  } else {
			    $(".sweet-alert").fadeOut(100);
				$(".sweet-overlay").fadeOut(100);
			  } 
			});
			
		});
		
		//主题添加限制 
		$('.tagsinputBox').off('click','li').on('click','li',function(){
			if($(this).parents('.tagsinputBox').parent().find('.bootstrap-tagsinput .remove').length==0){
				$(this).parents('.tagsinputBox').parent().find('.bootstrap-tagsinput input').before('<span class="badge badge badge-info">'+$(this).html()+'<span data-role="remove" class="remove"></span></span>')
			}else{
				swal({ 
				   title: "主题只能添加一个",
				   type: "error",
				   timer: 2000,
				   showConfirmButton: false 
				});
			}
		})
		//主题删除
		$('.bootstrap-tagsinput').on('click','.remove',function(){
			$(this).parent().remove()
		})
		//点击列表 展示详情
		$('.listTable tbody tr').click(function(event){
			event.stopPropagation();
			$('#myModal1').modal('show')
			let taskid2 = $(this).attr('b_id');
			$.post('report/mod2/taskList',{task_id:taskid2},function(data){
				var res = data.data[0];
				console.log(res)
				$('#myModal1 .title').html(res.title);
				$('#myModal1 .task_desc').html(res.task_desc);
				var arr = $('.tagsinputBox li').length;
			    $('#myModal1 .top_arr').html(res.top_name);
				$('#myModal1 .jinduT').css('width',res.plan);
				$('#myModal1 .jinduT').html(res.plan);
				$('#myModal1 .start_time').html(res.start_time);
				let html ='';
				let html2 ='';
				let html3='';
				for(var i=0;i<res.principal.length;i++){
					html+='<li data-id="'+res.principal[i].user_id+'"><span><i class="iconfont icon-ren1"></i></span>'+res.principal[i].nick_name+'</li>'
				}
				for(var i=0;i<res.participant.length;i++){
					html2+='<li data-id="'+res.participant[i].user_id+'"><span><i class="iconfont icon-ren1"></i></span>'+res.participant[i].nick_name+'</li>'
				}
				if(res.planList!=null&&res.planList.length>0){
					for(var i=0;i<res.planList.length;i++){
						html3+='<ul class="clearfix"><li>'+res.planList[i].update_time+'</li><li>'+res.planList[i].task_con_pro+'</li></ul>'
					}
					$('.tableJind').html(html3)
				}else{
					$('.tableJind').html('暂无')
				}
				$('#myModal1 .principal ul').html(html)
				$('#myModal1 .participant ul').html(html2)
			})
		
		})
		
	};
	option.pageSize = 10;
	new iPage.PageLoad("tableListAll","report/mod2/taskList",option).init();
}
$('.bootstrap-tagsinput input').focus(function(){
	$('.tagsinputBox').show();
})
top_name()
function top_name(){
	//主题下拉框
		$.post('report/mod2/topicalList',function(data){
			let html="";
			for(var i=0;i<data.length;i++){
				html+='<li data-id="'+data[i].id+'">'+data[i].top_name+'</li>';
			}
			$('.tagsinputBox').html(html);
		});
}
//点击别处的位置 隐藏 主题提示框 和 参与人的提示框
$('body').click(function(e) {
  if(e.target.offsetParent.className != 'tagsinput-primary form-group')
   if ( $('.tagsinputBox').is(':visible') && $('.bootstrap-tagsinput input').is(':visible')) {
     $('.tagsinputBox').hide();
   }
})
$('#myModal2').on('click','.fileLoadBox li .deleteList',function(){
	let id = $(this).data('id');
	swal({ 
	   title: "确定删除附件吗？", 
	   type: "warning",
	   showCancelButton: true, 
	   confirmButtonColor: "#DD6B55",
	   confirmButtonText: "确定！", 
	   cancelButtonText: "取消！",
	   closeOnConfirm: false, 
	   closeOnCancel: false	
	},function(isConfirm){ 
	  if (isConfirm) {
	  	$(this).parent().remove()
		$.post('report/mod2/deleteFileById',{id:id},function(data){
			if(data.code == "1") {
				swal({ 
				   title: "成功!",
				   type: "success",
				   timer: 1500, 
				   showConfirmButton: false 
				});
			}else {
				swal({ 
				   title: "删除失败!",
				   type: "error",
				   timer: 2000,
				   showConfirmButton: false 
				});
			}
		})
	  } else {
	    $(".sweet-alert").fadeOut(100);
			$(".sweet-overlay").fadeOut(100);
	  } 
	});
})
$('.addList').click(function(event){
	event.stopPropagation();
	$('#myModal3').modal('show')
})
$('body').on('click','.seeText .delete',function(){
	let $this = $(this);
	swal({ 
	   title: "确定删除吗？", 
	   type: "warning",
	   showCancelButton: true, 
	   confirmButtonColor: "#DD6B55",
	   confirmButtonText: "确定！", 
	   cancelButtonText: "取消！",
	   closeOnConfirm: false, 
	   closeOnCancel: false	
	},function(isConfirm){ 
	  if (isConfirm) {
		var id=$this.parent().data('id');
		let $list = $this.parents('.participantBox').find('.addPropList ul li').length;
		for(var i=0;i<$list;i++){
			if($this.parents('.participantBox').find('.addPropList ul li').eq(i).data('id') == id){
				$this.parents('.participantBox').find('.addPropList ul li').eq(i).show();
			}
		}
		$this.parent().remove();
		swal({ 
		   title: "成功!",
		   type: "success",
		   timer: 1500, 
		   showConfirmButton: false 
		});
	  } else {
	    $(".sweet-alert").fadeOut(100);
		$(".sweet-overlay").fadeOut(100);
	  } 
	});
})

//编辑完成
var file_arr1 = [];
	$("#upload_file").fileinput({  
			'language': 'zh',
	      	'theme': 'fa',
	      	uploadUrl: "/file-upload-batch/2",
	        'showPreview': true,
	        'showRemove':true,
	        'showUpload':false,
	        uploadAsync: true,
	        autoReplace:false,
		    previewFileIcon: '<i class="fa fa-file"></i>',
		    allowedPreviewTypes: null, // set to empty, null or false to disable preview for all types
		    previewFileIconSettings: {
		        'docx': '<i class="fa fa-file-word-o text-primary"></i>',
		        'xlsx': '<i class="fa fa-file-excel-o text-success"></i>',
		        'pptx': '<i class="fa fa-file-powerpoint-o text-danger"></i>',
		        'jpg': '<i class="fa fa-file-photo-o text-warning"></i>',
		        'pdf': '<i class="fa fa-file-pdf-o text-danger"></i>',
		        'zip': '<i class="fa fa-file-archive-o text-muted"></i>',
		    },
	        'maxFileSize': 0,//单位为kb，如果为0表示不限制文件大小
			'maxFileCount': 10, //表示允许同时上传的最大文件个数
			'maxFilesNum' : 10,//上传最大的文件数量
			"msgFilesTooMany": "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
	    }).on("filebatchselected",function (event,files) {
	    	file_arr1 = files;
	    	$(".fileinput-remove-button").click(function () {
	    		file_arr1 = [];
			});
			
	    }).on('filepreupload', function(event, data, previewId, index) {     //上传中
	        
	    }).on("fileuploaded", function (event, data, index) {    //一个文件上传成功
			
	    	    
	    }).on('fileerror', function(event, data, msg) {  //一个文件上传失败
	       /*swal({
			   title: "上传失败33!",
			   type: "error",
			   timer: 2000, 
			   showConfirmButton: false 
			});*/
	    });
	$(".btnSuccess").unbind("click").click(function () {
		let name = $.trim($("#add_file_name").val());
		let principal='';
		let formDataFiles = new FormData();
		let ff = $('#upload_file')[0];
		for(var i=0;i<$('#myModal2 .principal li').length;i++){
			principal+=$('#myModal2 .principal li').eq(i).data('id')+',';
		}
		let participant='';
		for(var i=0;i<$('#myModal2 .participant li').length;i++){
			participant +=$('#myModal2 .participant li').eq(i).data('id')+',';
		}
		var arr = $('.tagsinputBox li').length;
	    var top_id='';
	    console.log($('#myModal2 .bootstrap-tagsinput .badge-info').text())
	    for(var i=0;i<arr;i++){
	      	if($('.tagsinputBox li').eq(i).html()==$.trim($('#myModal2 .bootstrap-tagsinput .badge-info').text())){
	      		top_id=$('.tagsinputBox li').eq(i).data('id');
	      	}
	    }
		principal=principal.substring(0,principal.length-1);
		participant=participant.substring(0,participant.length-1);
		let jsondata = {
			task_id : taskid,
			title:$('#myModal2 .title').val(),
			task_desc:$('#myModal2 .task_desc').val(),
			participant:participant,
			principal:principal,
			top_id:top_id,
			taskPlan:$('#myModal2 .task_miao').val(),
			plan:$('#myModal2 #title3').html(),
			start_time:$('#myModal2 #start_time').val(),
		};
		if(jsondata.title==''||jsondata.participant==''||jsondata.principal==''||jsondata.start_time==''||jsondata.top_id==''){
			swal({ 
			   title: "*必选项 不可以为空!",
			   type: "error",
			   timer: 2000,
			   showConfirmButton: false 
			});
			return
		};
		for(var i=0;i<file_arr1.length;i++){
			formDataFiles.append("file"+i,file_arr1[i]);//
		}
		for(var i=0;i<file_arr1.length;i++){
			formDataFiles.append("file"+i,file_arr1[i]);//
		}
		formDataFiles.append('jsondata', JSON.stringify(jsondata));
		$.ajax({
			url:"report/mod2/updateTask",
			type:"post",
			cache: false,
		    data: formDataFiles,
		    processData: false,
		    contentType: false,
		    success:function(data){
		    	if(data.code == "1") {
					swal({ 
					   title: "成功!",
					   type: "success",
					   timer: 1500, 
					   showConfirmButton: false 
					});
					$("#myModal2").modal("hide");
					$('#subMitBtn').click();
					file_arr1=[];
				}else {
					swal({ 
					   title: "新建失败!",
					   type: "error",
					   timer: 2000,
					   showConfirmButton: false 
					});
				}
		    }
		});
	});
var optionSet1 = {
	maxDate: moment(),
	singleDatePicker: true,
	showDropdowns: true,
	timePicker: true,
    timePickerIncrement: 1,
    timePicker12Hour:false,
//  linkedCalendars : true,
    timePickerSeconds : true, 
    showSecond: true,
	format: 'YYYY-MM-DD HH:mm:ss',
};
$('#start_time').daterangepicker(optionSet1);
$('#start_time2').daterangepicker(optionSet1);
// 点击添加参与人 或责任人
$('body').off('click','.addPropList li').on('click','.addPropList li',function(){
	var name= $(this).html();
	var id= $(this).data('id');
	$(this).parents('.addPropList').hide();
	if($(this).parents('.principallist').length>0){
		if($(this).parents('.addProp').next().find('ul li').length<=0){
			$(this).parents('.addProp').next().find('ul').append('<li data-id="'+id+'"><span><i class="iconfont icon-ren1"></i></span>'+name+'<span class="delete"><img src="img/delete.png "></span></li>')
		}else{
			swal({ 
			   title: "负责人只可添加一个!",
			   type: "error",
			   timer: 2000,
			   showConfirmButton: false 
			});
		}
	}else{
		$(this).parents('.addProp').next().find('ul').append('<li data-id="'+id+'"><span><i class="iconfont icon-ren1"></i></span>'+name+'<span class="delete"><img src="img/delete.png "></span></li>')
	}
	var arr = $('.addPropList li').length;
    for(var i=0;i<arr;i++){
      	if($('.addPropList li').eq(i).html()==name){
      		$('.addPropList li').eq(i).hide()
      	}
    }
	$(this).hide();
})
$('.addPropBtn').click(function(){
	$(this).next().slideToggle();
})
//获取人的列表
$.post('report/mod2/getAllUser',function(data){
	var res = data.data;
	let html='';
	for(var i=0;i<res.length;i++){
		html+='<li data-id="'+res[i].user_id+'">'+res[i].nick_name+'</li>'
	}
	$('.addPropList ul').html(html)
})
//添加
var file_arr = [];
	$("#upload_file2").fileinput({  
			'language': 'zh',
	      	'theme': 'fa',
	      	uploadUrl: "/file-upload-batch/2",
	        'showPreview': true,
	        'showRemove':true,
	        'showUpload':false,
	        uploadAsync: true,
	        autoReplace:false,
		    previewFileIcon: '<i class="fa fa-file"></i>',
		    allowedPreviewTypes: null, // set to empty, null or false to disable preview for all types
		    previewFileIconSettings: {
		        'docx': '<i class="fa fa-file-word-o text-primary"></i>',
		        'xlsx': '<i class="fa fa-file-excel-o text-success"></i>',
		        'pptx': '<i class="fa fa-file-powerpoint-o text-danger"></i>',
		        'jpg': '<i class="fa fa-file-photo-o text-warning"></i>',
		        'pdf': '<i class="fa fa-file-pdf-o text-danger"></i>',
		        'zip': '<i class="fa fa-file-archive-o text-muted"></i>',
		    },
	        'maxFileSize': 0,//单位为kb，如果为0表示不限制文件大小
			'maxFileCount': 10, //表示允许同时上传的最大文件个数
			'maxFilesNum' : 10,//上传最大的文件数量
			"msgFilesTooMany": "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
	    }).on("filebatchselected",function (event,files) {
	    	file_arr = files;
	    	$(".fileinput-remove-button").click(function () {
	    		file_arr = [];
			});
			
	    }).on('filepreupload', function(event, data, previewId, index) {     //上传中
	        
	    }).on("fileuploaded", function (event, data, index) {    //一个文件上传成功
			
	    	    
	    }).on('fileerror', function(event, data, msg) {  //一个文件上传失败
	       /*swal({
			   title: "上传失败33!",
			   type: "error",
			   timer: 2000, 
			   showConfirmButton: false 
			});*/
	    });
	$(".btnSuccess2").unbind("click").click(function () {
		let name = $.trim($("#add_file_name").val());
		let principal='';
		let formDataFiles = new FormData();
		let ff = $('#upload_file')[0];
		for(var i=0;i<$('#myModal3 .principal li').length;i++){
			principal+=$('#myModal3 .principal li').eq(i).data('id')+',';
		}
		let participant='';
		for(var i=0;i<$('#myModal3 .participant li').length;i++){
			participant +=$('#myModal3 .participant li').eq(i).data('id')+',';
		}
		principal=principal.substring(0,principal.length-1);
		participant=participant.substring(0,participant.length-1);
		var arr = $('.tagsinputBox li').length;
	    var top_id='';
	    for(var i=0;i<arr;i++){
	      	if($('#myModal3 .tagsinputBox li').eq(i).html()==$.trim($('#myModal3 .bootstrap-tagsinput').text())){
	      		top_id=$('#myModal3 .tagsinputBox li').eq(i).data('id');
	      	}
	    }
		let jsondata = {
			title:$('#myModal3 .title').val(),
			task_desc:$('#myModal3 .task_desc').val(),
			participant:participant,
			principal:principal,
			top_id:top_id,
			taskPlan:$('#myModal3 .task_miao').val(),
			plan:$('#myModal3 #title2').html(),
			start_time:$('#myModal3 #start_time2').val(),
		};
		
		if(jsondata.title==''||jsondata.participant==''||jsondata.principal==''||jsondata.start_time==''){
			swal({ 
			   title: "*必选项 不可以为空!",
			   type: "error",
			   timer: 2000,
			   showConfirmButton: false 
			});
			return
		};
		for(var i=0;i<file_arr.length;i++){
			formDataFiles.append("file"+i,file_arr[i]);//
		}
		//formDataFiles.append("file",file_arr);// 
		formDataFiles.append('jsondata', JSON.stringify(jsondata));
		$.ajax({
			url:"report/mod2/updateTask",
			type:"post",
			cache: false,
		    data: formDataFiles,
		    processData: false,
		    contentType: false,
		    success:function(data){
		    	if(data.code == "1") {
					swal({ 
					   title: "成功!",
					   type: "success",
					   timer: 1500, 
					   showConfirmButton: false 
					});
					$("#myModal3").modal("hide");
					$('#subMitBtn').click();
					file_arr=[];
				}else {
					swal({ 
					   title: "新建失败!",
					   type: "error",
					   timer: 2000,
					   showConfirmButton: false 
					});
				}
		    }
		});
	});
$('#myModal3').on('hidden.bs.modal', function () {
    $('#myModal3').find('input').val('');
    $('#myModal3').find('textarea').val('');
    $('#myModal3').find('.principal ul').html('');
    $('#myModal3').find('.participant ul').html('');
    $('#myModal3').find('#title2').html('0%');
    $('#myModal3').find('#btn2').css('left','0');
    $('#myModal3').find('#title2').css('width','0');
    $('body .file-preview .ileinput-remove').click();
    file_arr=[];
    $('.addPropList li').show()
});
$('#myModal2').on('hidden.bs.modal', function () {
    file_arr1=[];
    $('.addPropList li').show()
});