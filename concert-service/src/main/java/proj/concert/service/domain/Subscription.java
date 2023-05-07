package proj.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="SUBSCRIPTIONS")
public class Subscription {
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Long id;
    @Column(name="CONCERTID")
    private Long concertId;
    @Column(name="DATETIME")
    private LocalDateTime dateTime;

    @Column(name="PERCENTBOOKED")
    private int percentBooked;

    public Subscription(){}
    public Subscription(Long concertID,LocalDateTime dateTime,int percentBooked){
        this.concertId=concertID;
        this.dateTime = dateTime;
        this.percentBooked=percentBooked;
    }

    public Long getId(){return this.id;}
    public void setId(Long id){this.id=id;}

    public Long getConcertIdId(){return this.concertId;}
    public void setConcertIdId(Long id){this.concertId=id;}

    public LocalDateTime getDateTime(){return this.dateTime;}
    public void setDateTime(LocalDateTime dateTime){this.dateTime=dateTime;}

    public int getPercentBooked(){return this.percentBooked;}
    public void setPercentBooked(int percentBooked){this.percentBooked=percentBooked;}
}
