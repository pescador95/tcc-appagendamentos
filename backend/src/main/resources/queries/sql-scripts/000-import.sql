DELETE FROM TIPOAGENDAMENTO CASCADE;

DELETE FROM STATUSAGENDAMENTO CASCADE;

DELETE FROM GENERO CASCADE;

DELETE FROM ROLE CASCADE;

DELETE FROM ENDERECO CASCADE;

DELETE FROM PESSOA CASCADE;

DELETE FROM ORGANIZACAO CASCADE;

DELETE FROM USUARIO CASCADE;

DELETE FROM USUARIOROLES CASCADE;

DELETE FROM USUARIOORGANIZACAO CASCADE;

DELETE FROM HISTORICOPESSOA CASCADE;

DELETE FROM ATENDIMENTO CASCADE;

DELETE FROM TIPOAGENDAMENTOORGANIZACOES CASCADE;

DELETE FROM CONFIGURADORAGENDAMENTO CASCADE;

DELETE FROM CONFIGURADORAGENDAMENTOESPECIAL CASCADE;

DELETE FROM AGENDAMENTO CASCADE;

DELETE FROM CONFIGURADORAUSENCIA CASCADE;

DELETE FROM CONFIGURADORAUSENCIAUSUARIO CASCADE;

DELETE FROM CONFIGURADORFERIADO CASCADE;

DELETE FROM CONTRATO CASCADE;

DELETE FROM TIPOCONTRATO CASCADE;

DELETE FROM CONFIGURADORNOTIFICACAO CASCADE;

SELECT
	SETVAL('agendamento_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			AGENDAMENTO
	));

SELECT
	SETVAL('configuradoragendamento_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONFIGURADORAGENDAMENTO
	));

SELECT
	SETVAL('configuradoragendamentoespecial_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONFIGURADORAGENDAMENTOESPECIAL
	));

SELECT
	SETVAL('configuradorausencia_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONFIGURADORAUSENCIA
	));

SELECT
	SETVAL('configuradorferiado_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONFIGURADORFERIADO
	));

SELECT
	SETVAL('configuradornotificacao_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONFIGURADORNOTIFICACAO
	));

SELECT
	SETVAL('endereco_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			ENDERECO
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
	SETVAL('genero_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			GENERO
	));

SELECT
	SETVAL('historicopessoa_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			HISTORICOPESSOA
	));

SELECT
	SETVAL('organizacao_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			ORGANIZACAO
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
	SETVAL('profile_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			PROFILE
	));

SELECT
	SETVAL('role_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			ROLE
	));

SELECT
	SETVAL('statusagendamento_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			STATUSAGENDAMENTO
	));

SELECT
	SETVAL('tipoagendamento_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			TIPOAGENDAMENTO
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
	SETVAL('contrato_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			CONTRATO
	));

SELECT
	SETVAL('tipocontrato_id_seq',
	(
		SELECT
			MAX(ID)
		FROM
			TIPOCONTRATO
	));