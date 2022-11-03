import { precacheAndRoute } from "workbox-precaching";
precacheAndRoute(self.__WB_MANIFEST);

var CACHE_NAME = 'user';
var urlsToPrefetch = [
  'http://localhost:8082/user/get'
];

self.addEventListener('install', function (event) {
  // Perform install steps
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('Opened cache');
        // Magic is here. Look the  mode: 'no-cors' part.
        cache.addAll(urlsToPrefetch.map(function (urlToPrefetch) {
          return new Request(urlToPrefetch);
        })).then(function () {
          console.log('All resources have been fetched and cached.');
        });
      })
  );
});

self.addEventListener('fetch', (event) => {
  // Check if this is a navigation request
  if (event.request.url === 'http://localhost:8082/user/get') {
    // Open the cache
    event.respondWith(caches.open(CACHE_NAME).then((cache) => {
      // Go to the network first
      return fetch(event.request.url).then((fetchedResponse) => {
        console.log("Serving from Network");
        cache.put(event.request, fetchedResponse.clone());
        return fetchedResponse;
      }).catch(() => {
        console.log("Serving from Cache");
        // If the network is unavailable, get
        return cache.match(event.request.url);
      });
    }));
  } else {
    return;
  }
});