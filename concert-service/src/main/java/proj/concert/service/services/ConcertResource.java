package proj.concert.service.services;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.*;

import javax.ws.rs.core.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proj.concert.service.domain.Concert;

public class ConcertResource {
    private static final Logger logger = LoggerFactory.getLogger(ConcertResource.class);

    public Response getSingleConcert(long id){return null;}

    public Response getAllConcerts(){return null;}

    public Response getConcertSummaries(){return null;}

    public Response getSinglePerformer(long id){return null;}

    public Response getAllPerformers(){return null;}

    public Response login(String username, String password){return null;}

    public Response bookSeats(long id, Date date, String seats, Cookie userId){return null;}

    public Response getBookingById(long id){return null;}

    public Response getAllBookings(){return null;}

    public Response getSeatsForDate(Date date, String status){return null;}

}
