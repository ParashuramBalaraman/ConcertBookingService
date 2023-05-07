package proj.concert.service.domain.mapper;

import proj.concert.common.dto.UserDTO;
import proj.concert.service.domain.User;

public class UserMapper {
    public static UserDTO toDTO(User user){
        return new UserDTO(user.getUsername(),user.getPassword());
    }

}

