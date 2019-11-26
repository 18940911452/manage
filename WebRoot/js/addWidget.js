function getParam(widget,type){
	// console.log(widget);
  var sid = widget.sid!= undefined?widget.sid:$sid;
  var autoId = widget.autoid;
  if(type=='analysis'){
    var time = $('#analysis_date').val().split(' 至 ');
    var sTime;
    var eTime;
    if($('#analysis_date').val().indexOf(':')>=0){
      sTime = time[0];
      eTime = time[1];
    }else {
      sTime = time[0]+' 00:00:00';
      if(time[1] == moment().format('YYYY-MM-DD')){
        eTime = moment().format('YYYY-MM-DD HH:mm:ss')
      }else{
        eTime = time[1]+' 23:59:59';
      }
    }
    let param = {
      startTime:sTime,
      endTime:eTime,
      path:'../../../dynamic/analysis/'+widget.id+'.html',
      sid: sid,
      autoId: autoId,
    };
    return param
  }else if(type=='specialTopic') {
    let param =  {
      startTime: $sTime,
      endTime: $eTime,
      path:'../../../dynamic/analysis/'+widget.id+'.html',
      sid: sid,
      autoId: autoId,
    };
    return param
  }else if(type=='share-analysis'||type=='moblie-share-analysis') {
  	let pathName = widget.id;
  	if (type=='moblie-share-analysis') {
  		if('compar_info_count' == pathName || 'info_count' == pathName || 'site_count' == pathName || 'search_infos' == pathName || 'top_ten_rankings' == pathName || 'news_hot_ranking' == pathName || 'influence_trend' == pathName || 'social_media_ranking' == widget.id){
        pathName = 'mobile/'+pathName;
      }
  	}
    let param = {
      path:'../../../dynamic/analysis/'+pathName+'.html',
      sid: sid,
      autoId: autoId,
    };
    return param
  }
  
}
var modulesAllData={};
var modulesAllDataFlag=false;
function addWidget (widgets,type) {
  if(modulesAllDataFlag){
    modulesAllData={};
    modulesAllDataFlag=false;
  }
  $.each(widgets, function (i, widget) {
    if(type=='share-analysis'||type=='moblie-share-analysis'){
      //获取accesstoken
      var href = window.location.href;
    //  debugger;
      var accessToken= href.substr(href.lastIndexOf("/"));
      if(accessToken.lastIndexOf("?")>0){
        accessToken = accessToken.substr(0,accessToken.lastIndexOf("?"));
      }
      var urls = widget.url.split("?");
      var url = urls[0]+accessToken;
      if(urls.length>1){
        url+="?"+urls[1];
      }
//  widget.url= url;
    }
    let param = getParam(widget,type);
    if (type=='share-analysis') {
    	widget.url= url;
    }
    if(type=='moblie-share-analysis'){
      let pathName = widget.id;
      if('compar_info_count' == pathName || 'info_count' == pathName || 'site_count' == pathName || 'search_infos' == pathName || 'top_ten_rankings' == pathName || 'news_hot_ranking' == pathName || 'influence_trend' == pathName || 'social_media_ranking' == widget.id){
        pathName = 'mobile/'+pathName;
      }
      

//    let param = getParam(pathName)
      $search_infos_url = $layoutObj[1].url+accessToken;
      if($sessionid == ""){
        widget.url= url;
      }else{
        param.startTime = $sTime;
        param.endTime = $eTime;
        writeCookie ("JSESSIONID", $sessionid, 1)
        $search_infos_url = $layoutObj[1].url;
      }
      sessionStorage.setItem("search_infos_url", $search_infos_url);
    }
    let random = getRandom(6);
    if(type=='analysis'||type=='specialTopic'){
      var str = '<div><div data-autoid="'+widget.autoid+'" data-id="'+widget.id+'" data-sid="'+(widget.sid!= undefined?widget.sid:$sid)+'" id="'+widget.id+random+'" class="'+widget.id+'" data-href="'+widget.url+'"></div></div>';
    }else {
      var str = '<div class="'+widget.id+'_widgth"><div data-id="'+widget.autoid+'" id="'+widget.id+random+'" class="'+widget.id+'" data-href="'+widget.url+'" data-sid="'+(widget.sid!= undefined?widget.sid:$sid)+'"></div></div>';
    }
    
    var idTemp=widget.autoid;
    var classify='';
    if(idTemp==1||idTemp==25){
      classify = 'info';
    }else if(idTemp==2||idTemp==31||idTemp==4||idTemp==20||idTemp==21||idTemp==23||idTemp==24){
      classify = 'spread';
    }else if(idTemp==5||idTemp==6||idTemp==7||idTemp==26||idTemp==27||idTemp==28||idTemp==29||idTemp==49){
      classify = 'dataSourceAnalysis';
    }else if(idTemp==14||idTemp==22){
      classify = 'distribution';
    }else if(idTemp==13){
      classify = 'emotion';
    }else if(idTemp==12||idTemp==15||idTemp==16||idTemp==17||idTemp==36||idTemp==30||idTemp==8||idTemp==9||idTemp==10||idTemp==11){
      classify = 'hotFocus';
    }else if(idTemp==32||idTemp==33||idTemp==34||idTemp==35){
      classify = 'bloggerHabit';
    }else if(idTemp==18||idTemp==19){
      classify = 'staticModule';
    }else if(idTemp==37||idTemp==38||idTemp==39||idTemp==40||idTemp==41||idTemp==42||idTemp==43||idTemp==44||idTemp==45||idTemp==46||idTemp==47||idTemp==48){
      classify = 'sensitive';
    }
    var name=widget.name;
    if(!name){
      name=$('#analysisModal .modalSelectList .file-item[data-id="'+idTemp+'"] .madalName').text().trim();
    }
    modulesAllData[classify]=modulesAllData[classify]?modulesAllData[classify]:[];
    modulesAllData[classify].push({
      id: idTemp,
      name: name,
      selfId: widget.id+random
    });

//  let sid = widget.sid!= undefined?widget.sid:$sid;
//  param.sid = sid
    if(type!='moblie-share-analysis'){
      gridster.add_widget(str, widget.size_x, widget.size_y, widget.col, widget.row,'','',function () {
        let $dom = $('#'+widget.id+random)
        var refreshBox = $dom;
        $("<div class='refresh-preloader'><div class='la-timer la-dark'><div></div></div></div>").appendTo(refreshBox).fadeIn(300);
        drawTemp($dom,widget,param,random,type);
      })
    }else {
      $('#analysisModular').append(str)
      let $dom = $('#'+widget.id+random)
      let refreshBox = $dom;
      $("<div class='refresh-preloader'><div class='la-timer la-dark'><div></div></div></div>").appendTo(refreshBox).fadeIn(300);
      drawTemp($dom,widget,param,random,type);
    }
    
  });
};

function drawTemp($dom,widget,param,random,type) {
  if (widget.id == 'search_infos'||widget.id == 'sensitive_list') {
    param['sortWay']='desc';
    param['sortField']=widget.sortType?widget.sortType:1;
    ipageLoad($dom,param,'search_infos_ipage'+random,widget)
  }else if(widget.id == 'text_module'||widget.id == 'picture_module'){
    $dom.load(widget.url,{'data':widget.content},function () {
      if(widget.name){
        $dom.find('.echarts3-title').text(widget.name)
      };
      if(type=='moblie-share-analysis'){
        $dom.find('.text_content').css({'width':'100%','max-height':'250px','overflow':'auto'});
		$dom.find('.picture_imgBox').css({'width':'100%','max-height':'300px','overflow':'auto'});
      }
      var refreshBox = $dom;
      let refreshPreloader = refreshBox.find('.refresh-preloader'),
      deletedRefreshBox = refreshPreloader.fadeOut(300, function(){
        refreshPreloader.remove();
      });
      refreshBox.find('[data-toggle="tooltip"]').tooltip();
    })
  }else if(widget.id == 'city_destribute'||widget.id == 'location_destribute'||widget.id == 'entityCount'||widget.id == 'hot_world'){
    param.type=widget.type;
    $dom.load(widget.url,param,function () {
      if(widget.name){
        $dom.find('.echarts3-title').text(widget.name)
      };
      var refreshBox = $dom;
      let refreshPreloader = refreshBox.find('.refresh-preloader'),
      deletedRefreshBox = refreshPreloader.fadeOut(300, function(){
        refreshPreloader.remove();
      });
      refreshBox.find('[data-toggle="tooltip"]').tooltip();
    })
  }else{
    $dom.load(widget.url,param,function () {
      if(widget.name){
        $dom.find('.echarts3-title').text(widget.name)
      };
      if(type=='moblie-share-analysis'){
        if($dom.find('.content_').find('canvas').length>0){
          $dom.addClass('echarts-mobile');
          echarts.init(document.getElementById($dom.find('.content_>div').prop('id'))).resize();
        }
      }
      var refreshBox = $dom;
      let refreshPreloader = refreshBox.find('.refresh-preloader'),
      deletedRefreshBox = refreshPreloader.fadeOut(300, function(){
        refreshPreloader.remove();
      });
      refreshBox.find('[data-toggle="tooltip"]').tooltip();
      modalResize();
      if(type=='moblie-share-analysis'){
        loadFinish ($dom,widget,param);
      }
    })
  }  
}


// 相似文章数
function similarityListNum(container,pageSize) {
  var listBox=container.find('.mainInfoList');
  var tempArr=[];
  for (let i = listBox.length-pageSize; i < listBox.length; i++) {
    if($(listBox[i]).find('.similarityListBtn')[0]){
      tempArr.push($(listBox[i]).find('.similarityListBtn').attr('data-uuid'));
    }
  }
  if(tempArr!=[]&&tempArr.join(',')!=''){
    $.ajax({
      type: "get",
      url: "search_al/mod2/getSimilarityInfoCount",
      data: {
        uuids: tempArr.join(',')
      },
      success: function (data) {
        // console.log(data);
        $.map(data,function (val,key) {
          // console.log(listBox.find('.similarityListBtn[data-uuid="'+key+'"] >span')[0]);
          listBox.find('.similarityListBtn[data-uuid="'+key+'"] >span').html(val);
        })
      }
    })
  }
}

