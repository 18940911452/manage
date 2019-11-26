(function (n, e) {
	//计算rem
    var t = n.documentElement, i = "orientationchange" in window ? "orientationchange" : "resize", d = function () {
        var n = t.clientWidth;
        n && (t.style.fontSize = n / 7.5 + "px")
    };
    n.addEventListener && (e.addEventListener(i, d, !1), n.addEventListener("DOMContentLoaded", d, !1))
    
		    /*var div1 = document.querySelector('#div1');
		    var div2 = document.querySelector('#btn3');
		    var div3 = document.querySelector('#title3');
			//限制最大宽高，不让滑块出去
			var maxW = div1.clientWidth-20;
			//手指触摸开始，记录div的初始位置
			div2.addEventListener('touchstart', function(e) {
			 var ev = e || window.event;
			 var touch = ev.targetTouches[0];
			 oL = touch.clientX - div2.offsetLeft;
			 document.addEventListener("touchmove", defaultEvent, false);
			});
			//触摸中的，位置记录
			div2.addEventListener('touchmove', function(e) {
			 var ev = e || window.event;
			 var touch = ev.targetTouches[0];
			 var oLeft = touch.clientX - oL;
			 if(oLeft < 0) {
			 	oLeft = 0;
			 } else if(oLeft >= maxW) {
			 	oLeft = maxW;
			 }
			 div3.style.width = 100-(maxW/oLeft)+'%'
			 div2.style.left = oLeft + 'px';
			});
			//触摸结束时的处理
			div2.addEventListener('touchend', function() {
			 document.removeEventListener("touchmove", defaultEvent);
			});
			//阻止默认事件
			function defaultEvent(e) {
			 e.preventDefault();
			}*/
		})(document, window);
		
		