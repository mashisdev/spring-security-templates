package com.oauth2.multi_auth.service;

import com.oauth2.multi_auth.model.entity.User;
import com.oauth2.multi_auth.model.error.UserNotFoundException;
import com.oauth2.multi_auth.model.payload.UserResponse;
import com.oauth2.multi_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserInfoById(Long id) {
        log.debug("Getting user info by id: {}", id);

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: %s.".formatted(id)));

        return userMapper.mapToUserResponse(user);
    }

}
