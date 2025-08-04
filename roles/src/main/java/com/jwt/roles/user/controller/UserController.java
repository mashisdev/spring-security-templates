package com.jwt.roles.user.controller;

import com.jwt.roles.exception.ErrorMessage;
import com.jwt.roles.user.dto.UserDto;
import com.jwt.roles.user.request.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserDto> findMeByEmail();

    @Operation(summary = "Find user by ID", description = "Retrieves a user by their unique ID.")
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserDto> findById(@Parameter(description = "ID of the user to retrieve", example = "1") Long id);

    @Operation(summary = "Find all users", description = "Retrieves a list of all users in the system. Requires ADMIN authority.")
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
                    description = "Forbidden. User does not have ADMIN authority.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<UserDto>> findAll();

    @Operation(summary = "Update user details", description = "Updates the details of a user. Can only update your own user account.")
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserDto> update(@Parameter(description = "ID of the user to update", example = "1") Long id, UpdateUserRequest updateUserRequest);

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID. Can only delete your own user account.")
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> delete(@Parameter(description = "ID of the user to delete", example = "1") Long id);
}
