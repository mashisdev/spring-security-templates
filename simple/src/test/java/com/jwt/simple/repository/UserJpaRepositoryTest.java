package com.jwt.simple.repository;
import com.jwt.simple.user.entity.UserEntity;
import com.jwt.simple.user.repository.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encodedpassword")
                .build();
    }

    @Test
    @Order(1)
    @Rollback(false)
    void shouldSaveUser() {
        UserEntity savedUser = userJpaRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");

        Optional<UserEntity> foundUser = userJpaRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
    }

    @Test
    @Order(2)
    @Rollback(false)
    void shouldFindUserByEmailWhenExists() {
        Optional<UserEntity> foundUser = userJpaRepository.findByEmail(testUser.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.get().getFirstname()).isEqualTo(testUser.getFirstname());
    }

    @Test
    @Order(3)
    @Rollback(false)
    void shouldReturnTrueIfEmailExists() {
        Boolean exists = userJpaRepository.existsByEmail(testUser.getEmail());

        assertTrue(exists);
    }

    @Test
    @Order(4)
    @Rollback(false)
    void shouldUpdateUser() {
        UserEntity userToUpdate = userJpaRepository.findByEmail(testUser.getEmail()).orElseThrow();
        userToUpdate.setFirstname("Jane");
        userToUpdate.setLastname("Smith");
        userToUpdate.setEmail("jane.smith@example.com");
        UserEntity updatedUser = userJpaRepository.save(userToUpdate);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstname()).isEqualTo("Jane");
        assertThat(updatedUser.getLastname()).isEqualTo("Smith");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.smith@example.com");

        Optional<UserEntity> retrievedUser = userJpaRepository.findById(updatedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getFirstname()).isEqualTo("Jane");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    @Order(5)
    @Rollback(false)
    void shouldDeleteUser() {
        UserEntity userToDelete = userJpaRepository.findByEmail("jane.smith@example.com").orElseThrow();

        userJpaRepository.delete(userToDelete);
        entityManager.flush(); // Force deletion to DB

        Optional<UserEntity> deletedUser = userJpaRepository.findById(userToDelete.getId());
        assertFalse(deletedUser.isPresent());
    }


    @Test
    @Order(6)
    void shouldNotFindUserByEmailWhenNotExists() {
        Optional<UserEntity> foundUser = userJpaRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @Order(7)
    void shouldReturnFalseIfEmailNotExists() {
        Boolean exists = userJpaRepository.existsByEmail("another.nonexistent@example.com");

        assertFalse(exists);
    }
}