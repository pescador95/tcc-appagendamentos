INSERT INTO CONFIGURADORAGENDAMENTOESPECIAL (
  ID,
  NOME,
  PROFISSIONALID,
  DATAINICIO,
  DATAFIM,
  ORGANIZACAOID,
  USUARIOID,
  DATAACAO
) VALUES (
  1,
  'Semana de Ozonoterapia',
  1,
  '2023-04-13',
  '2023-04-14',
  2,
  1,
  NOW()
);

INSERT INTO CONFIGURADORAGENDAMENTOESPECIAL (
  ID,
  DATAACAO,
  DATAFIM,
  DATAINICIO,
  NOME,
  ORGANIZACAOID,
  PROFISSIONALID,
  USUARIOID
) VALUES(
  2,
  '2023-04-30 00:41:28.551',
  '2023-04-30',
  '2023-04-10',
  'Semana de Osteopatia',
  1,
  2,
  1
);