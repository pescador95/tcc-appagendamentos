package app.agendamento.model.agendamento;

import app.agendamento.model.organizacao.Organizacao;
import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipoAgendamento")

public class TipoAgendamento extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "tipoAgendamentoIdSequence", sequenceName = "tipoAgendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "tipoAgendamentoIdSequence")
    @Id
    private Long id;

    @Column()
    private String tipoAgendamento;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tipoagendamentoorganizacoes", joinColumns = {
            @JoinColumn(name = "tipoagendamentoId")}, inverseJoinColumns = {
            @JoinColumn(name = "organizacaoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Organizacao> organizacoes = new ArrayList<>();

    public TipoAgendamento() {

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

    public String getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(String tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }

    public List<Organizacao> getOrganizacoes() {
        return organizacoes;
    }

    public void setOrganizacoes(List<Organizacao> organizacoes) {
        this.organizacoes = organizacoes;
    }
}
