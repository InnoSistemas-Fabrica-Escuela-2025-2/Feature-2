-- Create the schema using quoted uppercase identifier so H2 matches the unquoted DDL that
-- Hibernate emits (unquoted identifiers are uppercased by H2). This avoids "schema not found"
-- warnings caused by casing mismatch.
CREATE SCHEMA IF NOT EXISTS "AUTHENTICATOR";
-- Fallback: also create the unquoted form (H2 uppercases unquoted names, but creating both
-- is cheap and avoids subtle timing/casing issues across environments).
CREATE SCHEMA IF NOT EXISTS authenticator;
