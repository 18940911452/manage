var iToolbar = {
	/**
	 * 工具栏组件 
	 * @param containerObj 工具栏容器
	 * @param options 选项
	 * @param isFlexible 是否可伸缩 false 为完全展开
	 * options说明
	 * [
	 * 	{id,icon,name,group_class,success(){}},
	 * 	{id,icon,name,group_class,success(){}},
	 * 	……
	 * ]
	 * 
	 */
	init : function(options,containerObj,isFlexible){
		var containerName = "iToolbar_container_"+new Date().getTime();
		$(document.body).append("<div class='iToolbar_container "+containerName+"'></div>");
		var $container = null;
		if(null!=containerObj){
			$container = $(containerObj);
		}else{
			$container = $("."+containerName);
		}
		if(null==options || options.length==0){
			return;
		}
		var group_class = [];
		var grpup_defult = ""+new Date().getTime();
		//将工具分组加载进容器
		for(var i=0 ; i<options.length ; i++){
			var option = options[i];
			if(null==option.group_class){
				option.group_class = grpup_defult;
			}
			if(group_class.indexOf(option.group_class)==-1){
				group_class.push(option.group_class);
				$container.append("<span class='iToolbar_icon_group "+option.group_class+"'></span>");
			}
		};
		//将工具按分组加载入容器
		for(var i=0 ; i<options.length ; i++){
			var option = options[i];//id,icon,name,group_id
			var html = "";
			var icon="";
			if(null!=option.icon && option.icon!=""){
				icon='<i class="fa '+option.icon+'"> </i> '
				//html = "<span class='iToolbar_icon_single' id='"+option.id+"' title='"+option.name+"'> <img class='iToolbar_icon_img' src='"+option.icon+"'  alt='"+option.name+"' /></span>";
			}else{
			}
			if(isFlexible==false){
				html = "<span class='iToolbar_icon_single' id='"+option.id+"' title='"+option.name+"'><button class='iToolbar_icon_button'> "+icon+"<span class='text'  >"+option.name+"</span></button></span>";
			}else{
				html = "<span class='iToolbar_icon_single' id='"+option.id+"' title='"+option.name+"'><button class='iToolbar_icon_button'> "+icon+"<span class='text' style='display:none;' >"+option.name+"</span></button></span>";
			}
			
			if(null==option.group_class){
				$container.find("."+grpup_defult).append(html);
			}else{
				$container.find("."+option.group_class).append(html);
			}
			//回调函数
			option.success($container,option);
			
		}
		
		var justHoverTime = 0;
		var isHide=false;
		function timeHide(){
			if(isHide==true&&(new Date().getTime()-justHoverTime)>5000){
				$(".iToolbar_icon_single .text").hide(1500);
				isHide=false;
			}
		}
		
		$(".iToolbar_icon_single").hover(function(){
			justHoverTime=new Date().getTime();
			isHide=false;
			$(".text",this).show();
		})
		
		$('.iToolbar_container').on("mouseleave", function () {
			isHide=true;
		});
		if(isFlexible!=false){
		window.setInterval(timeHide, 1000);
		}
		return $container;
	},
	
	/**
	 * 向工具栏中添加工具
	 * @param options
	 * @param $container --工具栏容器,获取方式是由init()函数返回值
	 * 
	 * options说明
	 * [
	 * 	{id,icon,name,group_class,success(){}},
	 * 	{id,icon,name,group_class,success(){}},
	 * 	……
	 * ]
	 */
	push : function(options,$container){
		if(null==$container || options.length==0){
			return;
		}

		var group_class = [];
		var grpup_defult = ""+new Date().getTime();
		//将工具分组加载进容器
		for(var i=0 ; i<options.length ; i++){
			var option = options[i];
			if(null==option.group_class){
				option.group_class = grpup_defult;
			}
			if($container.children("."+option.group_class).length==0){
				if(group_class.indexOf(option.group_class)==-1){
					group_class.push(option.group_class);
					$container.append("<span class='iToolbar_icon_group "+option.group_class+"'></span>");
				}
			}
		};
		//将工具按分组加载入容器
		for(var i=0 ; i<options.length ; i++){
			var option = options[i];//id,icon,name,group_id
			var html = "";
			var icon="";
			if(null!=option.icon && option.icon!=""){
				icon='<i class="fa '+option.icon+'"> </i> '
				//html = "<span class='iToolbar_icon_single' id='"+option.id+"' title='"+option.name+"'> <img class='iToolbar_icon_img' src='"+option.icon+"'  alt='"+option.name+"' /></span>";
			}else{
			}
			html = "<span class='iToolbar_icon_single' id='"+option.id+"' title='"+option.name+"'><button class='iToolbar_icon_button'> "+icon+"<span class='text' style='display:none;' >"+option.name+"</span></button></span>";
			if(null==option.group_class){
				$container.find("."+grpup_defult).append(html);
			}else{
				$container.find("."+option.group_class).append(html);
			}
			//回调函数
			option.success($container,option);
		}
		
		var justHoverTime = 0;
		var isHide=false;
		function timeHide(){
			if(isHide==true&&(new Date().getTime()-justHoverTime)>5000){
				$(".iToolbar_icon_single .text").hide(1500);
				isHide=false;
			}
		}
		
		$(".iToolbar_icon_single").unbind("hover").hover(function(){
			justHoverTime=new Date().getTime();
			isHide=false;
			$(".text",this).show();
		});
		
//		$('.iToolbar_container').on("mouseleave", function () {
//			isHide=true;
//		});
		window.setInterval(timeHide, 1000);
		return $container;
	},
	
	/**
	 * 从工具栏中移除工具
	 * @param options
	 * @param $container --工具栏容器,获取方式是由init()函数返回值
	 * 
	 * options说明
	 * [
	 * 	{id,success(){}},
	 * 	{id,success(){}},
	 * 	……
	 * ]
	 */
	remove : function(options,$container){
		if(null==$container || options.length==0){
			return;
		}
		for(var i=0 ; i<options.length ; i++){
			var option = options[i];//id,success();
			$container.find(".iToolbar_icon_single").each(function(){
				var id = $(this).attr("id");
				if(id==option.id){
					$(this).remove();
					option.success();
				}
			});
		}
	}
};