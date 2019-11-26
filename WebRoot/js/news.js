zbdp.initNews = function() {
	//var nationUrl = serverDomain + '/screen/recentpolicy/searchchina?page_no=0&page_size=8';
	//var zjUrl = serverDomain + '/screen/recentpolicy/searchzj?page_no=0&page_size=8';
//	var groupUlr = serverDomain + '/screen/Corp/get/';
	//var zjUrl ='js/news/core/zhejiang.json';
	//var nationUrl = 'js/news/core/zhejiang.json';
	var todayTimeout = zbdp.configData.todayUpdateInterval || 120;
	var groupTimeout = zbdp.configData.groupUpdateInterval || 120;

//	drawNationNews2();
//	drawZJNews(zjUrl);
	//drawGroupNews(groupUlr);

	function drawNationNews2() {
		var label = 'nation';
		//test-f4.json
		//http://180.76.158.50:8080/mlfdp/js/news/core/zhizhidata.json
		url = "http://statistic.xinhua-news.cn/largeScreen_al/mod2/artTop";
		$.get(url,{language:2,count:20},function(datastr, state) {
			if (state == 'success') {
				var data = datastr;
				drawNews2(data, label);
			} else {
				console.log('获取数据失败');
			}
			setTimeout(function() {
				drawNationNews2();
			}, todayTimeout * 1000);
		});
	}
}

drawNews2 = function(data, label) {
	// function draw(data, label) {
	var dataArr,
		element,
		elementTop,
		elementBottom,
		markEle, //页面显示数据量
		moveThread, //垂直滚动需要有的条目数量
		moveVSelector, //垂直滚动的选择符
		moveHSelector; //水平滚动的选择符

	if (label == 'nation') {
		dataArr = data.data;
		if (!dataArr) {
			return;
		}
		element = $('.custom-content-box2');
		markEle = $('.news-today-nation-maker span');
		moveVSelector = 'custom-content-box2 custom-result';
		moveHSelector = 'custom-lrmove';
		moveThread = 8;

		element.empty();
	}
	
	//显示数量
	if (markEle && markEle.length > 0) {
		markEle.text(data.data.length);
	        
	}
	
	//循环增加信息条目
	for (var i = 0; i < dataArr.length; i++) {
		var item = dataArr[i];
		var itemWrapper = document.createElement('div');
		itemWrapper.classList.add('custom-result');
		
		var itemDetailWrapper = document.createElement('div');
		itemDetailWrapper.classList.add('custom-title');
		itemDetailWrapper.innerHTML = '<div class="custom-sub-title clearfix"><b class="pull-left custom-lrmove"><a class="j-text-move" href="/crossMedia_al/mod1/detail?eid='+ item.art_id +'" target="_blank">' + item.title + '</a></b><span class="pull-right">'+ item.count +'次</span></div><p class="custom-font-white no-margins">&emsp;</p></div>';
		itemWrapper.appendChild(itemDetailWrapper);
		
		element.append(itemWrapper);
	}

	zbdp.moveVerticalCancel(label);
	if (dataArr.length >= moveThread) {
		zbdp.moveVertical(element, moveVSelector, label);
	}

	if (element) {
		zbdp.moveHorizental(element, moveHSelector, label);
	}
	if (elementTop) {
		zbdp.moveHorizental(elementTop, moveHSelector, label);
	}
	if (elementBottom) {
		zbdp.moveHorizental(elementBottom, moveHSelector, label);
	}

};


