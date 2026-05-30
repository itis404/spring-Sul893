package com.privetmedved;

import com.privetmedved.entity.*;
import com.privetmedved.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PrivetMedvedApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(roleRepository).isNotNull();
    }

    @Test
    void testCreateUserAndRole() {
        Role role = new Role("ROLE_USER");
        role = roleRepository.save(role);
        assertThat(role.getId()).isNotNull();

        User user = new User("testuser", "test@example.com", "password");
        user.setRoles(Set.of(role));
        user = userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testCreateRoomAndPost() {
        Role role = roleRepository.save(new Role("ROLE_USER"));
        User user = userRepository.save(new User("author", "author@test.com", "pass"));
        user.setRoles(Set.of(role));
        user = userRepository.save(user);

        Room room = new Room();
        room.setName("Test Room");
        room.setSlug("test-room");
        room.setCreator(user);
        room.getParticipants().add(user);
        room = roomRepository.save(room);
        assertThat(room.getId()).isNotNull();

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post content.");
        post.setAuthor(user);
        post.setRoom(room);
        post = postRepository.save(post);
        assertThat(post.getId()).isNotNull();
    }

    @Test
    void testUserRepositoryQueries() {
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void testRoomRepositoryQueries() {
        assertThat(roomRepository.findPublicRooms()).isEmpty();
        assertThat(roomRepository.existsBySlug("no-such-room")).isFalse();
    }
}