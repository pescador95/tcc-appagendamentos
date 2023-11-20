SELECT
  SETVAL('historicopessoa_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      HISTORICOPESSOA
  ));

SELECT
  SETVAL('atendimento_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      ATENDIMENTO
  ));

SELECT
  SETVAL('usuario_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      USUARIO
  ));

SELECT
  SETVAL('pessoa_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      PESSOA
  ));

SELECT
  SETVAL('organizacao_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      ORGANIZACAO
  ));

--select
--	SETVAL('profile_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    PROFILE));
--
--select
--	SETVAL('role_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    ROLE));

--select
--	SETVAL('usuarioroles_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    USUARIOROLES));

SELECT
  SETVAL('genero_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      GENERO
  ));

SELECT
  SETVAL('statusAgendamento_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      STATUSAGENDAMENTO
  ));

SELECT
  SETVAL('tipoAgendamento_id_seq',
  (
    SELECT
      MAX(ID)
    FROM
      TIPOAGENDAMENTO
  ));