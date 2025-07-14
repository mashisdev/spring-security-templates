package com.jwt.simple.repository;
import com.jwt.simple.user.entity.UserEntity;
import com.jwt.simple.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        testUser = UserEntity.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encodedpassword")
                .build();
    }

    @Test
    void shouldSaveUser() {
        UserEntity savedUser = userJpaRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();

        Optional<UserEntity> foundUser = userJpaRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldFindUserByEmailWhenExists() {
        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserEntity> foundUser = userJpaRepository.findByEmail(testUser.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.get().getFirstname()).isEqualTo(testUser.getFirstname());
    }

    @Test
    void shouldNotFindUserByEmailWhenNotExists() {
        Optional<UserEntity> foundUser = userJpaRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        entityManager.persist(testUser);
        entityManager.flush();

        Boolean exists = userJpaRepository.existsByEmail(testUser.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfEmailNotExists() {
        Boolean exists = userJpaRepository.existsByEmail("another.nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldUpdateUser() {
        entityManager.persist(testUser);
        entityManager.flush();
        entityManager.clear();

        UserEntity userToUpdate = userJpaRepository.findByEmail(testUser.getEmail()).orElseThrow();
        userToUpdate.setFirstname("Jane");
        userToUpdate.setLastname("Smith");

        UserEntity updatedUser = userJpaRepository.save(userToUpdate);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstname()).isEqualTo("Jane");
        assertThat(updatedUser.getLastname()).isEqualTo("Smith");

        Optional<UserEntity> retrievedUser = userJpaRepository.findById(updatedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getFirstname()).isEqualTo("Jane");
    }

    @Test
    void shouldDeleteUser() {
        entityManager.persist(testUser);
        entityManager.flush();

        userJpaRepository.delete(testUser);
        entityManager.flush();

        Optional<UserEntity> deletedUser = userJpaRepository.findById(testUser.getId());
        assertThat(deletedUser).isNotPresent();
    }
}