package app.agendamento.DTO.agendamento;


import app.agendamento.model.agendamento.Atendimento;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtendimentoDTO {

    private Long id;
    private LocalDateTime dataAtendimento;

    private String atividade;
    private String evolucaoSintomas;

    private String avaliacao;
    private Map<String, Object> pessoaAtendimento;
    private Map<String, Object> profissionalAtendimento;

    public AtendimentoDTO(){

    }

    public AtendimentoDTO(Long id,
     LocalDateTime dataAtendimento,
     String atividade,
     String evolucaoSintomas,
                          String avaliacao,
                          Pessoa pessoaAtendimento,
                          Usuario profissionalAtendimento) {
            this.id = id;
            this.dataAtendimento = dataAtendimento;
            this.atividade = atividade;
            this.evolucaoSintomas = evolucaoSintomas;
            this.avaliacao = avaliacao;

        this.pessoaAtendimento = new HashMap<>();
        this.pessoaAtendimento.put("id", pessoaAtendimento.getId());
        this.pessoaAtendimento.put("nome", pessoaAtendimento.getNome());
        this.pessoaAtendimento.put("celular", pessoaAtendimento.getCelular());

        this.profissionalAtendimento = new HashMap<>();
        this.profissionalAtendimento.put("id", profissionalAtendimento.getId());
        this.profissionalAtendimento.put("nome", profissionalAtendimento.getPessoa().getNome());
    }

    public static List<AtendimentoDTO> makeListAtendimentoDTO(List<Atendimento> pAtendimento) {

        List<AtendimentoDTO> listAtendimentoDTO = new ArrayList<>();

        pAtendimento.forEach(atendimento -> {

                AtendimentoDTO atendimentoDTO = new AtendimentoDTO();

            atendimentoDTO.id = atendimento.getId();
            atendimentoDTO.dataAtendimento = atendimento.getDataAtendimento();
            atendimentoDTO.atividade = atendimento.getAtividade();
            atendimentoDTO.evolucaoSintomas = atendimento.getEvolucaoSintomas();
            atendimentoDTO.avaliacao = atendimento.getAvaliacao();
            if (BasicFunctions.isNotEmpty(atendimento.getProfissionalAtendimento())) {
                atendimentoDTO.profissionalAtendimento = new HashMap<>();
                atendimentoDTO.profissionalAtendimento.put("id", atendimento.getProfissionalAtendimento().getId());
                atendimentoDTO.profissionalAtendimento.put("nome", atendimento.getProfissionalAtendimento().getPessoa().getNome());
                }
            if (BasicFunctions.isNotEmpty(atendimento.getPessoa())) {
                atendimentoDTO.pessoaAtendimento = new HashMap<>();
                atendimentoDTO.pessoaAtendimento.put("id", atendimento.getPessoa().getId());
                atendimentoDTO.pessoaAtendimento.put("nome", atendimento.getPessoa().getNome());
                atendimentoDTO.pessoaAtendimento.put("celular", atendimento.getPessoa().getCelular());
                }
                listAtendimentoDTO.add(atendimentoDTO);
        });

        return listAtendimentoDTO;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDateTime dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getAtividade() {
        return atividade;
    }

    public void setAtividade(String atividade) {
        this.atividade = atividade;
    }

    public String getEvolucaoSintomas() {
        return evolucaoSintomas;
    }

    public void setEvolucaoSintomas(String evolucaoSintomas) {
        this.evolucaoSintomas = evolucaoSintomas;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Map<String, Object> getPessoaAtendimento() {
        return pessoaAtendimento;
    }

    public void setPessoaAtendimento(Map<String, Object> pessoaAtendimento) {
        this.pessoaAtendimento = pessoaAtendimento;
    }

    public Map<String, Object> getProfissionalAtendimento() {
        return profissionalAtendimento;
    }

    public void setProfissionalAtendimento(Map<String, Object> profissionalAtendimento) {
        this.profissionalAtendimento = profissionalAtendimento;
    }
}