package com.privetmedved.service;

import com.privetmedved.dto.RoomForm;
import com.privetmedved.entity.Room;
import com.privetmedved.entity.User;
import com.privetmedved.exception.BadRequestException;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.repository.RoomRepository;
import com.privetmedved.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Room createRoom(RoomForm form, User creator) {
        if (roomRepository.existsBySlug(form.getSlug())) {
            throw new BadRequestException("Room with slug '" + form.getSlug() + "' already exists");
        }

        Room room = new Room();
        room.setName(form.getName());
        room.setSlug(form.getSlug());
        room.setDescription(form.getDescription());
        room.setPrivate(form.isPrivate());
        room.setMaxParticipants(form.getMaxParticipants());
        room.setCreator(creator);

        room.getParticipants().add(creator);
        creator.getRooms().add(room);

        Room saved = roomRepository.save(room);
        log.info("Room created: {} by {}", saved.getName(), creator.getUsername());
        return saved;
    }

    @Transactional
    public Room updateRoom(Long id, RoomForm form, User currentUser) {
        Room room = findById(id);

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the creator can edit this room");
        }

        room.setName(form.getName());
        room.setSlug(form.getSlug());
        room.setDescription(form.getDescription());
        room.setPrivate(form.isPrivate());
        room.setMaxParticipants(form.getMaxParticipants());

        Room saved = roomRepository.save(room);
        log.debug("Room updated: {}", id);
        return saved;
    }

    @Transactional
    public void deleteRoom(Long id, User currentUser) {
        Room room = findById(id);

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the creator can delete this room");
        }

        roomRepository.delete(room);
        log.info("Room deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    @Transactional(readOnly = true)
    public Room findBySlug(String slug) {
        return roomRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + slug));
    }

    @Transactional(readOnly = true)
    public List<Room> findPublicRooms() {
        return roomRepository.findPublicRooms();
    }

    @Transactional(readOnly = true)
    public List<Room> findMostPopularRooms() {
        return roomRepository.findMostPopularRooms();
    }

    @Transactional(readOnly = true)
    public List<Room> findRoomsByCreator(Long userId) {
        return roomRepository.findByCreatorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Room> findRoomsByParticipant(Long userId) {
        return roomRepository.findByParticipantId(userId);
    }

    @Transactional
    public void joinRoom(String slug, User user) {
        Room room = roomRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + slug));

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", user.getId()));

        if (managedUser.getRooms().contains(room)) {
            throw new BadRequestException("You are already a member of this room");
        }

        if (room.getMaxParticipants() != null &&
                room.getParticipants().size() >= room.getMaxParticipants()) {
            throw new BadRequestException("Room is full");
        }

        managedUser.getRooms().add(room);
        userRepository.save(managedUser);
        log.info("User {} joined room {}", managedUser.getUsername(), room.getName());
    }

    @Transactional
    public void leaveRoom(String slug, User user) {
        Room room = roomRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + slug));

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", user.getId()));

        if (room.getCreator().getId().equals(managedUser.getId())) {
            throw new BadRequestException("Creator cannot leave the room");
        }

        managedUser.getRooms().remove(room);
        userRepository.save(managedUser);
        log.info("User {} left room {}", managedUser.getUsername(), room.getName());
    }
}
