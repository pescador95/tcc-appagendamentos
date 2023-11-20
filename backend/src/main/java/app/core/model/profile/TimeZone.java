package app.core.model.profile;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "timezone")
public class TimeZone extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "timezoneIdSequence", sequenceName = "timezone_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "timezoneIdSequence")
    @Id
    private Long id;
    @Column()
    private String timeZoneId;
    @Column()
    private String timeZoneOffset;

    public TimeZone(String timeZoneId, String timeZoneOffset) {
        this.timeZoneId = timeZoneId;
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public String getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public TimeZone(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public void setTimeZoneOffset(String timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
}
