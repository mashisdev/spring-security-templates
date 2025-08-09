package com.jwt.roles_email.user.controller;

import com.jwt.roles_email.exception.ErrorMessage;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.UserEntity;
import com.jwt.roles_email.user.request.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "User Management", description = "APIs for reading, updating and deleting users")
public interface UserController {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current authenticated user", description = "Retrieves the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user details.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Authentication token is missing or invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> findMeByEmail(UserEntity authenticatedUser);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find user by ID", description = "Retrieves a user by their unique ID. Requires a valid JWT token.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Authentication token is missing or invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> findById(@Parameter(description = "The ID of the user to retrieve", example = "1") Long id);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find all users", description = "Retrieves a list of all users in the system. Requires 'ADMIN' authority.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of all users.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Authentication token is missing or invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. User does not have 'ADMIN' authority.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Page<UserDto>> findAll(Pageable pageable);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user details", description = "Updates the details of a user. The authenticated user can only update their own account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. Invalid request body or validation errors.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Authentication token is missing or invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. Not allowed to update another user's credentials.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> update(@Parameter(description = "The ID of the user to update", example = "1") Long id,
                                   @RequestBody(description = "The user data to update") UpdateUserRequest updateUserRequest,
                                   UserEntity currentUser);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID. The authenticated user can only delete their own account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully. No content.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Authentication token is missing or invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. Not allowed to delete another user's account.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Void> delete(@Parameter(description = "The ID of the user to delete", example = "1") Long id,
                                UserEntity currentUser);
}
