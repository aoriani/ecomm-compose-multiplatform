const CACHE = 'eCommerceApp-cache-v2';
const PRECACHE = [
  '.',
  'index.html',
  'styles.css',
  'composeApp.js',
  'favicon.ico',
  'icon-192.png',
  'icon-512.png',
  'icon-192-maskable.png',
  'icon-512-maskable.png',
  'apple-touch-icon.png',
];

self.addEventListener('install', (event) => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE).then((cache) => cache.addAll(PRECACHE))
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', (event) => {
  const req = event.request;

  // Offline navigation fallback for SPA/Compose apps
  if (req.mode === 'navigate') {
    event.respondWith((async () => {
      try { return await fetch(req); } catch {
        const cache = await caches.open(CACHE);
        const cached = await cache.match('index.html');
        return cached || Response.error();
      }
    })());
    return;
  }

  // Cache-first for static assets
  event.respondWith(
    caches.match(req).then((cached) => cached || fetch(req))
  );
});
