
--Desconecta usuários ativos no BD--
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'teste'
  AND pid <> pg_backend_pid();

DROP DATABASE IF EXISTS teste;

DROP ROLE IF EXISTS usuario;

CREATE ROLE usuario LOGIN
  UNENCRYPTED PASSWORD 'senha'
  SUPERUSER INHERIT CREATEDB CREATEROLE REPLICATION;

CREATE DATABASE teste
  WITH OWNER = usuario
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'pt_BR.UTF-8'
       LC_CTYPE = 'pt_BR.UTF-8'
       CONNECTION LIMIT = -1;
