package proj.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;
import javax.ws.rs.*;

import javax.ws.rs.core.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proj.concert.common.dto.*;
import proj.concert.service.domain.*;
import proj.concert.service.domain.mapper.BookingMapper;
import proj.concert.service.domain.mapper.SeatMapper;
import proj.concert.service.domain.mapper.UserMapper;


@Path("/concert-service")
public class ConcertResource {
    private static final Logger logger = LoggerFactory.getLogger(ConcertResource.class);

    @GET
    @Path("/concerts/{id}")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getSingleConcert(@PathParam("id") long id){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            logger.info("Getting concert with id" + id);
            em.getTransaction().begin();
            Concert concert = em.find(Concert.class, id);
            em.getTransaction().commit();
            if (concert == null){
                throw new WebApplicationException((Response.Status.NOT_FOUND));
            }
            return Response.ok(concert.translateToDTO()).build();
        } finally{
            em.close();
        }
    }

    @GET
    @Path("/concerts")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getAllConcerts(){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();
            return Response.ok(concerts).build();
        }
        finally{
            em.close();
        }
    }

    public Response getConcertSummaries(){
        return null;
    }

    @GET
    @Path("/performers/{id}")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getSinglePerformer(@PathParam("id") long id){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            logger.info("Getting concert with id" + id);
            em.getTransaction().begin();
            Performer performer = em.find(Performer.class, id);
            em.getTransaction().commit();
            if (performer == null){
                throw new WebApplicationException((Response.Status.NOT_FOUND));
            }
            return Response.ok(performer.translatetoDTO()).build();
        } finally{
            em.close();
        }
    }

    @GET
    @Path("/performers")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getAllPerformers(){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            TypedQuery<Performer> performerQuery = em.createQuery("select p from Performer p", Performer.class);
            List<Performer> performers = performerQuery.getResultList();
            return Response.ok(performers).build();
        }
        finally{
            em.close();
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserDTO client){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            em.getTransaction().begin();
            TypedQuery<User> userQuery = em.createQuery("select u from User u", User.class);
            List<User> users = userQuery.getResultList();
            em.getTransaction().commit();
            UserMapper um = new UserMapper();
            for (User user : users){
                if (user.getUsername().equals(client.getUsername()) && user.getPassword().equals(client.getPassword())){
                    UUID value = UUID.randomUUID();
                    NewCookie n = new NewCookie("auth", value.toString());
                    user.setCookieValue(n.getValue());
                    user.setCookieName(n.getName());
                    UserDTO us = um.toDTO(user);
                    return Response.ok(us).cookie(n).build();
                }
            }
            return Response.status(401).build();
        }
        finally{
            em.close();
        }
    }

    @POST
    @Path("/bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response bookSeats(BookingRequestDTO booking, @CookieParam("auth") Cookie clientID){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            em.getTransaction().begin();
            TypedQuery<User> userQuery = em.createQuery("select u from User u", User.class);
            List<User> users = userQuery.getResultList();
            em.getTransaction().commit();
            //Go through all users to see if any are logged in. That will be the user we use to book.
            for (User user : users){
                if (clientID.getValue().equals(user.getCookieValue())){
                    System.out.println(user.getUsername() + " " +  user.getCookieValue());
                    //Get all concerts
                    em.getTransaction().begin();
                    TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
                    List<Concert> concerts = concertQuery.getResultList();
                    em.getTransaction().commit();
                    //Go through all concerts to check that the concertId and concertDate of the booking match an available concert
                    for (Concert concert : concerts){
                        if (concert.getId().equals(booking.getConcertId())){
                            //Go through all dates the concert is on to see if any match with the booking date
                            for (LocalDateTime date : concert.getDates()){
                                if (date.equals(booking.getDate())){
                                    //Go through all the seats to see if the seats looking to be booked are already booked
                                    em.getTransaction().begin();
                                    TypedQuery<Seat> seatQuery = em.createQuery("select s from Seat s where s.label in :inclList", Seat.class);
                                    List<Seat> seats = seatQuery.getResultList();
                                    em.getTransaction().commit();
                                    for (Seat seat : seats){
                                        if (seat.getIsBooked()){
                                            return Response.status(403).build();
                                        }
                                    }
                                    //All seats are available, go back through each seat and change their status to booked
                                    //Create a new arraylist for seatDTOs then convert all seats in the list to seatDTOs and add them to the list
                                    List<SeatDTO> seatDTOS = new ArrayList<SeatDTO>();
                                    SeatMapper sm = new SeatMapper();
                                    for (Seat seat : seats){
                                        seat.setIsBooked(true);
                                        SeatDTO seatDTO = sm.toDTO(seat);
                                        seatDTOS.add(seatDTO);

                                    }
                                    //Create a new bookingDTO, then convert into a booking object
                                    BookingDTO bDTO = new BookingDTO(booking.getConcertId(), booking.getDate(), seatDTOS);
                                    BookingMapper bm = new BookingMapper();
                                    Booking b = bm.toDM(bDTO);
                                    return Response.created(URI.create("/concert-service" + b.getId())).build();
                                }
                            }
                        }
                    }
                    return Response.status(400).build();
                }
            }
            return Response.status(401).build();
        }
        finally {
            em.close();
        }
    }

    public Response getBookingById(long id){return null;}

    public Response getAllBookings(){return null;}

    public Response getSeatsForDate(Date date, String status){return null;}

}
