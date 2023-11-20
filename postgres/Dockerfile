FROM postgres:latest

ENV DATABASE_PORT=5433

EXPOSE $DATABASE_PORT

COPY set_timezone.sql /docker-entrypoint-initdb.d/

CMD ["postgres", "-p", "5433", "-c", "shared_buffers=256MB"]
