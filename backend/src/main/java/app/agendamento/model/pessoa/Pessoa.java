package app.agendamento.model.pessoa;

import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import app.core.utils.StringBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pessoa", indexes = {
        @Index(name = "ipessoaak1", columnList = "nome, telefone, celular, dataNascimento, cpf, ativo")
})
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted"})

public class Pessoa extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "pessoaIdSequence", sequenceName = "pessoa_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "pessoaIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;

    @ManyToOne
    @JoinColumn(name = "generoId")
    private Genero genero;

    @Column()
    private LocalDate dataNascimento;

    @Column()
    private String telefone;

    @Column()
    private String celular;

    @Column()
    private String email;

    @Column()
    private String cpf;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    @Column()
    private Long telegramId;

    @Column()
    private Long whatsappId;

    public Pessoa() {

    }

    public Pessoa(Pessoa pPessoa, Genero genero, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pPessoa.getNome())) {
            this.setNome(pPessoa.getNome());
        }
        if (BasicFunctions.isNotEmpty(genero) && genero.isValid()) {
            this.setGenero(genero);
        }
        if (BasicFunctions.isValid(pPessoa.getDataNascimento())) {
            this.setDataNascimento(pPessoa.getDataNascimento());
        }
        if (BasicFunctions.isValid(pPessoa.getWhatsappId())) {
            this.setWhatsappId(pPessoa.getWhatsappId());
        }
        if (BasicFunctions.isValid(pPessoa.getTelegramId())) {
            this.setTelegramId(pPessoa.getTelegramId());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getTelefone())) {
            this.setTelefone(pPessoa.getTelefone());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getCelular())) {
            this.setCelular(pPessoa.getCelular());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getEmail())) {
            this.setEmail(pPessoa.getEmail());
        }
        this.setCpf(pPessoa.getCpf());
        this.setUsuario(usuarioAuth);
        this.setUsuarioAcao(usuarioAuth);
        this.setAtivo(Boolean.TRUE);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public Pessoa pessoa(Pessoa pPessoaOld, Pessoa pPessoa, Genero genero, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pPessoa.getNome())) {
            pPessoaOld.setNome(pPessoa.getNome());
        }
        if (BasicFunctions.isNotEmpty(genero)) {
            pPessoaOld.setGenero(genero);
        }
        if (BasicFunctions.isValid(pPessoa.getDataNascimento())) {
            pPessoaOld.setDataNascimento(pPessoa.getDataNascimento());
        }
        if (BasicFunctions.isValid(pPessoa.getWhatsappId())) {
            pPessoaOld.setWhatsappId(pPessoa.getWhatsappId());
        }
        if (BasicFunctions.isValid(pPessoa.getTelegramId())) {
            pPessoaOld.setTelegramId(pPessoa.getTelegramId());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getTelefone())) {
            pPessoaOld.setTelefone(pPessoa.getTelefone());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getCelular())) {
            pPessoaOld.setCelular(pPessoa.getCelular());
        }
        if (BasicFunctions.isNotEmpty(pPessoa.getEmail())) {
            pPessoaOld.setEmail(pPessoa.getEmail());
        }
        pPessoaOld.setCpf(pPessoa.getCpf());
        pPessoaOld.setUsuario(usuarioAuth);
        pPessoaOld.setUsuarioAcao(usuarioAuth);
        pPessoaOld.setAtivo(Boolean.TRUE);
        pPessoaOld.setDataAcao(Contexto.dataHoraContexto());

        return pPessoaOld;
    }

    public Boolean cpfJaUtilizado(Pessoa pPessoa) {
        List<Pessoa> pessoasExistentes = Pessoa.list("cpf = ?1 and ativo = true", pPessoa.getCpf());
        return BasicFunctions.isNotEmpty(pessoasExistentes) && pessoasExistentes.stream().anyMatch(pessoa -> !pessoa.getId().equals(pPessoa.getId()));
    }

    public Pessoa deletarPessoa(Pessoa pPessoa, SecurityContext context) {
        Usuario usuarioAuth = Contexto.getContextUser(context);
        pPessoa.setUsuarioAcao(usuarioAuth);
        pPessoa.setAtivo(Boolean.FALSE);
        pPessoa.setSystemDateDeleted(Contexto.dataHoraContexto());
        return pPessoa;
    }

    public Pessoa reativarPessoa(Pessoa pPessoa, SecurityContext context) {
        Usuario usuarioAuth = Contexto.getContextUser(context);
        pPessoa.setUsuarioAcao(usuarioAuth);
        pPessoa.setAtivo(Boolean.TRUE);
        pPessoa.setSystemDateDeleted(null);
        return pPessoa;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = StringBuilder.makeOnlyNumbers(telefone);
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = StringBuilder.makeOnlyNumbers(celular);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = StringBuilder.makeOnlyNumbers(cpf);
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getWhatsappId() {
        return whatsappId;
    }

    public void setWhatsappId(Long whatsappId) {
        this.whatsappId = whatsappId;
    }
}
