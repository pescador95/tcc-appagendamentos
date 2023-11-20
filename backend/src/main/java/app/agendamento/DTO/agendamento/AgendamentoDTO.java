package app.agendamento.DTO.agendamento;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.StatusAgendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import app.core.utils.StringBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendamentoDTO {

    private Long id;

    private LocalDate dataAgendamento;
    private LocalTime horarioAgendamento;

    private String endereco;
    private Map<String, Object> pessoaAgendamento;

    private Map<String, Object> organizacaoAgendamento;

    private Map<String, Object> profissionalAgendamento;

    private Map<String, Object> tipoAgendamento;

    private Map<String, Object> statusAgendamento;

    public AgendamentoDTO() {

    }

    public AgendamentoDTO(Long id,
                          LocalDate dataAgendamento,
                          LocalTime horarioAgendamento,
                          Pessoa pessoaAgendamento,
                          Organizacao organizacaoAgendamento,
                          Usuario profissionalAgendamento,
                          TipoAgendamento tipoAgendamentos,
                          StatusAgendamento statusAgendamento) {
        this.id = id;
        this.dataAgendamento = dataAgendamento;
        this.horarioAgendamento = horarioAgendamento;

        this.organizacaoAgendamento = new HashMap<>();
        this.organizacaoAgendamento.put("id", organizacaoAgendamento.getId());
        this.organizacaoAgendamento.put("nome", organizacaoAgendamento.getNome());
        this.organizacaoAgendamento.put("celular", organizacaoAgendamento.getCelular());
        this.organizacaoAgendamento.put("endereco", StringBuilder.makeEnderecoString(organizacaoAgendamento));

        this.pessoaAgendamento = new HashMap<>();
        this.pessoaAgendamento.put("id", pessoaAgendamento.getId());
        this.pessoaAgendamento.put("nome", pessoaAgendamento.getNome());
        this.pessoaAgendamento.put("telefone", pessoaAgendamento.getTelefone());
        this.pessoaAgendamento.put("celular", pessoaAgendamento.getCelular());

        this.profissionalAgendamento = new HashMap<>();
        this.profissionalAgendamento.put("id", profissionalAgendamento.getId());
        this.profissionalAgendamento.put("nome", profissionalAgendamento.getNomeProfissional());

        this.tipoAgendamento = new HashMap<>();
        this.tipoAgendamento.put("id", tipoAgendamentos.getId());
        this.tipoAgendamento.put("tipoAgendamento", tipoAgendamentos.getTipoAgendamento());

        this.statusAgendamento = new HashMap<>();
        this.statusAgendamento.put("id", statusAgendamento.getId());
        this.statusAgendamento.put("status", statusAgendamento.getStatus());

    }

    public static List<AgendamentoDTO> makeListAgendamentoDTO(List<Agendamento> pAgendamento) {

        List<AgendamentoDTO> listAgendamentoDTO = new ArrayList<>();

        pAgendamento.forEach(agendamento -> {
            AgendamentoDTO agendamentoDTO = new AgendamentoDTO();
            agendamentoDTO.id = agendamento.getId();
            if (BasicFunctions.isNotEmpty(agendamento.getTipoAgendamento())) {
                agendamentoDTO.tipoAgendamento = new HashMap<>();
                agendamentoDTO.tipoAgendamento.put("id", agendamento.getTipoAgendamento().getId());
                agendamentoDTO.tipoAgendamento.put("tipoAgendamento", agendamento.getTipoAgendamento().getTipoAgendamento());
            }
            if (BasicFunctions.isNotEmpty(agendamento.getPessoaAgendamento())) {
                agendamentoDTO.pessoaAgendamento = new HashMap<>();
                agendamentoDTO.pessoaAgendamento.put("id", agendamento.getPessoaAgendamento().getId());
                agendamentoDTO.pessoaAgendamento.put("nome", agendamento.getPessoaAgendamento().getNome());
                agendamentoDTO.pessoaAgendamento.put("telefone", agendamento.getPessoaAgendamento().getTelefone());
                agendamentoDTO.pessoaAgendamento.put("celular", agendamento.getPessoaAgendamento().getCelular());
            }
            if (BasicFunctions.isNotEmpty(agendamento.getProfissionalAgendamento())) {
                agendamentoDTO.profissionalAgendamento = new HashMap<>();
                agendamentoDTO.profissionalAgendamento.put("id", agendamento.getProfissionalAgendamento().getId());
                agendamentoDTO.profissionalAgendamento.put("nome", agendamento.getProfissionalAgendamento().getNomeProfissional());
            }
            if (BasicFunctions.isNotEmpty(agendamento.getOrganizacaoAgendamento())) {
                if (BasicFunctions.isNotEmpty(agendamento.getOrganizacaoAgendamento())) {
                    agendamentoDTO.endereco = StringBuilder.makeEnderecoString(agendamento.getOrganizacaoAgendamento());
                    agendamentoDTO.organizacaoAgendamento = new HashMap<>();
                    agendamentoDTO.organizacaoAgendamento.put("id", agendamento.getOrganizacaoAgendamento().getId());
                    agendamentoDTO.organizacaoAgendamento.put("nome", agendamento.getOrganizacaoAgendamento().getNome());
                    agendamentoDTO.organizacaoAgendamento.put("celular", agendamento.getOrganizacaoAgendamento().getCelular());
                    agendamentoDTO.organizacaoAgendamento.put("endereco", StringBuilder.makeEnderecoString(agendamento.getOrganizacaoAgendamento()));
                }
            }
            if (BasicFunctions.isNotEmpty(agendamento.getStatusAgendamento())) {
                agendamentoDTO.statusAgendamento = new HashMap<>();
                agendamentoDTO.statusAgendamento.put("id", agendamento.getStatusAgendamento().getId());
                agendamentoDTO.statusAgendamento.put("status", agendamento.getStatusAgendamento().getStatus());
            }
            agendamentoDTO.dataAgendamento = agendamento.getDataAgendamento();
            agendamentoDTO.horarioAgendamento = agendamento.getHorarioAgendamento();
            listAgendamentoDTO.add(agendamentoDTO);
        });
        return listAgendamentoDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHorarioAgendamento() {
        return horarioAgendamento;
    }

    public void setHorarioAgendamento(LocalTime horarioAgendamento) {
        this.horarioAgendamento = horarioAgendamento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Map<String, Object> getPessoaAgendamento() {
        return pessoaAgendamento;
    }

    public void setPessoaAgendamento(Map<String, Object> pessoaAgendamento) {
        this.pessoaAgendamento = pessoaAgendamento;
    }

    public Map<String, Object> getOrganizacaoAgendamento() {
        return organizacaoAgendamento;
    }

    public void setOrganizacaoAgendamento(Map<String, Object> organizacaoAgendamento) {
        this.organizacaoAgendamento = organizacaoAgendamento;
    }

    public Map<String, Object> getProfissionalAgendamento() {
        return profissionalAgendamento;
    }

    public void setProfissionalAgendamento(Map<String, Object> profissionalAgendamento) {
        this.profissionalAgendamento = profissionalAgendamento;
    }

    public Map<String, Object> getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(Map<String, Object> tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }

    public Map<String, Object> getStatusAgendamento() {
        return statusAgendamento;
    }

    public void setStatusAgendamento(Map<String, Object> statusAgendamento) {
        this.statusAgendamento = statusAgendamento;
    }
}
