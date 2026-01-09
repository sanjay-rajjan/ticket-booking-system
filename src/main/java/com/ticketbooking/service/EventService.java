package com.ticketbooking.service;

import com.ticketbooking.entity.Event;
import com.ticketbooking.entity.Role;
import com.ticketbooking.entity.User;
import com.ticketbooking.exception.BadRequestException;
import com.ticketbooking.exception.ConflictException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.exception.UnauthorizedException;
import com.ticketbooking.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(String name, String description, String venue, LocalDateTime eventDate,
                             Integer totalSeats, User createdBy) {
        if (!createdBy.getRole().equals(Role.HOST)) {
            throw new UnauthorizedException("Only hosts can create events");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Event name is required");
        }
        if (venue == null || venue.trim().isEmpty()) {
            throw new BadRequestException("Venue is required");
        }
        if (eventDate == null) {
            throw new BadRequestException("Event date is required");
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date must be in the future");
        }
        if (totalSeats == null || totalSeats <= 0) {
            throw new BadRequestException("Total seats must be greater than 0");
        }

        if (eventRepository.existsByNameAndVenueAndEventDate(name, venue, eventDate)) {
            throw new ConflictException("An event with this name already exists at this venue on this date/time");
        }
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setVenue(venue);
        event.setEventDate(eventDate);
        event.setTotalSeats(totalSeats);
        event.setAvailableSeats(totalSeats);
        event.setCreatedBy(createdBy);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now());
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    public List<Event> searchEventsByVenue(String venue) {
        if (venue == null || venue.trim().isEmpty()) {
            return getAllEvents();
        }
        return eventRepository.findByVenueContainingIgnoreCase(venue);
    }

    public List<Event> getEventsByCreator(User user) {
        return eventRepository.findByCreatedBy(user);
    }

    public Event updateEvent(Long id, String name, String description, String venue, LocalDateTime eventDate,
                             Integer totalSeats, User currentUser) {
        Event event = getEventById(id);
        if (!(event.getCreatedBy().getId().equals(currentUser.getId()) || currentUser.getRole().equals(Role.ADMIN))) {
            throw new UnauthorizedException("You are not authorized to update this event");
        }

        if (name != null && !name.trim().isEmpty()) {
            event.setName(name);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (venue != null && !venue.trim().isEmpty()) {
            event.setVenue(venue);
        }
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Event date must be in the future");
            }
            event.setEventDate(eventDate);
        }
        if (totalSeats != null) {
            if (totalSeats <= 0) {
                throw new BadRequestException("Total seats must be greater then 0");
            }
            event.setTotalSeats(totalSeats);
        }
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id, User currentUser) {
        Event event = getEventById(id);
        if (!(event.getCreatedBy().getId().equals(currentUser.getId()) ||
        currentUser.getRole().equals(Role.ADMIN))) {
            throw new UnauthorizedException("You are not authorized to delete this event");
        }
        eventRepository.deleteById(id);
    }
}
