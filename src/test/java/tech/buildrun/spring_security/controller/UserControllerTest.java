package tech.buildrun.spring_security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.spring_security.config.SecurityConfig;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.service.UserService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private Role basicRole;

    private CreateUserRequest createUserRequest;

    private User basicUser;

    @BeforeEach
    void setUp() {
        basicRole = Role.builder()
                .name(Role.Values.BASIC.name())
                .build();

        createUserRequest = new CreateUserRequest("user",
                "email@gmail.com",
                "123456789");

        basicUser = User.builder()
                .username("user").
                userEmail("email@email.com").
                password("123456789").
                roles(Set.of(basicRole)).build();
    }

    @Nested
    class ListUsers {

        @Test
        @DisplayName("Should Return a List of All Users with SCOPE_admin Authority")
        @WithMockUser(authorities = "SCOPE_admin")
        void shouldReturnAListOfAllUsersWithScopeAdminAuthority() throws Exception {

            // ARRANGE
            List<User> expectedUsers = List.of(basicUser);
            given(userService.listUsers()).willReturn(expectedUsers);

            // ACT & ASSERT
            mockMvc.perform(get("/users/all-users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("user"))
                    .andExpect(jsonPath("$[0].userEmail").value("email@email.com"));
        }

        @Test
        @DisplayName("Should Return Forbidden when User does not have SCOPE_admin Authority")
        @WithMockUser(authorities = "SCOPE_basic")
        void shouldReturnForbiddenWhenUserDoesNotHaveScopeAdminAuthority() throws Exception {
            mockMvc.perform(get("/users/all-users")).andExpect(status().isForbidden());
        }
    }

    @Nested
    class NewUser {

        @Test
        @DisplayName("Should Create a New User Successfully")
        void shouldCreateANewUserSuccessfully() throws Exception {

            // ARRANGE
            doNothing().when(userService).newUser(any());

            // ACT & ASSERT
            mockMvc.perform(post("/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createUserRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should Return Conflict When User Already Exists")
        void shouldReturnConflictWhenUserAlreadyExists() throws Exception {

            // ARRANGE
            String expectedMessage = "User already exists";
            doThrow(new ResponseStatusException(HttpStatus.CONFLICT, expectedMessage))
                    .when(userService).newUser(any());

            // ACT & ASSERT
            mockMvc.perform(post("/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createUserRequest)))
                    .andExpect(status().isConflict());
        }

    }
}