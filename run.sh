#!/bin/bash

current_path=$(dirname "$0")

cd "$current_path"

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    terminal="konsole"
    elif [[ "$OSTYPE" == "msys"* ]]; then
    terminal="start"
fi

arch=$(uname -m)

current_branch=$(git branch --show-current)
echo "Executando o script na branch: $current_branch"
echo "Build and Run - Projeto: tcc-agendamento... ##############"
echo "Executando docker-compose down..."
echo "Apagando imagens e containers..."
docker-compose -p tcc-appagendamento down --volumes
docker-compose -p tcc-appagendamento rm -f
if [[ $(docker images -q pescador95/tcc-agendamento:db) ]]; then
    docker image rm pescador95/tcc-agendamento:db
fi
docker image rm pescador95/tcc-agendamento:quarkus
docker image rm pescador95/tcc-agendamento:telegram
docker image rm pescador95/tcc-agendamento:whatsapp

echo "Criando imagens e containers..."
docker-compose -p tcc-appagendamento -f docker-compose.yml build
echo "Executando docker-compose up..."
docker-compose -p tcc-appagendamento -f docker-compose.yml up -d
echo "Containers em execução: ##################################"
docker ps
echo "##########################################################"
echo "docker-compose Executado com sucesso!"
