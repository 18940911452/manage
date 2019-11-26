var iColumn = {
	/**
	 * 插件初始化
	 * @param tableObj 插件作用table对象
	 * @param confBtn  点击该对象出现配置窗口
	 * @param option   初始化参数,具体说明参看默认值设置
	 * 
	 * 注：
	 * 1. 使用本插件前，需引入jquery插件
	 * 2. 如果有多个table同时使用本插件，这些table不能放在同一容器中
	 */
	init : function(tableObj,confBtn,options){
		//设置默认值
		options=$.extend({
			ALL:{},//存放显示的列名
			USE:[],//存放隐藏的列名
			url:null,//配置修改后请求地址，将修改后的options作为参数返回
			titleClass:null,//如果列名包含于内层标签，需对内层标签设置统一的class，同时将类名传递进来（为了方便样式的设置）
			conf_head_html:null,//配置窗口头部
			conf_foot_html:"<button type='button' class='btn btn-primary iCloumn_conf_save_btn'>确定</button><button type='button' class='btn btn-default iCloumn_conf_cancel_btn'>取消</button>",
			success:function(obj){},//初始化成功后调用的函数
			success_after_conf:function(result){},//配置成功，向url发送请求后，调用的函数
			trClass:null,
			thClass:null,
			tdClass:null
		},options);
		//table 初始化
		
		iColumn.table_init(tableObj,options);//显示需要显示的列
		//设置按钮初始化
		iColumn.confBtn_init(tableObj,confBtn,options);
		options.success();
	},
	/**
	 * 列表显隐控制
	 * @param tableObj 要控制的对象容器
	 * @param options 说明见下方默认值设置
	 */
	table_init : function(tableObj,options){
		//设置默认值
		options=$.extend({
			USE:[],//存放隐藏的列名,对应column-title的属性值
			titleClass:null,//如果列名包含于内层标签，需对内层标签设置统一的class，同时将类名传递进来（为了方便样式的设置）
			trClass:null,
			thClass:null,
			tdClass:null
		},options);
		if(null==options){
			return;
		}
		if(null==options.trClass && null==options.thClass && null==options.tdClass){

			$(tableObj).find("tr").find("td,th").hide();//将全部隐藏
			//option = $.parseJSON(option);
			//获取要显示的列名数组
			var show = options.show;
			//获取要隐藏的列名数组
	  		var hide = options.hide;
	  		//获取列名所在对象
	  		var ths = $(tableObj).find("tr").find("th");
	  		if(null!=options.titleClass){
	  			ths = $(tableObj).find("tr").find("th").find("."+options.titleClass);
	  		}
	  		for(var i=0 ; i<ths.length ; i++){
	  			//获取列名
	  			var this_title = $($(ths)[i]).html();
	  			//隐藏判断与操作
	  			if(hide!=null && hide.indexOf(this_title)>-1){
	  				$(tableObj).find("tr").find('th:eq('+i+'),td:eq('+i+')').hide();
	  			}
	  			//显示判断与操作
	  			if(show!=null && show.indexOf(this_title)>-1){
	  				$(tableObj).find("tr").find('th:eq('+i+'),td:eq('+i+')').show();
	  			}
	  		}
		}else if(null!=options.trClass && null!=options.thClass && null!=options.tdClass){
			var ALL = options.ALL;
			var USE = options.USE;
			$(tableObj).find("."+options.trClass).find("."+options.thClass+",."+options.tdClass).hide();
			//获取列名所在对象
			var ths = $(tableObj).find("."+options.trClass).find("."+options.thClass);
			if(null!=options.titleClass){
				ths = $(tableObj).find("."+options.trClass).find("."+options.thClass).find("."+options.titleClass);
			}
			for(var i=0 ; i<ths.length ; i++){
				//获取列名
				var this_title = $($(ths)[i]).attr("column-title");
				//显示判断与操作
				if(USE!=null && USE.indexOf(this_title)>-1){
					$(tableObj).find("."+options.trClass).find("."+options.thClass+":eq("+i+"),."+options.tdClass+":eq("+i+")").show();
				}
			}
		}
	},
	
	confBtn_init : function(tableObj,confBtn,options){
		$(confBtn).unbind("click").click(function(){
			var $container = $(confBtn).parent();
			//判断配置页是否已生成
			if($container.find(".iColumn_conf_div").length>0){
				if($container.find(".iColumn_conf_div").is(":visible")){
					$container.find(".iColumn_conf_div").hide();
					return;
				}else{
					$container.find(".iColumn_conf_div").show();
				}
			}else{
				var html="<div class='iColumn_conf_div'><div class='conf_div_head'></div><div class='conf_div_body clearfix'></div><div class='conf_div_foot'></div></div>";
				$container.append(html);
			}
			//初始化相关配置信息
			var $container_body = $container.find(".iColumn_conf_div").find(".conf_div_body");
			var $container_head = $container.find(".iColumn_conf_div").find(".conf_div_head");
			var $container_foot = $container.find(".iColumn_conf_div").find(".conf_div_foot");
			if(null!=options.conf_head_html){
				$container_head.html(options.conf_head_html);
			}
			if(null!=options.conf_foot_html){
				$container_foot.html(options.conf_foot_html);
			}
			//获取列名所在对象
	  		var ths = $(tableObj).find("tr").find("th");
	  		if(null!=options.titleClass){
	  			ths = $(tableObj).find("tr").find("th").find("."+options.titleClass);
	  		}
	  		if(null!=options.trClass && null!=options.thClass && null!=options.tdClass){
	  		//获取列名所在对象
				ths = $(tableObj).find("."+options.trClass).find("."+options.thClass);
				if(null!=options.titleClass){
					ths = $(tableObj).find("."+options.trClass).find("."+options.thClass).find("."+options.titleClass);
				}
	  		}
	  		//
	  		var checkbox_html="";
	  		$container_body.html("");
	  		//增加全部按钮
	  		var id = 'iColumn_conf_title_'+new Date().getTime()+"_";
	  		var allCheckedHtml="<div class='iColumn_conf_title'>";
	  		allCheckedHtml+="<div class='switch'> <div class='onoffswitch'>";
	  		allCheckedHtml+= "<input type='checkbox' class='onoffswitch-checkbox' name='iColumn_conf_title' value='"+this_title_value+"' id='"+id+"'/>";
	  		allCheckedHtml+="	<label class='onoffswitch-label' for='"+id+"'>	";
	  		allCheckedHtml+="	<span class='onoffswitch-inner'></span>	";
	  		allCheckedHtml+="	<span class='onoffswitch-switch'></span>	";
	  		allCheckedHtml+="	</label>	";
	  		allCheckedHtml+="</div></div>";
	  		allCheckedHtml+="<div class='title_text'>全部</div>";
	  		allCheckedHtml+="</div>";	
	  		$container_body.append(allCheckedHtml);
	  		var is_all_checked = true;
	  		//增加配置列
	  		for(var i=0 ; i<ths.length ; i++){
	  			//获取列名
	  			var this_title = $($(ths)[i]).html();
	  			var this_title_value = $($(ths)[i]).attr("column-title");
	  			var check = "";
	  			if(null!=this_title && $.trim(this_title)!=""){
  					var id = 'iColumn_conf_title_'+new Date().getTime()+"_"+i;
  					check+="<div class=' iColumn_conf_title'>";
  					check+="<div class='switch'> <div class='onoffswitch'>";
	  				if($($(ths)[i]).is(":visible") ){
		  				check+= "<input type='checkbox' class='onoffswitch-checkbox' name='iColumn_conf_title' value='"+this_title_value+"' checked id='"+id+"'/>";
	  					
	  				}else{
		  				check+= "<input type='checkbox' class='onoffswitch-checkbox' name='iColumn_conf_title' value='"+this_title_value+"' id='"+id+"'/>";
	  					is_all_checked = false;
		  			}
	  				check+="	<label class='onoffswitch-label' for='"+id+"'>	";
  					check+="	<span class='onoffswitch-inner'></span>	";
  					check+="	<span class='onoffswitch-switch'></span>	";
  					check+="	</label>	";
	  				check+="</div></div>";
	  				check+="<div class='title_text'>"+this_title+"</div>";
	  				check+="</div>";
		  			checkbox_html += check;
	  			}
	  		}
	  		$container_body.append(checkbox_html);
	  		if(is_all_checked){
	  			$container_body.find("input[name='iColumn_conf_title']:eq(0)").prop("checked","checked");
	  		}
	  		iColumn.checkbox_all_btn($container_body, 'iColumn_conf_title');
	  		
	  		//配置保存按钮点击事件
	  		$container_foot.find(".iCloumn_conf_save_btn").unbind("click").click(function(){
	  			options.ALL={};
	  			options.USE=[];
	  			$container_body.find("input[name='iColumn_conf_title']").each(function(){
	  				var title_text = $(this).parents(".iColumn_conf_title").find(".title_text").html();
	  				if(title_text!="全部"){
	  					options.ALL[$(this).val()]=title_text;
	  					if($(this).is(":checked")){
		  					options.USE.push($(this).val());
		  				}
	  				}
	  			});
	  			if(options.USE.length==0){
	  				alert("列表展示不能为空");
	  				return;
	  			}
	  			var param = JSON.stringify(options);
	  			//向后台传递配置结果
	  			if(null!=options.url && $.trim(options.url)!=""){
	  				$.post(options.url,{data:param}, function(result){
	  				    options.success_after_conf(result,options);
	  		  			//table 初始化
	  		  			iColumn.table_init(tableObj,options);
	  		  			$container.find(".iColumn_conf_div").hide();
	  				});
	  			}
	  			
	  		});
	  		//配置取消按钮点击事件
	  		$container_foot.find(".iCloumn_conf_cancel_btn").unbind("click").click(function(){
	  			$container.find(".iColumn_conf_div").hide();
	  		});
		});
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
	}
};