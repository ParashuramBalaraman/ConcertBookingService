package proj.concert.service.domain.mapper;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConcertMapper {
    public static Concert toDM(ConcertDTO concertDTO){
        Concert c= new Concert(concertDTO.getId(),
                concertDTO.getTitle(),
                concertDTO.getImageName(),
                concertDTO.getBlurb(),
                Set.copyOf(concertDTO.getDates()));
        Set<Performer> performers = new HashSet<Performer>();

        for(PerformerDTO p:concertDTO.getPerformers()){
            performers.add(PerformerMapper.toDM(p));
        }
        return c;
    }

    public static ConcertDTO ToDTO(Concert c) {
        ConcertDTO concertDTO = new ConcertDTO(c.getId(), c.getTitle(), c.getImageName(), c.getBlurb());
        ArrayList<PerformerDTO> performersAL = new ArrayList<PerformerDTO>();
        for (Performer p : c.getPerformers()) {
            performersAL.add(p.translatetoDTO());
        }
        concertDTO.setPerformers(performersAL);

        ArrayList<LocalDateTime> datesAL = new ArrayList<LocalDateTime>();
        for (LocalDateTime d : c.getDates()) {
            datesAL.add(d);
        }
        concertDTO.setDates(datesAL);
        return concertDTO;
    }
}
