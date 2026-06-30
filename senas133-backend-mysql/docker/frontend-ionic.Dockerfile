# Dockerfile genérico para los frontends Ionic/Angular del proyecto.
# Se usa desde docker-compose.full.yml sin copiar carpetas de frontend al backend.
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build && \
    if [ -d /app/www/browser ]; then cp -r /app/www/browser /app/nginx-html; else cp -r /app/www /app/nginx-html; fi

FROM nginx:alpine
COPY --from=build /app/nginx-html /usr/share/nginx/html
RUN printf '%s\n' \
    'server {' \
    '    listen 80;' \
    '    server_name localhost;' \
    '    root /usr/share/nginx/html;' \
    '    index index.html;' \
    '    location / {' \
    '        try_files $uri $uri/ /index.html;' \
    '    }' \
    '}' > /etc/nginx/conf.d/default.conf
EXPOSE 80
