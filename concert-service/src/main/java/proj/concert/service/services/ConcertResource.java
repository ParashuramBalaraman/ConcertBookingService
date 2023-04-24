package proj.concert.service.services;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.ws.rs.*;

import javax.ws.rs.core.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.service.domain.User;
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String username, String password){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            TypedQuery<User> userQuery = em.createQuery("select u from User u", User.class);
            List<User> users = userQuery.getResultList();
            for (User user : users){
                if (user.getUsername() == username){
                    if(user.getPassword() == password){
                        NewCookie n = new NewCookie("auth", "auth");
                        return Response.ok(user).cookie(n).build();
                    }
                }
            }
            throw new WebApplicationException((Response.Status.UNAUTHORIZED));
        }
        finally{
            em.close();
        }
    }

    public Response bookSeats(long id, Date date, String seats, Cookie userId){return null;}

    public Response getBookingById(long id){return null;}

    public Response getAllBookings(){return null;}

    public Response getSeatsForDate(Date date, String status){return null;}

}
