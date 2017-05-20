google.charts.load('current', {
    'packages': ['corechart']
});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {
    drawChart1();
    drawChart2();
}

function drawChart1() {
    var jsonData = $.ajax({
        url: "/stats/likes",
        dataType: "json",
        async: false
    }).responseText;
    if (JSON.parse(jsonData).cols.length > 0 && JSON.parse(jsonData).rows.length > 0) {
        var data = new google.visualization.DataTable(jsonData);
        var options = {
            'title': 'Beer likes',
            'is3D': true,
            'backgroundColor': {
                'fill': 'transparent',
                stroke: 'white',
                strokeWidth: 3

            }
        };
        var chart = new google.visualization.PieChart(document.getElementById('chart_div1'));
        chart.draw(data, options);
    } else {
        $('#chart_div1').html("<p class='text-center'>There are no beer likes yet</p>");

    }
}

function drawChart2() {
    var jsonData = $.ajax({
        url: "/stats/comments",
        dataType: "json",
        async: false
    }).responseText;
    if (JSON.parse(jsonData).cols.length > 0 && JSON.parse(jsonData).rows.length > 0) {
        var data = new google.visualization.DataTable(jsonData);
        var options = {
            'title': 'Beer likes',
            'is3D': true,
            'backgroundColor': {
                'fill': 'transparent',
                stroke: 'white',
                strokeWidth: 3

            }
        };
        var chart = new google.visualization.PieChart(document.getElementById('chart_div2'));
        chart.draw(data, options);
    } else {
        $('#chart_div2').html("<p class='text-center'>There are no beer comments yet</p>");
    }
}
