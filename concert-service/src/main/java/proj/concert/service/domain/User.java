package proj.concert.service.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class User {
    private AtomicLong id;//username could function as id may look at changing over later
    private String username;
    //in an ideal world the password would be hashed and not strored as plin text but that seems outside the scope of the project.
    private String password;
    private HashSet<Booking> bookings;

public User(){}
    public AtomicLong getId(){return id;}
    public String getUsername(){return username;}
    public void setUsername(String name){username = name;}
    public String getPassword(){return password;}
    public void setPassword(String pw){password = pw;}

    public Set<Booking> getBookings(){return bookings;}
    public void addBooking(Booking booking){bookings.add(booking);}




}
