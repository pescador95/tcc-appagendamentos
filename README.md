# Agenda  Fácil

Repositório para o backend do Trabalho de Conclusão de Curso referente a Turma de Engenharia de Software FAG de 2020.

# build and Run

Você pode executar o projeto containerizado através do script run.sh...

# Docker Compose tcc-appagendamentos

Simple docker compose tcc-appagendamentos.

# Exemplo de instruções e comandos para usar com o docker.

# Criação Container
#pra criar o container
docker build -t {containername} .


# Execução Container
#pra rodar o container
docker run -it {containername} bash

# Remoção Containers e Imagens
#apagar imagens 
docker image rm -f  `docker images -q`

#apagar containers 
docker container rm -f `docker ps -aq`

#apagar imagens e containers
docker image rm -f  `docker images -q` && docker container rm -f `docker ps -aq`

# Localizar Container
#pegar ip do container rodando
docker inspect {containername} | grep IPAddress

# Executar Compose
#subir docker compose
docker-compose up

#baixar docker compose
docker-compose down


# fazer dump banco de dados:
docker exec -t -u postgres {db_containername} pg_dump -p 5433 -U {db_username} {db_name} > backup.sql

# Apagar Cache
#remover cache do build
docker builder prune --all

# Criar Rede no Docker
docker network create --driver bridge --label 'com.docker.compose.network=default' --label 'com.docker.compose.project={project_name}' internal-network

docker network create --driver bridge --label 'com.docker.compose.network=default' --label 'com.docker.compose.project={project_name}' external-network

Repositório para o backend do Trabalho de Conclusão de Curso referente a Turma de Engenharia de Software FAG de 2020.
