#!/bin/sh

#psql -h 127.0.0.1 < create-user-and-db.sql
cat create-user-and-db.sql | sudo su - postgres -c psql

export PGDATABASE='teste'
export PGUSER='usuario'
export PGPASSWORD='senha'

psql -h 127.0.0.1 < generate-database.sql
psql -h 127.0.0.1 -e < inserir-cursos.sql
