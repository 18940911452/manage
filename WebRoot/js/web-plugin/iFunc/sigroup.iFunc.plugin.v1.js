var iFunc = {
	reg : /^\s*$/,		//用于判断是否是一个或者多个空格
	split_1 : "#@#",
	split_2 : "~@~",
	//字符串为空判断
	isEmpty : function(str){
		if(str != null && str != undefined && this.reg.test(str) == false){
			return false;
		}
		return true;
	},
	//邮箱监测
	CheckEmail : function (email){
		var reyx= /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
		return(reyx.test(email));
	},
	
	//数字缩写
	sum : function(t){
		if(t!=null && t!=undefined && t!=""){
			var t = parseInt(t);
			if(t > 1000000){
				t = (t/1000000).toFixed(1)+"M";
			} else if(t > 1000){
				t = (t/1000).toFixed(1)+"K";
			}
		}
		return t;
	},
	//URL解析
	UrlParse : function(){
		var search = window.location.search;
		var paramObj = {};
		if (search != null && !this.isEmpty(search) && search.indexOf("?") == 0){
	  		search = search.substring(1);
	  		var params = search.split("&");
	  		//console.log("size:"+params.length + "; params:" + params);
	  		for(var i=0; i<params.length; i++){
	  			var n = params[i];
	  			if(n != null && !this.isEmpty(n)){
		  			var param = n.split("=");
		  			if(param != null && param.length > 1){
			  			var key = param[0];
			  			var val = param[1];
			  			//console.log(key+":"+val);
			  			if(key != null && !this.isEmpty(key)){
			  				paramObj[key] = val;
				  			//console.log(paramObj);
			  			}
		  			}
	  			}
	  		}
	  	}
	  	return paramObj;
	  },
	  
	  //cookie操作
	  getValue:function(k){var v=window.sessionStorage.getItem(k);return v;},
	  saveValue:function(k,v){if(k==null){return null;}var o=window.sessionStorage.setItem(k,v);return o;},
	  removeHashKey:function(k){window.sessionStorage.removeItem(k);},
	  saveValueWithinROOT:function(k,v){if(k==null){return null;}var o=$.cookie(k,v,{expires:30,path:"/"});return o;},
	  removeHashKeyWithinROOT:function(key){$.cookie(key, null,{path:"/"});},
	  //系统跳转标记前缀
	  sysJumpPreFix:"url_param_",
	  
	  //获取用户权限
	  getUserFrame:function(username,func){
		  if(this.isEmpty(username)){
			  return null;
		  }
		  var frame;
		  var un = this.getValue("username");
		  if(!this.isEmpty(un) && un==username){
			  frame = this.getValue("sysFrame");
		  }
		  if(this.isEmpty(frame)){
			  $.post("sys_find_user_frame.action", function(vframe){
				  console.log("Frame From Post.");
				  iFunc.saveValue("sysFrame",vframe);
				  iFunc.saveValue("username",username);
				  if(func){
					  func(vframe);
				  }
			  });
		  } else {
			  console.log("Frame From Storage.");
			  if(func){
				  func(frame);
			  }
		  }
	  },
	  
	  
	  //iPage按钮
	  exportBtn : '<button class="ipage-fenye-on exoprt_btn">导出所选</button>',
	  exportAllBtn : '<button class="ipage-fenye-on exoprt_all_btn">导出全部</button>',
	  infoCompareBtn : '<button class="ipage-fenye-on info_compare_btn">信息对比</button>',
	  infoAnalysisBtn : '<button class="ipage-fenye-on info_analysis_btn">信息分析</button>',
	  

	  /**
	   * 话题名称验证：不能包含特殊字符，中文、字母、数字、下划线
	   * @param name
	   * @returns {Boolean}
	   */
	  checkTopicName : function(name) {
		  var re1 = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9_-])*$"); 
		  if (!re1.test(name)){ 
			  return false; 
		  } else {
			  return true;
		  } 
		  
	  },
	  
	  checkKeywords : function(keyword) {
		  var kws = $.trim(keyword);
		  if (kws != '') {
			  kws = kws.replace("；",";");
			  if (kws == ';') {
				  return false;
			  }
			  var re1 = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9_-])*$"); 
			  var kwsArray = kws.split(";");
			  for (var i = 0; i < kwsArray.length; i ++) {
				  if (kwsArray[i] == '' || !re1.test(kwsArray[i])) {
					  return false;
				  }
			  }
			  return true;
		  } else {
			  return false;
		  }
	  },
	/**
	 * checkbox全选按钮初始化,默认全选按钮为第一个复选框
	 * @param container 装载复选框的jquery对象
	 * @param name 复选框的name属性值
	 */
	checkbox_all_btn:function(container,name){
		//初始化全选按钮
		$(container).find("input[name='"+name+"']:eq(0)").unbind("click").click(function(){
			var isCheck = $(container).find("input[name='"+name+"']:eq(0)").is(':checked');
			if(isCheck){
				var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
				if(checkboxs.length>0){
					for(var i=0 ; i<checkboxs.length ; i++){
						$(checkboxs[i]).prop("checked","checked");						
					}
				}
			}else{
				var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
				if(checkboxs.length>0){
					for(var i=0 ; i<checkboxs.length ; i++){
						$(checkboxs[i]).prop("checked","");						
					}
				}				
			};
		});
		//初始化非全选按钮，效果是：如果其他按钮全选则全选按钮为勾选状态，如果有任意非全选按钮未选中，全选按钮为非选中状态
		$(container).find("input[name='"+name+"']:gt(0)").unbind("click").click(function(){
			var isCheck = true;
			var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
			if(checkboxs.length>0){
				for(var i=0 ; i<checkboxs.length ; i++){
					if(!$(checkboxs[i]).is(':checked')){
						isCheck = false;
					}
				}
			}
			if(isCheck){
				$(container).find("input[name='"+name+"']:eq(0)").prop("checked","checked");
			}else{
				$(container).find("input[name='"+name+"']:eq(0)").prop("checked","");
			}
		});
		//初始化，按钮全部为选中状态
		//$(container).find("input[name='"+name+"']").prop("checked","checked");
	}
};

