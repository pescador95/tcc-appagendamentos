FROM node:18

WORKDIR /app

COPY / package*.json ./

RUN npm install

COPY / .

ENV PORT=5000

EXPOSE $PORT

CMD ["node", "--max-old-space-size=512", "index.js"]
