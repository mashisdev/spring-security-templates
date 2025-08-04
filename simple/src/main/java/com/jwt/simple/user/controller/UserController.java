package com.jwt.simple.user.controller;

import com.jwt.simple.exception.ErrorMessage;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.request.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get authenticated user details", description = "Retrieves the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved authenticated user details.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> findMeByEmail();

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find user by ID", description = "Retrieves the details of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user details.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> findById(Long id);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find all users", description = "Retrieves a list of all users. This may be restricted to administrators in a real application.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of all users.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto[].class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<List<UserDto>> findAll();

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user details", description = "Updates the details of a user. An authenticated user can only update their own details.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation failed. " +
                            "**Message:** Validation failed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to change another user's credentials. " +
                            "**Message:** Not allowed to change another user's credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> update(Long id, UpdateUserRequest updateUserRequest);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a user", description = "Deletes a user by ID. An authenticated user can only delete their own account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted. No content to return."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to change another user's credentials. " +
                            "**Message:** Not allowed to change another user's credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Void> delete(Long id);
}
