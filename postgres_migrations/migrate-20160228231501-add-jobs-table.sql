CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE jobs(
       id uuid DEFAULT uuid_generate_v4() NOT NULL,
       status varchar(100) NOT NULL,
       created_at timestamp without time zone DEFAULT now() NOT NULL,
       updated_at timestamp without time zone DEFAULT now() NOT NULL,
       body text);            

ALTER TABLE ONLY jobs ADD CONSTRAINT jobs_pkey PRIMARY KEY(id);       
