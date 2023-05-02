package proj.concert.service.services;

import java.net.URI;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import proj.concert.common.dto.*;
import proj.concert.service.domain.*;
import proj.concert.service.domain.mapper.*;
import proj.concert.service.jaxrs.*;

import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.service.domain.User;
import proj.concert.service.domain.Subscription;
import proj.concert.service.util.TheatreLayout;

@Path("/concert-service")
public class ConcertResource {
    private static final Logger logger = LoggerFactory.getLogger(ConcertResource.class);
    private static final ConcurrentHashMap<LocalDateTime, LinkedList<SubscriptionInfo>> subsInfo = new ConcurrentHashMap<>();

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
            List<ConcertDTO> concertsMapped = new ArrayList<>();
            for(int i = 0; i < concerts.size(); i++){
                concertsMapped.add(ConcertMapper.toDTO(concerts.get(i)));
            }
            return Response.ok(concertsMapped).build();
        }
        finally{
            em.close();
        }
    }

    @GET
    @Path("/concerts/summaries")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getConcertSummaries() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();
            List<ConcertSummaryDTO> summariesMapped = new ArrayList<>();
            for(int i = 0; i < concerts.size(); i++){
                summariesMapped.add(ConcertMapper.toConcertSummaryDTO(concerts.get(i)));
            }
            return Response.ok(summariesMapped).build();
        }
        finally {
            em.close();
        }
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
            List<PerformerDTO> performersMapped = new ArrayList<>();
            for(int i = 0; i < performers.size(); i++){
                performersMapped.add(PerformerMapper.toDTO(performers.get(i)));
            }
            return Response.ok(performersMapped).build();
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

            for (User user : users){
                if (user.getUsername().equals(client.getUsername()) && user.getPassword().equals(client.getPassword())){
                    UUID value = UUID.randomUUID();
                    NewCookie n = new NewCookie("auth", value.toString());
                    user.setCookieValue(n.getValue());
                    UserDTO us = UserMapper.toDTO(user);
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
            //check if anyone logged in
            if (clientID == null){
                return Response.status(401).build();
            }
            //work out which user is logged in
            em.getTransaction().begin();
            TypedQuery<User> userQuery = em.createQuery("select u from User u where u.cookieValue like :clientID", User.class)
                    .setParameter("clientID", clientID.getValue()).setMaxResults(1);
            List<User> users = userQuery.getResultList();
            User user = users.get(0);
            em.getTransaction().commit();
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
                            //Find the seats that are available on the date of the booking
                            TypedQuery<Seat> seatQuery = em.createQuery("select s from Seat s where s.dateTime = :date", Seat.class)
                                    .setParameter("date", booking.getDate());
                            List<Seat> seats = seatQuery.getResultList();
                            List<Seat> bSeats = new ArrayList<Seat>();
                            //Go through all seats that are available, find the ones which we want to book and if they aren't already booked add them to bSeats
                            for (Seat seat : seats){
                                for (String label : booking.getSeatLabels()){
                                    if (seat.getLabel().equals(label)){
                                        if (seat.getIsBooked() == true){
                                            return Response.status(403).build();
                                        }
                                        bSeats.add(seat);
                                    }
                                }

                            }
                            //All seats are available, go back through each seat and change their status to booked
                            //Create a new arraylist for seatDTOs then convert all seats in the list to seatDTOs and add them to the list
                            List<SeatDTO> seatDTOS = new ArrayList<SeatDTO>();
                            SeatMapper sm = new SeatMapper();
                            for (Seat seat : bSeats){
                                seat.setIsBooked(true);
                                SeatDTO seatDTO = SeatMapper.toDTO(seat);
                                seatDTOS.add(seatDTO);
                            }
                            //Create a new bookingDTO and then convert into a booking object
                            BookingDTO bDTO = new BookingDTO(booking.getConcertId(), booking.getDate(), seatDTOS);
                            Booking b = BookingMapper.toDM(bDTO);
                            user.addBooking(b);

                            // get number of free seats for notification
                            int freeSeats = em.createQuery("SELECT COUNT(s) FROM Seat s WHERE s.dateTime = :date AND s.isBooked = false", Long.class)
                                    .setParameter("date", date)
                                    .getSingleResult()
                                    .intValue();

                            // send out notifications to subs who pass query
                            checkWithSubscribers(booking.getConcertId(), date, freeSeats);

                            em.getTransaction().commit();
                            return Response.created(URI.create("/concert-service/bookings/" + b.getId())).build();
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
    @GET
    @Path("/bookings/{id}")
    @Produces((MediaType.APPLICATION_JSON))
    public Response getBookingById(@PathParam("id") long id, @CookieParam("auth") Cookie clientID){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            //Check if user is logged in
            if (clientID != null){
                em.getTransaction().begin();
                TypedQuery<User> userQuery = em.createQuery("select u from User u where u.cookieValue like :clientID", User.class)
                        .setParameter("clientID", clientID.getValue()).setMaxResults(1);
                List<User> users = userQuery.getResultList();
                User user = users.get(0);
                em.getTransaction().commit();
                //Checking if the booking was made by the user that is currently logged in
                for (Booking booking : user.getBookings()){
                    if (booking.getId().equals(id)){
                        BookingMapper bm = new BookingMapper();
                        BookingDTO bDTO = bm.toDTO(booking);
                        return Response.ok(bDTO).build();
                    }
                }
            }
            return Response.status(403).build();
        }
        finally{
            em.close();
        }
    }

    @GET
    @Path("/bookings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings(@CookieParam("auth") Cookie clientID){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            if (clientID != null){
                em.getTransaction().begin();
                TypedQuery<User> userQuery = em.createQuery("select u from User u where u.cookieValue like :clientID", User.class)
                        .setParameter("clientID", clientID.getValue()).setMaxResults(1);
                List<User> users = userQuery.getResultList();
                User user = users.get(0);
                em.getTransaction().commit();
                List<BookingDTO> bDTOS = new ArrayList<BookingDTO>();
                for (Booking b : user.getBookings()){
                    BookingDTO bDTO = BookingMapper.toDTO(b);
                    bDTOS.add(bDTO);
                }
                return Response.ok(bDTOS).build();
            }
            return Response.status(401).build();
        }
        finally{
            em.close();
        }
    }

    @GET
    @Path("/seats/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeatsForDate(@PathParam("date") LocalDateTimeParam dateParam,
                                    @QueryParam("status") String status) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        LocalDateTime date = dateParam.getLocalDateTime();
        try{
            // Get all seats for the date, booked or unbooked
            TypedQuery<Seat> seatQuery = em.createQuery("select s from Seat s where s.dateTime = :dateTime", Seat.class)
                    .setParameter("dateTime", date);
            List<Seat> seats = seatQuery.getResultList();
            switch(status) {
                // Returns list of seats in accordance to the status
                case "Any":
                    List<SeatDTO> anyMapped = new ArrayList<>();
                    for (Seat seat : seats) {
                        anyMapped.add(SeatMapper.toDTO(seat));
                    }
                    return Response.ok(anyMapped).build();

                case "Booked":
                    List<SeatDTO> bookedMapped = new ArrayList<>();
                    for (Seat seat : seats) {
                        if (seat.getIsBooked() == true){
                            bookedMapped.add(SeatMapper.toDTO(seat));
                        }
                    }
                    return Response.ok(bookedMapped).build();

                case "Unbooked":
                    List<SeatDTO> unbookedMapped = new ArrayList<>();
                    for (Seat seat : seats) {
                        if(seat.getIsBooked() == false){
                            unbookedMapped.add(SeatMapper.toDTO(seat));
                        }
                    }
                    return Response.ok(unbookedMapped).build();
            }
        }
        finally{
            em.close();
        }
        return null;
    }

    @POST
    @Path("/subscribe/concertInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void subscribeToConcert(@Suspended AsyncResponse response, ConcertInfoSubscriptionDTO subInfo, @CookieParam("auth") Cookie clientID) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try{
            //check if anyone logged in
            if (clientID == null){
                response.resume(Response.status(401).build());
                return;
            }
            // Validate target concert
            em.getTransaction().begin();
            Concert concert = em.find(Concert.class, subInfo.getConcertId(), LockModeType.PESSIMISTIC_READ);
            if (concert == null || !concert.getDates().contains(subInfo.getDate())) {
                response.resume(Response.status(400).build());
                return;
            }
            //work out which user is logged in
            TypedQuery<User> userQuery = em.createQuery("select u from User u where u.cookieValue like :clientID", User.class)
                    .setParameter("clientID", clientID.getValue()).setMaxResults(1);
            List<User> users = userQuery.getResultList();
            User user = users.get(0);

            Subscription sub = SubscriptionMapper.toDM(subInfo);
            user.addSubscription(sub);
            em.getTransaction().commit();
            response.resume(Response.ok(subInfo).build());

            // check if the map contains the date, if not, create a new linked list
            synchronized (subsInfo) {
                if (!subsInfo.containsKey(subInfo.getDate())) {
                    subsInfo.put(subInfo.getDate(), new LinkedList<>());
                }
            }

            // create a new subscription info object with the information
            subsInfo.get(subInfo.getDate()).add(new SubscriptionInfo(response, subInfo));
        }

        finally{
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            em.close();
        }
    }

    private void checkWithSubscribers(long concertId, LocalDateTime date, int availableSeats) {

        // % of available seats
        double percentageBooked = 1.0 - availableSeats / (double) TheatreLayout.NUM_SEATS_IN_THEATRE;

        // check if there are subs for the date/time for concert
        if (!subsInfo.containsKey(date)) {
            return;
        }

        // iterate through the subs
        for (Iterator<SubscriptionInfo> it = subsInfo.get(date).iterator(); it.hasNext(); ) {
            SubscriptionInfo subscriptionInfo = it.next();

            // check if it is the same concert to prevent double notifications
            if (subscriptionInfo.getSubInfo().getConcertId() != concertId) {

                return;
            }
            if (percentageBooked >= subscriptionInfo.getSubInfo().getPercentageBooked()) {
                // remove the sub so that they are only notified once
                it.remove();

                // send out the notification
                subscriptionInfo.getAsyncResponse().resume(Response.ok(new ConcertInfoNotificationDTO(availableSeats)).build());
            }
        }
    }

}
