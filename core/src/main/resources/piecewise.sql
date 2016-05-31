-- Table: public.tester

-- DROP TABLE public.tester;

CREATE TABLE public.piecewise
(
  edgeId double precision,
  timeDay double precision,
  totalTime double precision
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tester
  OWNER TO postgres;