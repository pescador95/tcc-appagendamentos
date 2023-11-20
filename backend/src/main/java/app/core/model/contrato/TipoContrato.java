package app.core.model.contrato;

import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "tipocontrato")

public class TipoContrato extends PanacheEntityBase {

    public static final Long SESSAO_UNICA = 1L;
    public static final Long SESSAO_COMPARTILHADA = 2L;
    @Column()
    @SequenceGenerator(name = "tipoContratoIdSequence", sequenceName = "tipocontrato_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "tipoContratoIdSequence")
    @Id
    private Long id;
    @Column()
    private String tipoContrato;
    @Column()
    private String descricao;

    public TipoContrato() {

    }

    public Boolean sessaoUnica() {
        return BasicFunctions.isValid(this.id) && this.id.equals(TipoContrato.SESSAO_UNICA);
    }

    public Boolean sessaoCompartilhada() {
        return BasicFunctions.isValid(this.id) && this.id.equals(TipoContrato.SESSAO_COMPARTILHADA);
    }

    public void setTipoSessaoUnica() {
        this.id = TipoContrato.SESSAO_UNICA;
    }

    public void setTipoSessaoCompartilhada() {
        this.id = TipoContrato.SESSAO_COMPARTILHADA;
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

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
