package proj.concert.service.domain.mapper;

import proj.concert.common.dto.ConcertInfoSubscriptionDTO;
import proj.concert.service.domain.Subscription;

public class SubscriptionMapper {

    public static Subscription toDM(ConcertInfoSubscriptionDTO concertInfoSubDTO){
        return new Subscription(concertInfoSubDTO.getConcertId(),concertInfoSubDTO.getDate(),concertInfoSubDTO.getPercentageBooked());
    }
}
