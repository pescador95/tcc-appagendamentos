INSERT INTO public.configuradornotificacao
        (id, dataacao, dataintervalo, horaminutointervalo, mensagem, usuarioid)
VALUES(1, now(), 1, '01:00', 'Olá, NOME!\n\nVocê tem um agendamento de TIPOAGENDAMENTO marcado para DIA SEMANA às HORARIO na EMPRESA.\n \n Endereço: ENDERECO\n Contato: CONTATO \n Profissional: PROFISSIONAL \n Data do Agendamento: DATA \n Horário do Agendamento: HORARIO\n \n Atensiosamente, \n EMPRESA.', 1);

INSERT INTO public.configuradornotificacao
        (id, dataacao, dataintervalo, horaminutointervalo, mensagem, usuarioid)
VALUES(2, now(), 0, '01:00', 'Olá, NOME!\n\nVocê tem um agendamento de TIPOAGENDAMENTO marcado para DIA SEMANA às HORARIO na EMPRESA.\n \n Endereço: ENDERECO\n Contato: CONTATO \n Profissional: PROFISSIONAL \n Data do Agendamento: DATA \n Horário do Agendamento: HORARIO\n \n Atensiosamente, \n EMPRESA.', 1);