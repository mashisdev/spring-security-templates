package com.jwt.simple.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.simple.exception.GlobalExceptionHandler;
import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.user.controller.UserControllerImpl;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.request.UpdateUserRequest;
import com.jwt.simple.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserControllerImpl userController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(this.userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        SecurityContextHolder.clearContext();
    }

    // GET method
    @Test
    void shouldReturnAuthenticatedUser_whenFindMeByEmailIsCalled() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john.doe@example.com", "password")
        );

        when(userService.findByEmail("john.doe@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void shouldReturnUserById_whenFindByIdIsCalled() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("jhon.doe@example.com")
                .build();

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstname").value("John"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFound_whenFindByIdIsCalledWithNonExistingId() throws Exception {
        long nonExistingId = 99L;
        when(userService.findById(nonExistingId)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.exception").value("UserNotFoundException"));

        verify(userService, times(1)).findById(nonExistingId);
    }

    @Test
    void shouldReturnAllUsers_whenFindAllIsCalled() throws Exception {
        UserDto user1 = UserDto.builder().id(1L).firstname("John").lastname("Doe").email("john@example.com").build();
        UserDto user2 = UserDto.builder().id(2L).firstname("Jane").lastname("Smith").email("jane@example.com").build();
        List<UserDto> userList = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(userList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyList_whenFindAllIsCalledAndNoUsersExist() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).findAll();
    }

    // PUT method
    @Test
    void shouldUpdateUser_whenCurrentUserMatchesUpdatedUserAndIdsMatch() throws Exception {
        Long currentUserId = 1L;
        String currentUserEmail = "current.user@example.com";

        UserDto currentUserDto = UserDto.builder().id(currentUserId).firstname("Current").lastname("User").email(currentUserEmail).build();
        UserDto updatedUserDto = UserDto.builder().id(currentUserId).firstname("Updated").lastname("User").email("updated.user@example.com").build();
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder().id(currentUserId).firstname("Updated").lastname("User").email("updated.user@example.com").build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password")
        );

        when(userService.findByEmail(currentUserEmail)).thenReturn(currentUserDto);
        when(userService.findById(updateUserRequest.getId())).thenReturn(currentUserDto);
        when(userMapper.updateUserRequestToUserDto(any(UpdateUserRequest.class))).thenReturn(updatedUserDto);
        when(userService.update(any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/{id}", currentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(currentUserId))
                .andExpect(jsonPath("$.firstname").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated.user@example.com"));

        verify(userService, times(1)).findByEmail(currentUserEmail);
        verify(userService, times(1)).findById(currentUserId);
        verify(userMapper, times(1)).updateUserRequestToUserDto(any(UpdateUserRequest.class));
        verify(userService, times(1)).update(any(UserDto.class));
    }

    @Test
    void shouldReturnForbidden_whenPathVariableIdDoesNotMatchRequestBodyId() throws Exception {

        Long currentUserId = 1L;
        String currentUserEmail = "current.user@example.com";

        UserDto currentUserDto = UserDto.builder().id(currentUserId).firstname("Current").lastname("User").email(currentUserEmail).build();

        Long pathId = 2L;

        UpdateUserRequest requestBodyWithCurrentUserId = UpdateUserRequest.builder()
                .id(currentUserId)
                .firstname("Test")
                .lastname("User")
                .email("test.user@example.com")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password")
        );

        when(userService.findByEmail(currentUserEmail)).thenReturn(currentUserDto);
        when(userService.findById(requestBodyWithCurrentUserId.getId())).thenReturn(currentUserDto);

        mockMvc.perform(put("/api/users/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBodyWithCurrentUserId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Not allowed to change another user's credentials"))
                .andExpect(jsonPath("$.exception").value("NotAllowedToChangeCredentialsException"));

        verify(userService, times(1)).findByEmail(currentUserEmail);
        verify(userService, times(1)).findById(requestBodyWithCurrentUserId.getId());
        verify(userMapper, times(0)).updateUserRequestToUserDto(any(UpdateUserRequest.class));
        verify(userService, times(0)).update(any(UserDto.class));
    }

    @Test
    void shouldReturnForbidden_whenCurrentUserDoesNotMatchUpdatedUser() throws Exception {

        Long currentUserId = 1L;
        String currentUserEmail = "current.user@example.com";

        UserDto currentUserDto = UserDto.builder().id(currentUserId).firstname("Current").lastname("User").email(currentUserEmail).build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password")
        );

        Long anotherUserId = 2L;
        String anotherUserEmail = "another.user@example.com";

        UserDto anotherUserDto = UserDto.builder()
                .id(anotherUserId)
                .firstname("Another")
                .lastname("User")
                .email(anotherUserEmail)
                .build();

        UpdateUserRequest requestToUpdateAnotherUser = UpdateUserRequest.builder()
                .id(anotherUserId)
                .firstname("Modified")
                .lastname("Another")
                .email("modified.another@example.com")
                .build();

        when(userService.findByEmail(currentUserEmail)).thenReturn(currentUserDto);
        when(userService.findById(anotherUserId)).thenReturn(anotherUserDto);

        mockMvc.perform(put("/api/users/{id}", anotherUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToUpdateAnotherUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Not allowed to change another user's credentials"))
                .andExpect(jsonPath("$.exception").value("NotAllowedToChangeCredentialsException"));

        verify(userService, times(1)).findByEmail(currentUserEmail);
        verify(userService, times(1)).findById(anotherUserId);
        verify(userMapper, times(0)).updateUserRequestToUserDto(any(UpdateUserRequest.class));
        verify(userService, times(0)).update(any(UserDto.class));
    }

    // DELETE method
    @Test
    void shouldDeleteUser_whenCurrentUserMatchesDeletedUser() throws Exception {
        Long currentUserId = 1L;
        String currentUserEmail = "current.user@example.com";

        UserDto currentUserDto = UserDto.builder().id(currentUserId).firstname("Current").lastname("User").email(currentUserEmail).build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password")
        );

        when(userService.findByEmail(currentUserEmail)).thenReturn(currentUserDto);
        when(userService.findById(currentUserId)).thenReturn(currentUserDto);
        doNothing().when(userService).delete(currentUserId);

        mockMvc.perform(delete("/api/users/{id}", currentUserId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).findByEmail(currentUserEmail);
        verify(userService, times(1)).findById(currentUserId);
        verify(userService, times(1)).delete(currentUserId);
    }

    @Test
    void shouldReturnForbidden_whenCurrentUserDoesNotMatchUserToDelete() throws Exception {
        Long currentUserId = 1L;
        String currentUserEmail = "current.user@example.com";

        UserDto currentUserDto = UserDto.builder().id(currentUserId).firstname("Current").lastname("User").email(currentUserEmail).build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password")
        );

        Long anotherUserId = 2L;
        String anotherUserEmail = "another.user@example.com";

        UserDto anotherUserDto = UserDto.builder()
                .id(anotherUserId)
                .firstname("Another")
                .lastname("User")
                .email(anotherUserEmail)
                .build();

        when(userService.findByEmail(currentUserEmail)).thenReturn(currentUserDto);
        when(userService.findById(anotherUserId)).thenReturn(anotherUserDto);

        mockMvc.perform(delete("/api/users/{id}", anotherUserId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Not allowed to change another user's credentials"))
                .andExpect(jsonPath("$.exception").value("NotAllowedToChangeCredentialsException"));

        verify(userService, times(1)).findByEmail(currentUserEmail);
        verify(userService, times(1)).findById(anotherUserId);
        verify(userService, times(0)).delete(anyLong());
    }
}