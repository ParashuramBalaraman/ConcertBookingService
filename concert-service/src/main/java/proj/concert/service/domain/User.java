package proj.concert.service.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
@Entity
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "ID",nullable = false,unique = true)
    private Long id;
    @Column(name="USERNAME",nullable = false,unique = true)
    private String username;
    @Column(name="PASSWORD",nullable = false)
    //in an ideal world the password would be hashed and not strored as plin text but that seems outside the scope of the project.
    private String password;
    @OneToMany(mappedBy = "USER",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @Column(name = "VERSION")
    Long version;

public User(){}

    public String getUsername(){return username;}
    public void setUsername(String name){username = name;}
    public String getPassword(){return password;}
    public void setPassword(String pw){password = pw;}

    public Set<Booking> getBookings(){return bookings;}
    public void addBooking(Booking booking){bookings.add(booking);}




}
