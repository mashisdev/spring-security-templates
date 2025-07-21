package com.jwt.simple.service;

import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.repository.UserRepository;
import com.jwt.simple.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void findByIdShouldReturnUserDtoWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto foundUserDto = userService.findById(1L);

        assertNotNull(foundUserDto);
        assertEquals(userDto.getId(), foundUserDto.getId());
        assertEquals(userDto.getEmail(), foundUserDto.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).userToUserDto(user);
    }

    @Test
    void findByIdShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, never()).userToUserDto(any(User.class));
    }

    @Test
    void findByEmailShouldReturnUserDtoWhenUserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto foundUserDto = userService.findByEmail("john.doe@example.com");

        assertNotNull(foundUserDto);
        assertEquals(userDto.getId(), foundUserDto.getId());
        assertEquals(userDto.getEmail(), foundUserDto.getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userMapper, times(1)).userToUserDto(user);
    }

    @Test
    void findByEmailShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("nonexistent@example.com"));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(userMapper, never()).userToUserDto(any(User.class));
    }

    @Test
    void findAllShouldReturnListOfUserDtos() {
        User user2 = User.builder().id(2L).firstname("Jane").lastname("Smith").email("jane.smith@example.com").password("pass456").build();
        UserDto userDto2 = UserDto.builder().id(2L).firstname("Jane").lastname("Smith").email("jane.smith@example.com").build();

        List<User> users = Arrays.asList(user, user2);
        List<UserDto> userDtos = Arrays.asList(userDto, userDto2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(userMapper.userToUserDto(user2)).thenReturn(userDto2);

        List<UserDto> foundUserDtos = userService.findAll();

        assertNotNull(foundUserDtos);
        assertEquals(2, foundUserDtos.size());
        assertEquals(userDtos, foundUserDtos);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).userToUserDto(user);
        verify(userMapper, times(1)).userToUserDto(user2);
    }

    @Test
    void updateShouldReturnUpdatedUserDtoWhenUserExists() {
        User updatedUser = User.builder()
                .id(1L)
                .firstname("Updated John")
                .lastname("Updated Doe")
                .email("updated.john@example.com")
                .password("password123")
                .build();

        UserDto updatedUserDtoInput = UserDto.builder()
                .id(1L)
                .firstname("Updated John")
                .lastname("Updated Doe")
                .email("updated.john@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userDtoToUser(updatedUserDtoInput)).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(updatedUserDtoInput);

        UserDto resultUserDto = userService.update(updatedUserDtoInput);

        assertNotNull(resultUserDto);
        assertEquals(updatedUserDtoInput.getFirstname(), resultUserDto.getFirstname());
        assertEquals(updatedUserDtoInput.getEmail(), resultUserDto.getEmail());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).userDtoToUser(updatedUserDtoInput);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).userToUserDto(any(User.class));
    }

    @Test
    void updateShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        UserDto updatedUserDtoInput = UserDto.builder().id(99L).firstname("Nonexistent").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(updatedUserDtoInput));
        verify(userRepository, times(1)).findById(99L);
        verify(userMapper, never()).userDtoToUser(any(UserDto.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteShouldCallDeleteByIdOnRepository() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
