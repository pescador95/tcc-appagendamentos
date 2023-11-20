package app.agendamento.model.pessoa;

import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;

@Entity
@Table(name = "historicoPessoa", indexes = {
        @Index(name = "ihistoricopessoaak1", columnList = "pessoaId, ativo")
})
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted"})

public class HistoricoPessoa extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "historicopessoaIdSequence", sequenceName = "historicopessoa_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "historicopessoaIdSequence")
    @Id
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column()
    private String medicamentos;

    @Column(columnDefinition = "TEXT")
    private String diagnosticoClinico;

    @Column()
    private String comorbidades;

    @Column()
    private String ocupacao;

    @Column()
    private String responsavelContato;

    @Column()
    private String nomePessoa;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "pessoaId")
    private Pessoa pessoa;

    @ManyToOne

    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    public HistoricoPessoa() {

    }

    public HistoricoPessoa(HistoricoPessoa pHistoricoPessoa, Pessoa pessoa, SecurityContext context) {

       Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pessoa)) {
            this.setPessoa(pessoa);
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getQueixaPrincipal())) {
            this.setQueixaPrincipal(pHistoricoPessoa.getQueixaPrincipal());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getMedicamentos())) {
            this.setMedicamentos(pHistoricoPessoa.getMedicamentos());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getDiagnosticoClinico())) {
            this.setDiagnosticoClinico(pHistoricoPessoa.getDiagnosticoClinico());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getComorbidades())) {
            this.setComorbidades(pHistoricoPessoa.getComorbidades());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getOcupacao())) {
            this.setOcupacao(pHistoricoPessoa.getOcupacao());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getResponsavelContato())) {
            this.setResponsavelContato(pHistoricoPessoa.getResponsavelContato());
        }
        this.setPessoa(pessoa);
        this.setNomePessoa(pessoa.getNome());
        this.setUsuario(usuarioAuth);
        this.setUsuarioAcao(usuarioAuth);
        this.setAtivo(Boolean.TRUE);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public HistoricoPessoa historicoPessoa(HistoricoPessoa historicoPessoaOld, HistoricoPessoa pHistoricoPessoa, Pessoa pessoa, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pessoa)) {
            historicoPessoaOld.setPessoa(pessoa);
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getQueixaPrincipal())) {
            historicoPessoaOld.setQueixaPrincipal(pHistoricoPessoa.getQueixaPrincipal());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getMedicamentos())) {
            historicoPessoaOld.setMedicamentos(pHistoricoPessoa.getMedicamentos());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getDiagnosticoClinico())) {
            historicoPessoaOld.setDiagnosticoClinico(pHistoricoPessoa.getDiagnosticoClinico());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getComorbidades())) {
            historicoPessoaOld.setComorbidades(pHistoricoPessoa.getComorbidades());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getOcupacao())) {
            historicoPessoaOld.setOcupacao(pHistoricoPessoa.getOcupacao());
        }
        if (BasicFunctions.isNotEmpty(pHistoricoPessoa.getResponsavelContato())) {
            historicoPessoaOld.setResponsavelContato(pHistoricoPessoa.getResponsavelContato());
        }
        historicoPessoaOld.setPessoa(pessoa);
        historicoPessoaOld.setNomePessoa(pessoa.getNome());
        historicoPessoaOld.setUsuario(usuarioAuth);
        historicoPessoaOld.setUsuarioAcao(usuarioAuth);
        historicoPessoaOld.setAtivo(Boolean.TRUE);
        historicoPessoaOld.setDataAcao(Contexto.dataHoraContexto());
        return historicoPessoaOld;
    }

    public HistoricoPessoa deletarHistoricoPessoa(HistoricoPessoa pHistoricoPessoa, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);
        pHistoricoPessoa.setUsuarioAcao(usuarioAuth);
        pHistoricoPessoa.setAtivo(Boolean.FALSE);
        pHistoricoPessoa.setDataAcao(Contexto.dataHoraContexto());
        pHistoricoPessoa.setSystemDateDeleted(Contexto.dataHoraContexto());
        return pHistoricoPessoa;
    }
    public HistoricoPessoa reativarHistoricoPessoa(HistoricoPessoa pHistoricoPessoa, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);
        pHistoricoPessoa.setUsuarioAcao(usuarioAuth);
        pHistoricoPessoa.setAtivo(Boolean.TRUE);
        pHistoricoPessoa.setDataAcao(Contexto.dataHoraContexto());
        pHistoricoPessoa.setSystemDateDeleted(null);
        return pHistoricoPessoa;
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

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public void setQueixaPrincipal(String queixaPrincipal) {
        this.queixaPrincipal = queixaPrincipal;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getDiagnosticoClinico() {
        return diagnosticoClinico;
    }

    public void setDiagnosticoClinico(String diagnosticoClinico) {
        this.diagnosticoClinico = diagnosticoClinico;
    }

    public String getComorbidades() {
        return comorbidades;
    }

    public void setComorbidades(String comorbidades) {
        this.comorbidades = comorbidades;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public String getResponsavelContato() {
        return responsavelContato;
    }

    public void setResponsavelContato(String responsavelContato) {
        this.responsavelContato = responsavelContato;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
}
