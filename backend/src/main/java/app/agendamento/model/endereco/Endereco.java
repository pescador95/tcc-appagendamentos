package app.agendamento.model.endereco;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import app.core.utils.StringBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;

@Entity
@Table(name = "endereco")
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted"})

public class Endereco extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "enderecoIdSequence", sequenceName = "endereco_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "enderecoIdSequence")
    @Id
    private Long id;

    @Column()
    private String cep;

    @Column()
    private String logradouro;

    @Column()
    private Long numero;

    @Column()
    private String complemento;

    @Column()
    private String cidade;

    @Column()
    private String estado;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pessoaId")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "organizacaoId")
    private Organizacao organizacao;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    private Usuario usuarioAcao;

    public Endereco() {

    }

    public Endereco(Endereco pEndereco, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pEndereco.getPessoa()) && pEndereco.getPessoa().isValid()) {
            this.setPessoa(pEndereco.getPessoa());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getOrganizacao()) && pEndereco.getOrganizacao().isValid()) {
            this.setOrganizacao(pEndereco.getOrganizacao());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getCep())) {
            this.setCep(pEndereco.getCep());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getLogradouro())) {
            this.setLogradouro(pEndereco.getLogradouro());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getNumero())) {
            this.setNumero(pEndereco.getNumero());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getComplemento())) {
            this.setComplemento(pEndereco.getComplemento());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getCidade())) {
            this.setCidade(pEndereco.getCidade());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getEstado())) {
            this.setEstado(pEndereco.getEstado());
        }
        this.setUsuario(usuarioAuth);
        this.setUsuarioAcao(usuarioAuth);
        this.setAtivo(Boolean.TRUE);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public Endereco endereco(Endereco pEnderecoOld, Endereco pEndereco, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);


        if (BasicFunctions.isNotEmpty(pEndereco.getPessoa()) && pEndereco.getPessoa().isValid()) {
            pEnderecoOld.setPessoa(pEndereco.getPessoa());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getOrganizacao()) && pEndereco.getOrganizacao().isValid()) {
            pEnderecoOld.setOrganizacao(pEndereco.getOrganizacao());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getCep())) {
            pEnderecoOld.setCep(pEndereco.getCep());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getLogradouro())) {
            pEnderecoOld.setLogradouro(pEndereco.getLogradouro());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getNumero())) {
            pEnderecoOld.setNumero(pEndereco.getNumero());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getComplemento())) {
            pEnderecoOld.setComplemento(pEndereco.getComplemento());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getCidade())) {
            pEnderecoOld.setCidade(pEndereco.getCidade());
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getEstado())) {
            pEnderecoOld.setEstado(pEndereco.getEstado());
        }
        pEnderecoOld.setUsuario(usuarioAuth);
        pEnderecoOld.setUsuarioAcao(usuarioAuth);
        pEnderecoOld.setAtivo(Boolean.TRUE);
        pEnderecoOld.setDataAcao(Contexto.dataHoraContexto());
        return pEnderecoOld;
    }

    public Endereco deletarEndereco(Endereco pEndereco, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pEndereco.setUsuario(usuarioAuth);
        pEndereco.setUsuarioAcao(usuarioAuth);
        pEndereco.setAtivo(Boolean.FALSE);
        pEndereco.setSystemDateDeleted(Contexto.dataHoraContexto());

        return pEndereco;
    }

    public Endereco reativarEndereco(Endereco pEndereco, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pEndereco.setUsuario(usuarioAuth);
        pEndereco.setUsuarioAcao(usuarioAuth);
        pEndereco.setAtivo(Boolean.TRUE);
        pEndereco.setSystemDateDeleted(null);

        return pEndereco;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = StringBuilder.makeOnlyNumbers(cep);
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    public void setOrganizacao(Organizacao organizacao) {
        this.organizacao = organizacao;
    }

    public Usuario getUsuarioAcao() {
        return usuarioAcao;
    }

    public void setUsuarioAcao(Usuario usuarioAcao) {
        this.usuarioAcao = usuarioAcao;
    }
}
