/**
 * Map related functions.
 */

/** Call Google Maps API, create a map and add landmarks. */
function createMap() {
    // Add a map.
    const map = new google.maps.Map(
        document.getElementById('map'),
        {
            center: {lat: -33.8688, lng: 151.2093},
            zoom: 12
        });

    // Add a marker.
    const trexMarker = new google.maps.Marker({
        position: {lat: -33.8688, lng: 151.2093},
        map: map,
        title: 'Welcome vistors!'
    });

    // Add an info window.
    const trexInfoWindow =
        new google.maps.InfoWindow({content: 'This is Sydney.'});
    trexInfoWindow.open(map, trexMarker);

    // For each landmark, show an info window when clicked.
    fetch('/landmarks').then(response => response.json()).then((landmarks) => {
        landmarks.forEach((landmark) => {
            addLandmarkToMap(map, landmark)
        });
    });  

}

/** For landscapes, add a marker that shows an info window when clicked. */
function addLandmarkToMap(map, landmark) {
  // Create a marker.
  const marker = new google.maps.Marker({
    position: {lat: landmark.lat, lng: landmark.lng},
    map: map, 
    title: landmark.title
  });

  // Create an info window for the marker.
  const contentString = landmark.title + 
        ': ' + landmark.description;

  const infoWindow = new google.maps.InfoWindow({content: contentString});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

/**
 * Charts related functions.
 */
google.charts.load('current', {'packages':['corechart']});
// google.charts.setOnLoadCallback(drawVistorsChart);
google.charts.setOnLoadCallback(drawCovid19Chart);

// /** Fetches landmark-info and uses it to create a chart. */
// function drawChart() {
//   // Num. visitors chart.
//   drawVistorsChart();

//   // Covid19 chart
//   drawCovid19Chart();
// }

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
  drawCharts ('/temperature-query', 'Temperature', 'temp');
  drawCharts ('/latitude-query', 'Latitude', 'latitude');
}

// Draw charts for the data from the given queryName.
function drawCharts(queryName, tableKey, chartPrefix) {
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
    createLineChart(confirmedData, 'Num. confirmed cases in the past one week', chartPrefix + '-confirmed');
    createLineChart(deceasedData, 'Num. deceased cases in the past one week', chartPrefix + '-deceased');
    createLineChart(recoveredData, 'Num. recovered cases in the past one week', chartPrefix + '-recovered');
    createLineChart(testedData, 'Num. tested cases in the past one week', chartPrefix + '-tested');
  });
}

function createTwoColumnDataTable(colOneName, colTwoName) {
  const dataTable = new google.visualization.DataTable();
  dataTable.addColumn('number', colOneName);
  dataTable.addColumn('number', colTwoName);
  return dataTable;
}

function createLineChart(data, title, name) {
  const options = {
      'legend':'none',
      'title': title,
      'width': 600,
      'height':300
    };

  const chart = new google.visualization.LineChart(
        document.getElementById(name));
  chart.draw(data, options);
}