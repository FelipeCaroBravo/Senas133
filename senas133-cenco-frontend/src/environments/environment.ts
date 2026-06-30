export const environment = {
  production: false,

  // Backend Spring Boot.
  // Si el panel CENCO corre en otro equipo, reemplaza localhost por la IP del computador del backend.
  apiUrl: 'http://localhost:8080/api',

  // Centro inicial del mapa: Concepción, Chile.
  defaultMapCenter: {
    lat: -36.82699,
    lng: -73.04977
  },
  defaultZoom: 13
};
