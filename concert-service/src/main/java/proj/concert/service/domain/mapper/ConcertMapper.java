package proj.concert.service.domain.mapper;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;

import java.util.Set;

public class ConcertMapper {
    public static Concert toDM(ConcertDTO concertDTO){
        Concert c= new Concert(concertDTO.getId(),
                concertDTO.getTitle(),
                concertDTO.getImageName(),
                concertDTO.getBlurb(),
                Set.copyOf(concertDTO.getDates()));
        Set<Performer> performers;

        for(PerformerDTO p:concertDTO.getPerformers()){
            performer.add()
        }
    }
}
