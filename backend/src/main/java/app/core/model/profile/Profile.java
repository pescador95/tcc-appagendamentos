package app.core.model.profile;

import app.agendamento.model.pessoa.HistoricoPessoa;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile")

public class Profile extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "profileIdSequence", sequenceName = "profile_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "profileIdSequence")
    @Id
    private Long id;

    @Column()
    private String originalName;

    @Column()
    private String keyName;

    @Column()
    private String mimetype;

    @Column()
    private LocalDateTime dataCriado;

    @Column()
    private Long fileSize;

    @ManyToOne()
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "historicopessoaId")
    private HistoricoPessoa historicoPessoa;
    @Column()
    private String fileReference;

    @Column()
    private String nomeCliente;

    public Profile() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public LocalDateTime getDataCriado() {
        return dataCriado;
    }

    public void setDataCriado(LocalDateTime dataCriado) {
        this.dataCriado = dataCriado;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public HistoricoPessoa getHistoricoPessoa() {
        return historicoPessoa;
    }

    public void setHistoricoPessoa(HistoricoPessoa historicoPessoa) {
        this.historicoPessoa = historicoPessoa;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
}
