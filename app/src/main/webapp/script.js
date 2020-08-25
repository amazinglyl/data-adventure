/** Call Google Maps API, create a map and add it to the webpage. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {
          center: {lat: -33.8688, lng: 151.2093},
          zoom: 15
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

  addLandmark(
      map, -33.870453, 151.208755, 'Sydney Tower',
      'Sydney Tower is Sydney\'s tallest structure and the second tallest observation tower in the Southern Hemisphere.')
      
}

/** For landscapes, add a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
  // Create a marker.
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  // Create an info window for the marker.
  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}