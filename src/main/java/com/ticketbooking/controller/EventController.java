package com.ticketbooking.controller;

import com.ticketbooking.dto.EventRequest;
import com.ticketbooking.dto.EventUpdateRequest;
import com.ticketbooking.entity.Event;
import com.ticketbooking.entity.Role;
import com.ticketbooking.entity.User;
import com.ticketbooking.repository.UserRepository;
import com.ticketbooking.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EventRequest request) {
        User currentUser = getCurrentUser(userDetails);
        if (!currentUser.getRole().equals(Role.HOST)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Event createdEvent = eventService.createEvent(
            request.getName(), request.getDescription(), request.getVenue(),
            request.getEventDate(), request.getTotalSeats(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEventsByVenue(@RequestParam String venue) {
        List<Event> events = eventService.searchEventsByVenue(venue);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Event>> getMyEvents(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Event> events = eventService.getEventsByCreator(currentUser);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody EventUpdateRequest request) {
        User currentUser = getCurrentUser(userDetails);
        Event updatedEvent = eventService.updateEvent(id, request.getName(), request.getDescription(),
            request.getVenue(), request.getEventDate(), request.getTotalSeats(), currentUser);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User currentUser = getCurrentUser(userDetails);
        eventService.deleteEvent(id, currentUser);
        return ResponseEntity.noContent().build();
    }

}
