CREATE DATABASE db71u;
\connect db71u;
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;

-- ASSET
CREATE USER asset WITH PASSWORD 'asset'; 
CREATE SCHEMA AUTHORIZATION asset;  
ALTER USER asset SET search_path = asset, public; 
GRANT CONNECT ON DATABASE db71u TO asset;  
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA asset TO asset; 
ALTER SCHEMA asset OWNER TO asset;

-- AUDIT
CREATE USER audit WITH PASSWORD 'audit';  
CREATE SCHEMA AUTHORIZATION audit;  
ALTER USER audit SET search_path = audit, public;  
GRANT CONNECT ON DATABASE db71u TO audit; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA audit TO audit; 
ALTER SCHEMA audit OWNER TO audit; 

-- CONFIG
CREATE USER config WITH PASSWORD 'config'; 
CREATE SCHEMA AUTHORIZATION config;  
ALTER USER config SET search_path = config, public; 
GRANT CONNECT ON DATABASE db71u TO config;  
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA config TO config; 
ALTER SCHEMA config OWNER TO config; 

-- Exchange
CREATE USER exchange WITH PASSWORD 'exchange'; 
CREATE SCHEMA AUTHORIZATION exchange; 
ALTER USER exchange SET search_path = exchange, public; 
GRANT CONNECT ON DATABASE db71u TO exchange; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA exchange TO exchange; 
ALTER SCHEMA exchange OWNER TO exchange; 

-- Movement
CREATE USER movement WITH PASSWORD 'movement'; 
CREATE SCHEMA AUTHORIZATION movement; 
ALTER USER movement SET search_path = movement, public; 
GRANT CONNECT ON DATABASE db71u TO movement; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA movement TO movement; 
ALTER SCHEMA movement OWNER TO movement;

-- Reporting 
CREATE USER reporting WITH PASSWORD 'reporting'; 
CREATE SCHEMA AUTHORIZATION reporting; 
ALTER USER reporting SET search_path = reporting, public; 
GRANT CONNECT ON DATABASE db71u TO reporting; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA reporting TO reporting; 
ALTER SCHEMA reporting OWNER TO reporting; 

-- MovementRules
CREATE USER movementrules WITH PASSWORD 'movementrules'; 
CREATE SCHEMA AUTHORIZATION movementrules; 
ALTER USER movementrules SET search_path = movementrules, public; 
GRANT CONNECT ON DATABASE db71u TO movementrules; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA movementrules TO movementrules; 
ALTER SCHEMA movementrules OWNER TO movementrules; 

-- Spatial
CREATE USER spatial WITH PASSWORD 'spatial'; 
CREATE SCHEMA AUTHORIZATION spatial; 
ALTER USER spatial SET search_path = spatial, public; 
GRANT CONNECT ON DATABASE db71u TO spatial; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA spatial TO spatial; 
ALTER SCHEMA spatial OWNER TO spatial;

-- User
CREATE USER usm WITH PASSWORD 'usm'; 
CREATE SCHEMA AUTHORIZATION usm; 
ALTER USER usm SET search_path = usm, public; 
GRANT CONNECT ON DATABASE db71u TO usm; 
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA usm TO usm; 
ALTER SCHEMA usm OWNER TO usm; 

-- SALES
CREATE USER sales WITH PASSWORD 'sales';
CREATE SCHEMA AUTHORIZATION sales;
ALTER USER sales SET search_path = sales, public;
GRANT CONNECT ON DATABASE db71u TO sales;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA sales TO sales;
ALTER SCHEMA sales OWNER TO sales;

-- SALES ECB
CREATE USER salesecbproxy WITH PASSWORD 'salesecbproxy';
CREATE SCHEMA AUTHORIZATION salesecbproxy;
ALTER USER salesecbproxy SET search_path = salesecbproxy, public;
GRANT CONNECT ON DATABASE db71u TO salesecbproxy;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA salesecbproxy TO salesecbproxy;
ALTER SCHEMA salesecbproxy OWNER TO salesecbproxy;

-- INCIDENT
CREATE USER incident WITH PASSWORD 'incident'; 
CREATE SCHEMA AUTHORIZATION incident;  
ALTER USER incident SET search_path = incident, public; 
GRANT CONNECT ON DATABASE db71u TO incident;  
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA incident TO incident; 
ALTER SCHEMA incident OWNER TO incident;

-- Activity
CREATE USER activity WITH PASSWORD 'activity';
CREATE SCHEMA AUTHORIZATION activity;
ALTER USER activity SET search_path = activity, public;
GRANT CONNECT ON DATABASE db71u TO activity;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA activity TO activity;
ALTER SCHEMA activity OWNER TO activity;
