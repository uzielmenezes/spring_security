package tech.buildrun.spring_security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.repository.RoleRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<String> roleNameCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private Role basicRole;

    private CreateUserRequest createUserRequest;

    private User basicUser;

    @BeforeEach
    void setUp() {
        basicRole = Role.builder()
                .name(Role.Values.BASIC.name())
                .build();

        createUserRequest = new CreateUserRequest("user",
                "email@email.com",
                "password");

        basicUser = User.builder()
                .username(createUserRequest.username())
                .userEmail(createUserRequest.email())
                .password(createUserRequest.password())
                .roles(Set.of(basicRole))
                .build();
    }

    @Nested
    class ListUsers {

        @Test
        @DisplayName("Should Return a List of All Users")
        void shouldReturnAListOfAllUsers() {

            // ARRANGE
            List<User> expectedUsers = List.of(basicUser);
            when(userRepository.findAll()).thenReturn(expectedUsers);

            // ACT
            List<User> actualUsers = userService.listUsers();

            // ASSERT
            assertEquals(expectedUsers, actualUsers);
            verify(userRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should Return an Empty List When There is no User Registered")
        void shouldReturnAnEmptyListWhenThereIsNoUserRegistered() {

            // ARRANGE
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // ACT
            List<User> actualUsers = userService.listUsers();

            // ASSERT
            assertEquals(Collections.emptyList(), actualUsers);
            verify(userRepository, times(1)).findAll();
        }

    }

    @Nested
    class NewUser {

        @Test
        @DisplayName("Should Create a New User When It Does not Exist")
        void shouldCreateAnewUserWhenItDoesNotExist() {

            //ARRANGE
            String encondedPassword = "encodedPassword";
            when(roleRepository.findByName(roleNameCaptor.capture())).thenReturn(basicRole);
            when(userRepository.findByUserEmail(any())).thenReturn(Optional.empty());
            when(bCryptPasswordEncoder.encode(createUserRequest.password())).thenReturn(encondedPassword);

            // ACT
            userService.newUser(createUserRequest);

            // ASSERT
            assertEquals(Role.Values.BASIC.name(), roleNameCaptor.getValue());
            verify(userRepository, times(1)).findByUserEmail(createUserRequest.email());

            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertEquals(createUserRequest.username(), capturedUser.getUsername());
            assertEquals(createUserRequest.email(), capturedUser.getUserEmail());
            assertEquals(encondedPassword, capturedUser.getPassword());
            assertTrue(capturedUser.getRoles().contains(basicRole));

//            verify(userRepository).save(argThat(user ->
//                    user.getUsername().equals(createUserRequest.username())
//                            && user.getUserEmail().equals(createUserRequest.email())
//                            && user.getRoles().contains(basicRole)));

        }

        @Test
        @DisplayName("Should Throw a Conflict Exception When User Already Exists ")
        void shouldThrowAConflictExceptionWhenUserAlreadyExists() {

            //ARRANGE
            when(userRepository.findByUserEmail(basicUser.getUserEmail()))
                    .thenReturn(Optional.of(basicUser));

            // ACT & ASSERT
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> userService.newUser(createUserRequest)
            );

            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            verify(userRepository, never()).save(any());
        }
    }
}