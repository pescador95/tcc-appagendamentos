FROM node:18


RUN set -x \
    && apt-get update \
    && apt-get install -y \
    chromium \
    build-essential \
    libvips-dev

WORKDIR /app

COPY / package*.json ./

RUN npm install --platform=linux --arch=arm64 sharp
#RUN npm install --platform=linux --arch=x64 sharp

COPY / .

ENV PORT=4000

EXPOSE $PORT

CMD ["node", "--max-old-space-size=512", "index.js"]