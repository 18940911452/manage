var chartSetTheme = 'walden_change'

function drawStatisticChartAnalysis(container, option,callback) {
	$("#" + container).empty().attr("_echarts_instance_", "");
	var domMain = document.getElementById(container);
	var myChart = echarts.init(domMain, chartSetTheme);

//	myChart.setTheme(chartSetTheme);
	myChart.hideLoading();
	myChart.setOption(option);
	myChartObj[container] = myChart;
	if (callback) {
		callback(myChart);
	}
	
}