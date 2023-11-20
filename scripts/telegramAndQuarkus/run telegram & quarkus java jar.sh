#!/bin/bash

current_path=$(dirname "$0")

cd "$current_path"

cd ..

cd ..

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    terminal="konsole"
    elif [[ "$OSTYPE" == "msys"* ]]; then
    terminal="start"
fi

remote=$(git config --get remote.origin.url)
if [[ "$remote" =~ .*github\.com.* ]]; then
    remote_branch="main"
else
    remote_branch="master"
fi

current_branch=$(git branch --show-current)

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    $terminal -e "bash -c 'cd tcc-appagendamentos;cd backend;cd target;cd quarkus-app;java -jar quarkus-run.jar;exec bash'" &
    $terminal -e "bash -c 'cd telegram;npm run dev;exec bash'" &
    
    elif [[ "$OSTYPE" == "msys"* ]]; then
    $terminal bash -c "cd tcc-appagendamentos;cd backend;cd target;cd quarkus-app;java -jar quarkus-run.jar;cmd /K" &
    $terminal bash -c "cd telegram;npm run dev;cmd /K" &
fi