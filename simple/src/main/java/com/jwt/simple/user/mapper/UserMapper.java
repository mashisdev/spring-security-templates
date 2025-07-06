package com.jwt.simple.user.mapper;

import com.jwt.simple.auth.RegisterRequest;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto userToUserDto (User user);

    User userDtoToUser(UserDto userDto);

    User registerRequestToUser(RegisterRequest request);
}
