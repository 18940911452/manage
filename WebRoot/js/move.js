zbdp.moveVerticalCancel = function(label) {
    clearInterval(zbdp['moveVerticalInterval' + label.substring(0, 1).toUpperCase() + label.substring(1)]);
}

zbdp.moveVertical = function(container, selector, label) {
    var intervalName = 'moveVerticalInterval' + label.substring(0, 1).toUpperCase() + label.substring(1);
    
    zbdp[intervalName] = setInterval(function() {
        var item = container.find('.' + selector + ':last'); //此变量不可放置于函数起始处，li:first取值是变化的
        var item2 = container.find('.' + selector + ':first');
        var _h = item.height(); //取得每次滚动高度
        if (label == "Mod9"){
        	item2.animate({
	            marginTop: -(_h+40) + 'px'
	        }, 600, function() { //通过取负margin值，隐藏第一行
	            item2.css('marginTop', 0);
	            item2.appendTo(container); //隐藏后，将该行的margin值置零，并插入到最后，实现无缝滚动
	        })
        } else {
        	item2.animate({
	            marginTop: _h + 'px'
	        }, 600, function() { //通过取负margin值，隐藏第一行
	            item2.css('marginTop', 0);
	            item.prependTo(container); //隐藏后，将该行的margin值置零，并插入到最后，实现无缝滚动
	        })
        }
    }, zbdp.configData.moveVerticalDelay);
}

var moveHorizentalArr = {}
zbdp.moveHorizental = function(container, selector, label) {
	var id = container.prop('id');
	clearTimeout(moveHorizentalArr[id])
    var wrappers = container.find('.' + selector);
    var wrapperWidth = $(wrappers[0]).width();
    // console.log(wrapperWidth);
    for (var i = 0; i < wrappers.length; i++) {
//  	console.log(id+i)
    	clearTimeout(moveHorizentalArr[id+i])
        var wrapper = $(wrappers[i]);
        var content = wrapper.children();
        var contentWidth = content.width();
        // console.log(contentWidth);
        if (contentWidth > wrapperWidth) {
            doMove(wrapper, content, contentWidth,i);
        }
    }

    function doMove(wrapper, content, contentWidth,i) {
        var contentSibling = content.siblings();
        if (contentSibling.length == 0) {
            content.css('padding-right', '80px');
            contentSibling = content.clone();
            //console.log(contentSibling);
            wrapper.append(contentSibling);
        }
        var moveLength = contentWidth + 80;
        var moveDur = parseInt(moveLength / zbdp.configData.moveHorizontalSpeed) * 1000;
        content.animate({
            marginLeft: -moveLength + 'px'
        }, moveDur, function() {
            content.css('margin-left', '0px').appendTo(wrapper);
           moveHorizentalArr[id+i] = setTimeout(function() {
                doMove(wrapper, contentSibling, contentWidth);
            }, 2000);
        });
    }
}
