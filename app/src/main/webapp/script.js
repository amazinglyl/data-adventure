/** Call Google Maps API, create a map and add it to the webpage. */
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