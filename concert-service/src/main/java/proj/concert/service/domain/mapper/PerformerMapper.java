package proj.concert.service.domain.mapper;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Performer;

public class PerformerMapper {
    public static Performer toDM(PerformerDTO performerDTO){
        return new Performer(performerDTO.getId(),
                performerDTO.getName(),
                performerDTO.getImageName(),
                performerDTO.getGenre(),
                performerDTO.getBlurb());
    }
    public static PerformerDTO toDTO(Performer performer){
        return new PerformerDTO(performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb());

    }
}
