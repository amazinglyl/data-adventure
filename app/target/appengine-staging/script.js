/**
 * Charts related functions.
 */
google.charts.load('current', {'packages':['corechart', 'controls']});
// google.charts.setOnLoadCallback(drawVistorsChart);
google.charts.setOnLoadCallback(drawCovid19Chart);

/** Fetches landmark-info and uses it to create a chart. */
function drawVistorsChart() {
  // Num. visitors chart.
  fetch('/landmark-info').then(response => response.json())
  .then((vistorsByYear) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Year');
    data.addColumn('number', 'Num. vistors');
    Object.keys(vistorsByYear).forEach((year) => {
      data.addRow([year, vistorsByYear[year]]);
    });

    const options = {
      'title': 'Number of visitors each year',
      'width':600,
      'height':300
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('chart'));
    chart.draw(data, options);
  });
}

/** Covid19 charts. */
function drawCovid19Chart() {
  drawTotalConfirmedDashboard('/temperature-total-query', 'Temperature', 'temperature (celsius)', 'temp');
  drawTotalConfirmedDashboard('/latitude-total-query', 'Latitude', 'latitude', 'latitude');
  drawCharts('/temperature-query', 'Temperature', 'temperature (celsius)', 'temp');
  drawCharts('/latitude-query', 'Latitude', 'latitude', 'latitude');
}

// Draw dashboard for total confirmed
function drawTotalConfirmedDashboard(queryName, tableKey, hAxisTitle, chartPrefix) {
  fetch(queryName).then(response => response.json())
  .then((casesByKey) => {
    
    // Create data tables.
    const data = createTwoColumnDataTable(tableKey, 'Total confirmed');
   
    Object.keys(casesByKey).forEach((key) => {
      key = Number(key);
      cases = casesByKey[key];
      data.addRow([key, cases.confirmed]);
    });

    // Create a dashboard.
    var dashboard = new google.visualization.Dashboard(
        document.getElementById(chartPrefix + '-dashboard'));

    // Create a range slider, passing some options
    var keyRangeSlider = new google.visualization.ControlWrapper({
      'controlType': 'NumberRangeFilter',
      'containerId': chartPrefix + '-total-confirmed-filter',
      'options': {
        'filterColumnLabel': tableKey
      }
    });

    // Create a line chart, passing some options.
    const dashboardOptions = createOptions('Total confirmed', hAxisTitle)
    var lineChart = new google.visualization.ChartWrapper({
      chartType: 'LineChart',
      containerId: chartPrefix + '-total-confirmed-dash',
      options: dashboardOptions
    });

    dashboard.bind(keyRangeSlider, lineChart);
    dashboard.draw(data);
  });
}


// Draw charts for the data from the given queryName.
function drawCharts(queryName, tableKey, hAxisTitle, chartPrefix) {
  fetch(queryName).then(response => response.json())
  .then((casesByKey) => {
    
    // Create data tables.
    const confirmedData = createTwoColumnDataTable(tableKey, 'New confirmed');
    const deceasedData = createTwoColumnDataTable(tableKey,  'New deceased');
    const recoveredData = createTwoColumnDataTable(tableKey, 'New recovered');
    const testedData = createTwoColumnDataTable(tableKey, 'New tested');

    Object.keys(casesByKey).forEach((key) => {
      key = Number(key);
      cases = casesByKey[key];
      confirmedData.addRow([key, cases.confirmed]);
      deceasedData.addRow([key, cases.deceased]);
      recoveredData.addRow([key, cases.recovered]);
      testedData.addRow([key, cases.tested]);
    });

    // Create charts.
    createLineChart(confirmedData, 'confirmed', hAxisTitle, chartPrefix + '-confirmed');
    createLineChart(deceasedData, 'deceased', hAxisTitle, chartPrefix + '-deceased');
    createLineChart(recoveredData, 'recovered', hAxisTitle, chartPrefix + '-recovered');
    createLineChart(testedData, 'tested', hAxisTitle, chartPrefix + '-tested');
  });
}

function createTwoColumnDataTable(colOneName, colTwoName) {
  const dataTable = new google.visualization.DataTable();
  dataTable.addColumn('number', colOneName);
  dataTable.addColumn('number', colTwoName);
  return dataTable;
}

function createLineChart(data, title, hAxisTitle, name) {
  const options = createOptions(title, hAxisTitle);

  const chart = new google.visualization.LineChart(
        document.getElementById(name));
  chart.draw(data, options);
}

function createOptions(title, hAxisTitle) {
  return {
      legend:'none',
      title: title,
      width: 600,
      height: 300,
      hAxis : {
        title: hAxisTitle,
      },
      vAxis : {
        title: 'Num. cases',
      }
    };
}
