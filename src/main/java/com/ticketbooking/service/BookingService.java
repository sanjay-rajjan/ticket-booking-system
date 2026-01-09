package com.ticketbooking.service;

import com.ticketbooking.entity.*;
import com.ticketbooking.exception.BadRequestException;
import com.ticketbooking.exception.ConflictException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.exception.UnauthorizedException;
import com.ticketbooking.repository.BookingRepository;
import com.ticketbooking.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Booking createBooking(Long eventId, Integer numberOfSeats, User user) {
        if (eventId == null) {
            throw new BadRequestException("Event ID is required");
        }
        if (numberOfSeats == null || numberOfSeats <= 0) {
            throw new BadRequestException("Number of seats must be greater than 0");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book tickets for past events");
        }
        if (bookingRepository.existsByUserAndEventAndStatus(user, event, BookingStatus.CONFIRMED)) {
            throw new ConflictException("You already have a booking for this event");
        }
        if (event.getAvailableSeats() < numberOfSeats) {
            throw new BadRequestException("Not enough seats available. Only " + event.getAvailableSeats() + " seats remaining");
        }

        event.setAvailableSeats(event.getAvailableSeats() - numberOfSeats);
        eventRepository.save(event);
        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    public List<Booking> getConfirmedUserBookings(User user) {
        return bookingRepository.findByUserAndStatus(user, BookingStatus.CONFIRMED);
    }

    public List<Booking> getEventBookings(Long eventId, User currentUser) {
        Event event =
            eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(
                "Event not found with id: " + eventId));
        if (!event.getCreatedBy().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedException("You are not authorized to view the bookings for this" +
                " event");
        }
        return bookingRepository.findByEvent(event);
    }

    public Booking getBookingById(Long id, User currentUser) {
        Booking booking =
            bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Booking not found with id: " + id));
        if (!booking.getUser().getId().equals(currentUser.getId()) && !booking.getEvent().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }
        return booking;
    }

    @Transactional
    public void cancelBooking(Long id, User currentUser) {
        Booking booking =
            bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Booking not found with id: " + id));
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only cancel your own bookings");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }
        if (booking.getEvent().getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel bookings for past events");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);
    }
}
