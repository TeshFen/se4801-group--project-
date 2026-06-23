package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/edutrack");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }

    @Test
    void shouldSaveAndFindUserByEmail() {
        // Use a unique email to avoid conflicts with existing data
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";
        User user = User.builder()
                .email(uniqueEmail)
                .password("encodedPass")
                .fullName("Test User")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        userRepository.save(user);

        var found = userRepository.findByEmail(uniqueEmail);
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }
}