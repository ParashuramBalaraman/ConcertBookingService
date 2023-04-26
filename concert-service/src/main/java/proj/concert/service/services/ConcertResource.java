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
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.dto.UserDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.service.domain.User;

import proj.concert.service.domain.mapper.UserMapper;
import proj.concert.service.domain.mapper.ConcertMapper;
import proj.concert.service.domain.mapper.PerformerMapper;



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
            return Response.ok(ConcertMapper.toDTO(concert)).build();
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
            return Response.ok(PerformerMapper.toDTO(performer)).build();
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
            UserMapper um = new UserMapper();
            for (User user : users){
                if (user.getUsername().equals(client.getUsername()) && user.getPassword().equals(client.getPassword())){
                    UUID value = UUID.randomUUID();
                    NewCookie n = new NewCookie("auth", value.toString());
                    user.setCookieValue(n.getValue());
                    UserDTO us = um.toDTO(user);
                    em.getTransaction().commit();
                    return Response.ok(us).cookie(n).build();
                }
            }
            em.getTransaction().commit();
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
            if (clientID == null){
                return Response.status(401).build();
            }
            em.getTransaction().begin();
            System.out.println("validUser");
            TypedQuery<User> userQuery = em.createQuery("select u from User u where u.cookieValue like :clientID", User.class)
                    .setParameter("clientID", clientID.getValue()).setMaxResults(1);
            System.out.println("validUser2");
            List<User> users = userQuery.getResultList();
            User user = users.get(0);
            em.getTransaction().commit();
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
                            TypedQuery<Seat> seatQuery = em.createQuery("select s from Seat s where s.label in :inclList", Seat.class)
                                    .setParameter("inclList", booking.getSeatLabels());
                            List<Seat> seats = seatQuery.getResultList();
                            for (Seat seat : seats){
                                if (seat.getIsBooked() == true){
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
                            //Create a new bookingDTO and then convert into a booking object
                            BookingDTO bDTO = new BookingDTO(booking.getConcertId(), booking.getDate(), seatDTOS);
                            BookingMapper bm = new BookingMapper();
                            Booking b = bm.toDM(bDTO);
                            em.getTransaction().commit();
                            return Response.created(URI.create("/concert-service" + b.getId())).build();
                        }
                    }
                }
            }
            return Response.status(400).build();
        }
        finally {
            em.close();
        }
    }

    public Response getBookingById(long id){return null;}

    public Response getAllBookings(){return null;}

    public Response getSeatsForDate(Date date, String status){return null;}

}
