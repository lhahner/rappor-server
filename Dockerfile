FROM postgres:12 AS db

ENV POSTGRES_USER=admin \
    POSTGRES_PASSWORD=admin123 \
    POSTGRES_DB=csp_rappor

COPY ./database/schema.sql /docker-entrypoint-initdb.d/schema.sql
COPY ./database/seed.sql   /docker-entrypoint-initdb.d/seed.sql

EXPOSE 5433