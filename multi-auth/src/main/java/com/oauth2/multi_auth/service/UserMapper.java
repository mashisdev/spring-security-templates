package com.oauth2.multi_auth.service;

import com.oauth2.multi_auth.model.entity.User;
import com.oauth2.multi_auth.model.payload.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "authProvider", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    UserResponse mapToUserResponse(User user);
}
