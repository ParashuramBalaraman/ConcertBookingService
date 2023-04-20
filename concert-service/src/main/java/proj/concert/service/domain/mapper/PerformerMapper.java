package proj.concert.service.domain.mapper;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Performer;

public class PerformerMapper {
    public Static Performer toDM(PerformerDTO performerDTO){
        return new Performer(performerDTO.getId(),
                performerDTO.getName(),
                performerDTO.getImageName(),
                performerDTO.getGenre(),
                performerDTO.getBlurb())
    }
}
