package com.jwt.simple.user.mapper;

import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // User <-> UserEntity
    User userEntityToUser(UserEntity userEntity);

    UserEntity userToUserEntity(User user);

    // User <-> UserDto
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDTO);

    // RegisterRequest <-> User
    User registerRequestToUser(RegisterRequest registerRequest);
}
