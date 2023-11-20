QCONFIGURADORAGENDAMENTOBYORGANIZACAOPROFISSIONAL

SELECT
    C
FROM
    CONFIGURADORAGENDAMENTO C
    JOIN ORGANIZACAO O
    ON C.ORGANIZACAOID = O.ID
    AND O.ATIVO = TRUE JOIN USUARIO U
    ON U.ID = C.PROFISSIONALID
    AND U.ATIVO = TRUE
WHERE
    O.ID = :ORGANIZACAOID
    AND U.ID = :PROFISSIONALID QLISTCONFIGURADORAGENDAMENTOBYORGANIZACAO
    SELECT
        C
    FROM
        CONFIGURADORAGENDAMENTO C
        JOIN ORGANIZACAO O
        ON C.ORGANIZACAOID = O.ID
        AND O.ATIVO = TRUE
    WHERE
        O.ID = :ORGANIZACAOID