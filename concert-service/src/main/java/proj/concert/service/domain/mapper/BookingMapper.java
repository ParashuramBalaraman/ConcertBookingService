package proj.concert.service.domain.mapper;
import proj.concert.service.domain.Seat;
import proj.concert.common.dto.BookingDTO;
import proj.concert.common.dto.SeatDTO;

import proj.concert.service.domain.Booking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BookingMapper {
    public static BookingDTO toDTO(Booking booking){
        ArrayList<SeatDTO> seatsDTO = new ArrayList<SeatDTO>();
        for(Seat s:booking.getSeats()){
            seatsDTO.add(SeatMapper.toDTO(s));
        }

        return new BookingDTO(booking.getConcertId(),booking.getDateTime(), seatsDTO);
    }

    public static Booking toDM(BookingDTO bookingDTO){
        Set<Seat> seats = new HashSet<Seat>();
        for(SeatDTO s:bookingDTO.getSeats()){
            seats.add(SeatMapper.toDM(s));
        }
        return new Booking(bookingDTO.getConcertId(),bookingDTO.getDate(),seats);
    }
}
