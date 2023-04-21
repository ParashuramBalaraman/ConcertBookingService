package proj.concert.service.domain;

import net.bytebuddy.asm.Advice;
import proj.concert.common.dto.UserDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="BOOKINGS")
public class Booking {
@Id
@GeneratedValue
@Column(name="ID")
    private Long id;
@Column(name="CONCERT_ID")
private Long concertId;
@Column(name="DATETIME")
    private LocalDateTime dateTime;
    @OneToMany
    //some form of joincolumn or join table
    private Set<Seat> seats;

    public Booking(){}
    public Booking(Long concertId, LocalDateTime dateTime,Set<Seat> seats){
        this.concertId=concertId;
        this.dateTime=dateTime;
        this.seats=seats;
    }

    public Long getConcertId(){return this.concertId;}
    public void setConcertId(Long concertId){this.concertId=concertId;}
public Long getId(){return this.id;}
    public void setId(Long id){this.id = id;}

    public LocalDateTime getDateTime(){return this.dateTime;}
    public void setDateTime(LocalDateTime dateTime){this.dateTime=dateTime;}

    public Set<Seat> getSeats(){return this.seats;}
    public void setSeats(Set<Seat> seats){this.seats=seats;}

}
