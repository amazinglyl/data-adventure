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
google.charts.setOnLoadCallback(drawChart);

/** Fetches landmark-info and uses it to create a chart. */
function drawChart() {
  // Num. visitors chart.
  drawVistorsChart();

  // Covid19 chart
  drawCovid19Chart();
}

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
  fetch('/query').then(response => response.json())
  .then((confirmedByTemp) => {
    const data = new google.visualization.DataTable();
    data.addColumn('number', 'Temperature');
    data.addColumn('number', 'Cumulative confirmed');
    Object.keys(confirmedByTemp).forEach((temp) => {
      data.addRow([temp, confirmedByTemp[temp]]);
    });

    const options = {
      'title': 'Number of cumalative confirmed cases per temperature',
      'width':600,
      'height':300
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('chart'));
    chart.draw(data, options);
  });
}