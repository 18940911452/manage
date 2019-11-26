// input
function animateInputInit(input,searchIcon,delIcon,keyupFunc,enterFunc,delFunc) {
  $(document).off('click', searchIcon).on('click', searchIcon, function () {
    $(this).css({'left':'3px'}).siblings(input).css({'width':'120px','padding-left':'25px','padding-right':'20px'});
  });
  $(document).on('transitionend webkitTransitionEnd',input,function(){
    $(this).focus();
  }); 
  // $(document).on('focus', input, function () {
  //   $(this).width('120px').siblings(searchIcon).css({'left':'3px','padding-left':'25px'});
  //   if($(this).val()){
  //     $(this).siblings(delIcon).show();
  //   }
  // });
  $(document).off('keyup', input).on('keyup', input, function (e) {
    if($(this).val()){
      $(this).siblings(delIcon).show();
    }else {
      $(this).siblings(delIcon).hide();
    }
    if(e && e.keyCode==13){
      if(enterFunc) enterFunc();
    }else {
      if(keyupFunc) keyupFunc();
    }
  });
  $(document).off('blur', input).on('blur', input, function () {
    if(!$(this).val()){
      $(this).siblings(delIcon).hide();
      $(this).css({'width':'0','padding-left':'0','padding-right':'0'}).siblings(searchIcon).css({'left':'-20px'});
    }
  });
  // 删除
  $(document).off('click', delIcon).on('click', delIcon, function () {
    $(this).hide().siblings(input).val('');
    $(this).siblings(searchIcon).css('left','-20px').end().siblings(input).css({'width':'0','padding-left':'0','padding-right':'0'});
    if(delFunc) delFunc();
  });
  
}

// 数据源
// 1：twitter_tweet,2:facebook_blog,3:instagram_blog,4:major_info,5:blog_info
// 6:forum_thread,7:newspaper_info,8:wechat,9:mblog_info,10:appdata
// 11：youtube_videoinfo
var $layoutObj = {
	"1":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/searchInfos","id":"search_infos","autoid":'1'},
	"2":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/comparInfoCount","id":"compar_info_count","autoid":'2'},
	"3":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/","id":"","autoid":'3'},
	"4":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/infoCount","id":"info_count","autoid":'4'},
	"5":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/siteCount?dataSource=4","id":"site_count","autoid":'5'},
	"6":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/siteCount?dataSource=7","id":"site_count","autoid":'6'},
	"7":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/siteCount?dataSource=10","id":"site_count","autoid":'7'},
	"8":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/topTenRankings?dataSource=8","id":"top_ten_rankings","autoid":'8'},
	"9":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/topTenRankings?dataSource=9","id":"top_ten_rankings","autoid":'9'},
	"10":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/topTenRankings?dataSource=1","id":"top_ten_rankings","autoid":'10'},
	"11":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/topTenRankings?dataSource=2","id":"top_ten_rankings","autoid":'11'},
	"12":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/hotWordsRandking","id":"hot_world","autoid":'12'},
	"13":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/emotionDestribute","id":"emotion_destribute","autoid":'13'},
	"14":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/locationDestribute","id":"location_destribute","autoid":'14'},
  "15":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/entityCount?entityType=entity_people","id":"entityCount","autoid":'15'},
  "16":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/entityCount?entityType=entity_address","id":"entityCount","autoid":'16'},
  "17":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/entityCount?entityType=entity_org","id":"entityCount","autoid":'17'},
  "18":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/load?path=../../../dynamic/analysis/text_module.html","id":"text_module","autoid":'18'},
  "19":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load?path=../../../dynamic/analysis/picture_module.html","id":"picture_module","autoid":'19'},
  "20":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/dataSourceCount","id":"data_source_count","autoid":'20'},
  "21":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/langugaeRanking","id":"langugae_ranking","autoid":'21'},
  "22":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/cityDestribute","id":"city_destribute","autoid":'22'},
  "23":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/catgoryRanking","id":"catgory_ranking","autoid":'23'},
  "24":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/dataSourceSum","id":"data_source_sum","autoid":'24'},
  "25":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/timeLine","id":"time_line","autoid":'25'},
  "26":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/socialMediaRnaking?dataSource=8","id":"social_media_ranking","autoid":'26'},
  "27":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/socialMediaRnaking?dataSource=9","id":"social_media_ranking","autoid":'27'},
  "28":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/socialMediaRnaking?dataSource=1","id":"social_media_ranking","autoid":'28'},
  "29":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/socialMediaRnaking?dataSource=2","id":"social_media_ranking","autoid":'29'},
  "30":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/newsHotRanking?dataSource=4","id":"news_hot_ranking","autoid":'30'},
  "31":{"size_x":2,"size_y":1,"url":"analysis_al/mod2/influenceTrend","id":"influence_trend","autoid":'31'},
  "32":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/publishHabit?dataSource=8","id":"publish_habit","autoid":'32'},
  "33":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/publishHabit?dataSource=9","id":"publish_habit","autoid":'33'},
  "34":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/publishHabit?dataSource=1","id":"publish_habit","autoid":'34'},
  "35":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/publishHabit?dataSource=2","id":"publish_habit","autoid":'35'},
  "36":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/hotTopic","id":"hot_topic","autoid":'36'},
  "37":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveRumorInfo","id":"sensitive_list","autoid":'37'},
  "38":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveRumorTrend","id":"sensitive_trend","autoid":'38'},
  "39":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitivePornographicInfo","id":"sensitive_list","autoid":'39'},
  "40":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitivePornographicTrend","id":"sensitive_trend","autoid":'40'},
  "41":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitivePoliticalDetrimentalInfo","id":"sensitive_list","autoid":'41'},
  "42":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitivePoliticalDetrimentalTrend","id":"sensitive_trend","autoid":'42'},
  "43":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveMilitaryInfo","id":"sensitive_list","autoid":'43'},
  "44":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveMilitaryTrend","id":"sensitive_trend","autoid":'44'},
  "45":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveReligionInfo","id":"sensitive_list","autoid":'45'},
  "46":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveReligionTrend","id":"sensitive_trend","autoid":'46'},
  "47":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveMaliciouslyHypeInfo","id":"sensitive_list","autoid":'47'},
  "48":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/sensitiveMaliciouslyHypeTrend","id":"sensitive_trend","autoid":'48'},
  "49":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/hotForumThread","id":"site_count","autoid":'49'},

  "100":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"media","autoid":'100'},
  "101":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"hot","autoid":'101'},
  "102":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"newmedia","autoid":'102'},
  "103":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"effectRanking","autoid":'103'},
  "104":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"article","autoid":'104'},
  "105":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"emotion","autoid":'105'},
  "106":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"reprint","autoid":'106'},
  "107":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"banmian","autoid":'107'},
  "108":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"taskMonitor","autoid":'108'},
  "109":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"card","autoid":'109'},
  "110":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"taskNotice","autoid":'110'},
  "111":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"day","autoid":'111'},
  "112":{"size_x":3,"size_y":1,"url":"analysis_al/mod2/load","id":"mediaMap","autoid":'112'}
};


var colorArr=[
    "#46b2e1",
    "#f7b64f",
    "#70e5c1",
    "#626d90",
    "#8cda5e",
    "#a0a8e4"
];

// var noDataHtml='<p class="noDataHtml" style="text-align: center; margin-top: 60px; font-size: 20px;">暂无分析数据</p>';
var noDataHtml='<p class="noDataHtml" style="text-align: center; padding-top: 60px; font-size: 20px;"><img src="img/nodata.png"></p>';
var noDataHtml2='<p class="noDataHtml" style="text-align: center; padding-top: 60px; font-size: 20px;"><img src="img/nodata2.png"></p>';

// 获取随机数字符串
function getRandom(num){
  var randomArr = ['1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];
  var resArr=[];
  for(var i=0;i<num;i++){
    var ranNum = Math.floor(Math.random() * 36);
    resArr.push(randomArr[ranNum]);
  }
  return resArr.join('');
}



//随机数
function RandomNumBoth(Min,Max){
      var Range = Max - Min;
      var Rand = Math.random();
      var num = Min + Math.round(Rand * Range); //四舍五入
      return num;
}


//检测是否为移动端
window.mobileAndTabletcheck = function() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
};


//对象数组通过对象的属性进行排序
function compare(propertyName) { 
    return function (object1, object2) { 
        var value2 = object1[propertyName]; 
        var value1 = object2[propertyName]; 
        if (value1 < value2) { 
            return 1; 
        }else if (value1 > value2) { 
            return -1; 
        }else { 
            return 0; 
        } 
    };  
}



//更新专题图片
function updateDisplayConfs (id,callback) {
	if($('#addDisplayModal').find('.modalSelectList .file-preview.active').attr('data-id') == "specialTopic") {
		if (cropper == undefined) {
			callback('ok')
		}
		var img = cropper.getDataURL();
		$.post('display_al/mod1/uploadDisplayImg', {
			imgEncode: img
		}, function(data) {
			if(data.status == 'ok') {
				$.post('display_al/mod1/updateDisplayConfs', {
					imgUrl: data.imgUrl,
					autoId: id
				}, function(result) {
					callback(result.status)
					
//					if(result.status == 'ok') {
//						callback()
//					}
				})
			}
		})
	}
}


// 模块数量渲染
function modulNumInit(allData) {
  $.map(allData,function (val,key) {
    if(val.length>0){
      $('.targetBar[data-classify="'+key+'"]').find('.modulNum').html(val.length).fadeIn();
      var tempHtml='';
      val.forEach(function (item,i) {
        tempHtml+='<span class="modulList">'+
                    '<span class="nameText" data-toggle="tooltip" data-placement="bottom" title="'+item.name+'">'+item.name+'</span>'+
                    '<i class="iconfont icon-chuyidong delBtn" data-classify="'+key+'" data-id="'+item.id+'" data-selfid="'+item.selfId+'"></i>'+
                  '</span>';
      });
      $('.targetBar[data-classify="'+key+'"]').find('.modulListBox').html(tempHtml);
      $('.nameText[data-toggle="tooltip"]').tooltip();
    }else {
      $('.targetBar[data-classify="'+key+'"]').find('.modulNum').html(val.length).fadeOut();
      $('.targetBar[data-classify="'+key+'"]').find('.modulListBox').html('').fadeOut();
    }
    
  });
}
// 模块数据回填
function modulListInit(allData) {
  $('.modalSelectHiddenBox .modalSelectList .file-item').removeClass('active');
  $.map(allData,function (val,key) {
    if(val.length>0){
      val.forEach(function (item,i) {
        $('.modalSelectHiddenBox .modalSelectList .file-item[data-classify="'+key+'"][data-id="'+item.id+'"]').addClass('active');
      });
    }
    
  });
}

// 字符串长度 中英文区分
// function getStrRealLength(str) {
//   var cArr = str.match(/[^\x00-\xff]/ig);
//   return str.length + (cArr == null ? 0 : cArr.length);
// }

// 判断中英文
function getRealFormat(str) {
  return /[^\x00-\xff]/.test(str);
}

// 
function echartsAxisLabelFormatter(data,num) {
  var strTemp=data;
  var strLength=0;
  for (var i = 0; i < strTemp.split('').length; i++) {
    if(getRealFormat(strTemp.split('')[i])){
      strLength+=2;
    }else if(/[A-Z]/.test(strTemp.split('')[i])) {
      strLength+=1.5;
    }else {
      strLength+=1;
    }
    if(strLength>num){
      // console.log(strTemp,strLength,strTemp.slice(0,i-1)+'..',i-1);
      strTemp=strTemp.slice(0,i-1)+'..';
      break;
    }
  }
  return strTemp;
}
