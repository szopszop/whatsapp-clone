# Stage 1: Build the Angular application
FROM node:20-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build --prod

# Stage 2: Serve the application with Nginx
FROM nginx:alpine
RUN apk add --no-cache nodejs

COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY env.js /usr/local/bin/env.js

EXPOSE 80

# Create a startup script that runs env.js and then starts nginx
RUN echo '#!/bin/sh\nnode /usr/local/bin/env.js\nnginx -g "daemon off;"' > /usr/local/bin/start.sh \
    && chmod +x /usr/local/bin/start.sh

CMD ["/usr/local/bin/start.sh"]
