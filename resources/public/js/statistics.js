function drawCharts() {
    drawChartLikes();
    drawChartComments();
}

function drawChartLikes() {
    var jsonData = $.ajax({
        url: "/stats/likes",
        dataType: "json",
        async: false
    }).responseText;
    if (JSON.parse(jsonData).cols.length > 0 && JSON.parse(jsonData).rows.length > 0) {
        var data = new google.visualization.DataTable(jsonData);
        var paddingHeight = 40;
        var rowHeight = data.getNumberOfRows() * 50;
        var chartHeight = rowHeight + paddingHeight;
        var options = {
            'title': 'Beer likes',
            'is3D': true,
            'backgroundColor': {
                'fill': 'transparent',
                stroke: 'white',
                strokeWidth: 3
            },
            height: chartHeight,
            chartArea: {
                height: rowHeight,
            }
        };
        var chart = new google.visualization.PieChart(document.getElementById('chart_div1'));
        chart.draw(data, options);
    } else {
        $('#chart_div1').html("<p class='text-center'>There are no likes yet</p>");

    }
}

function drawChartComments() {
    var jsonData = $.ajax({
        url: "/stats/comments",
        dataType: "json",
        async: false
    }).responseText;
    if (JSON.parse(jsonData).cols.length > 0 && JSON.parse(jsonData).rows.length > 0) {
        var data = new google.visualization.DataTable(jsonData);
        var paddingHeight = 40;
        var rowHeight = data.getNumberOfRows() * 50;
        var chartHeight = rowHeight + paddingHeight;
        var options = {
            'title': 'Beer comments',
            'is3D': true,
            'backgroundColor': {
                'fill': 'transparent',
                stroke: 'white',
                strokeWidth: 3
            },
            height: chartHeight,
            chartArea: {
                height: rowHeight,
            }
        };
        var chart = new google.visualization.PieChart(document.getElementById('chart_div2'));
        chart.draw(data, options);
    } else {
        $('#chart_div2').html("<p class='text-center'>There are no comments yet</p>");
    }
}
