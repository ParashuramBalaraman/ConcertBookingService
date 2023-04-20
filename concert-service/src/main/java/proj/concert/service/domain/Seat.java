package proj.concert.service.domain;

import proj.concert.common.types.BookingStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
@Entity
@Table(name="SEATS")
public class Seat{
	@Id
	@GeneratedValue
	@Column(name = "ID",nullable = false,unique = true)
	private Long seatID;
	@Column(name="LABEL",nullable = false)
	private String label;
	@Column(name="ISBOOKED")
	private boolean isBooked;
	@Column(name = "DATEANDTIME")
	private LocalDateTime dateTime;
	@Column(name = "COST")
	private BigDecimal cost;

	public Seat() {}
	public Seat(String label,BigDecimal cost){
		this.label = label;
		this.cost = cost;
	}
	public Seat(String label,boolean isBooked,LocalDateTime datetime,BigDecimal cost){
		this.label=label;
		this.isBooked = isBooked;
		this.dateTime=datetime;
		this.cost=cost;
	}


	public Long getSeatID(){return this.seatID;}
	public void setSeatID(Long id){this.seatID=id;}
	public String getLabel(){return this.label;}
	public void setLabel(String label){this.label = label;}

	public boolean getIsBooked(){return this.isBooked;}
	public void setIsBooked(boolean booked){this.isBooked = booked;}

	public LocalDateTime getDateTime(){return this.dateTime;}
	public void setDateTime(LocalDateTime date){this.dateTime = date;}
	public BigDecimal getCost(){return this.cost;}
	public void setCost(BigDecimal cost){this.cost=cost;}




}
