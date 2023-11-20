#!/bin/bash

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

images=("pescador95/tcc-agendamento:db" "pescador95/tcc-agendamento:redis" "pescador95/tcc-agendamento:quarkus" "pescador95/tcc-agendamento:telegram" "pescador95/tcc-agendamento:whatsapp")

cd "$parent_path"

cd ..

cd ..

cd ..

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    terminal="konsole"
    elif [[ "$OSTYPE" == "msys"* ]]; then
    terminal="start"
fi

docker-compose build

for image in "${images[@]}"
do
    echo "Enviando imagem: $image"
    docker push $image
done