### Создание БД
Все операции производятся под пользователем postgres (или любым другим, у которого есть права суперпользователя)
* Создание пользователя 
  ```
  CREATE USER dh_datastorage WITH
       	LOGIN
       	NOSUPERUSER
       	NOCREATEDB
       	NOCREATEROLE
       	INHERIT
       	NOREPLICATION
       	CONNECTION LIMIT -1
       	PASSWORD 'dh_datastorage';
  ```
* Создание БД
  ```
  CREATE DATABASE dh_datastorage
      WITH 
      OWNER = dh_datastorage
      ENCODING = 'UTF8'
      CONNECTION LIMIT = -1;    
  ```
* Права пользователю 
  ```
  GRANT ALL ON DATABASE dh_datastorage TO dh_datastorage;
  ```
* Создать расширение (необходимо подключиться к созданной БД под пользователем postgres, 
  например в psql это можно сделать командой ```\c dh_datastorage```) 
  ```
  drop extension if exists pgcrypto;
  create extension pgcrypto;
  ```