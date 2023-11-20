package app.agendamento.model.agendamento;

import app.agendamento.controller.agendamento.AgendamentoController;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento", indexes = {
        @Index(name = "iatendimentoak1", columnList = "dataAtendimento, pessoaId, ativo")
})
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao",
        "systemDateDeleted"})

public class Atendimento extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "atendimentoIdSequence", sequenceName = "atendimento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "atendimentoIdSequence")
    @Id
    private Long id;

    @Column()
    private LocalDateTime dataAtendimento;

    @Column()
    private String atividade;

    @Column(columnDefinition = "TEXT")
    private String evolucaoSintomas;

    @Column(columnDefinition = "TEXT")
    private String avaliacao;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "profissionalId", insertable = false, updatable = false)
    private Usuario profissionalAtendimento;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    @ManyToOne
    @JoinColumn(name = "pessoaId")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "agendamentoId")
    private Agendamento agendamento;

    public Atendimento() {

    }

    public Atendimento(Atendimento pAtendimento, Long agendamentoId, Pessoa pessoa, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if(BasicFunctions.isValid(agendamentoId)){

            Agendamento agendamentoAtendimento = Agendamento.findById(agendamentoId);

            if (BasicFunctions.isNotEmpty(agendamentoAtendimento) && agendamentoAtendimento.isValid()) {
                AgendamentoController.setStatusAgendamentoAtendidoByAgendamento(agendamentoAtendimento);
                this.setAgendamento(agendamentoAtendimento);
            }
        }
        this.dataAtendimento = pAtendimento.getDataAtendimento();
        this.profissionalAtendimento = usuarioAuth;
        this.atividade = pAtendimento.getAtividade();
        this.evolucaoSintomas = pAtendimento.getEvolucaoSintomas();
        this.avaliacao = pAtendimento.getAvaliacao();
        this.pessoa = pessoa;

        this.usuarioAcao = usuarioAuth;
        this.ativo = Boolean.TRUE;
        this.usuarioAcao = Contexto.getContextUser(context);
        this.dataAcao = Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault());
    }

    public Atendimento atendimento(Atendimento pAtendimentoOld, Atendimento pAtendimento, Long agendamentoId, Pessoa pessoa, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if(BasicFunctions.isValid(agendamentoId)){

            Agendamento agendamentoAtendimento = Agendamento.findById(agendamentoId);

            if (BasicFunctions.isNotEmpty(agendamentoAtendimento) && agendamentoAtendimento.isValid()) {
                AgendamentoController.setStatusAgendamentoAtendidoByAgendamento(agendamentoAtendimento);
                pAtendimentoOld.setAgendamento(agendamentoAtendimento);
            }
        }
        pAtendimentoOld.dataAtendimento = pAtendimento.getDataAtendimento();
        pAtendimentoOld.profissionalAtendimento = usuarioAuth;
        pAtendimentoOld.atividade = pAtendimento.getAtividade();
        pAtendimentoOld.evolucaoSintomas = pAtendimento.getEvolucaoSintomas();
        pAtendimentoOld.avaliacao = pAtendimento.getAvaliacao();
        pAtendimentoOld.pessoa = pessoa;

        pAtendimentoOld.usuarioAcao = usuarioAuth;
        pAtendimentoOld.ativo = Boolean.TRUE;
        pAtendimentoOld.usuarioAcao = Contexto.getContextUser(context);
        pAtendimentoOld.dataAcao = Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault());
        return pAtendimentoOld;
    }

    public Atendimento deletarAtendimento(Atendimento atendimento, SecurityContext context) {
        Usuario usuarioAuth = Contexto.getContextUser(context);


        atendimento.setUsuarioAcao(usuarioAuth);
        atendimento.setAtivo(Boolean.FALSE);
        atendimento.setDataAcao(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()));
        atendimento.setSystemDateDeleted(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()));
        this.usuarioAcao = Contexto.getContextUser(context);
        this.dataAcao = Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault());
        return atendimento;
    }

    public Atendimento reativarAgendimento(Atendimento atendimento, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        atendimento.setUsuarioAcao(usuarioAuth);
        atendimento.setAtivo(Boolean.TRUE);
        atendimento.setDataAcao(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()));
        atendimento.setSystemDateDeleted(null);
        this.usuarioAcao = Contexto.getContextUser(context);
        this.dataAcao = Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault());
        return atendimento;
    }
    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Usuario getProfissionalAtendimento() {
        return profissionalAtendimento;
    }

    public void setProfissionalAtendimento(Usuario profissionalAtendimento) {
        this.profissionalAtendimento = profissionalAtendimento;
    }

    public Usuario getUsuarioAcao() {
        return usuarioAcao;
    }

    public void setUsuarioAcao(Usuario usuarioAcao) {
        this.usuarioAcao = usuarioAcao;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public LocalDateTime getSystemDateDeleted() {
        return systemDateDeleted;
    }

    public void setSystemDateDeleted(LocalDateTime systemDateDeleted) {
        this.systemDateDeleted = systemDateDeleted;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }
}
