package proj.concert.service.domain.mapper;

import proj.concert.common.dto.SeatDTO;
import proj.concert.service.domain.Seat;

public class SeatMapper {
    public static SeatDTO toDTO(Seat seat){return new SeatDTO(seat.getLabel(),seat.getCost());}
    public static Seat toDM(SeatDTO seatDTO){return new Seat(seatDTO.getLabel(),seatDTO.getPrice());}
}
