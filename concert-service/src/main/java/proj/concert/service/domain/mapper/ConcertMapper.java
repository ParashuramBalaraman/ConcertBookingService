package proj.concert.service.domain.mapper;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConcertMapper {

    //don't need a concertdto to concert mapper as it is never required
    public static ConcertDTO toDTO(Concert c) {
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
    public static ConcertSummaryDTO toConcertSummaryDTO(Concert c){
        return new ConcertSummaryDTO(c.getId(),c.getTitle(),c.getImageName());
    }
}
