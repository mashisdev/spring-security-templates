package com.jwt.roles_email.user.service;

import com.jwt.roles_email.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDto findById(Long id);

    UserDto findByEmail(String email);

    Page<UserDto> findAll(Pageable pageable);

    UserDto update(UserDto userDto);

    void delete(Long id);

}
