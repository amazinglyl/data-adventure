/** Call Google Maps API, create a map and add it to the webpage. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 50, lng: -120}, zoom: 15});
}
