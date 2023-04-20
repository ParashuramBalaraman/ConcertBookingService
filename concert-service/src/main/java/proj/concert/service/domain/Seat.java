package proj.concert.service.domain;

import proj.concert.common.types.BookingStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Seat{

    private BookingStatus status;

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal cost) {	
	}	
	
	public Seat() {}	
}
