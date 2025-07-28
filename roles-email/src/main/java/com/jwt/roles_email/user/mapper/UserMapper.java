package com.jwt.roles_email.user.mapper;

import com.jwt.roles_email.auth.request.RegisterRequest;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.entity.UserEntity;
import com.jwt.roles_email.user.request.UpdateUserRequest;
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

    // RegisterRequest -> User
    User registerRequestToUser(RegisterRequest registerRequest);

    // UpdateUserRequest -> UserDto
    UserDto updateUserRequestToUserDto(UpdateUserRequest updateUserRequest);
}
