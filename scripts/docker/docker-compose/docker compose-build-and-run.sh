#!/bin/bash

current_path=$(dirname "$0")

cd "$current_path"

cd ..

cd ..

cd ..

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    terminal="konsole"
    elif [[ "$OSTYPE" == "msys"* ]]; then
    terminal="start"
fi

arch=$(uname -m)

if [ "$arch" = "x86_64" ]; then
    jdk="amazoncorretto:20.0.2"
    elif [ "$arch" = "aarch64" ]; then
    jdk="amazonlinux:2"
    #jdk="arm64v8/openjdk:17-jdk-slim" para java17
else
    echo "Arquitetura não suportada: $arch"
fi

export IMAGE=$jdk

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

echo "Criando imagens e containers..."
docker-compose -p tcc-appagendamento -f "docker-compose.yml build
echo "Executando docker-compose up..."
docker-compose -p tcc-appagendamento -f "docker-compose.yml up -d
echo "Containers em execução: ##################################"
docker ps
echo "##########################################################"
read -n 1 -s -r -p "docker-compose Executado com sucesso! Pressione qualquer tecla para sair."