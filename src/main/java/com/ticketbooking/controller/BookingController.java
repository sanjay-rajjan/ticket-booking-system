package com.ticketbooking.controller;

import com.ticketbooking.dto.BookingRequest;
import com.ticketbooking.entity.Booking;
import com.ticketbooking.entity.User;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.repository.UserRepository;
import com.ticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody BookingRequest request) {
        User currentUser = getCurrentUser(userDetails);
        Booking booking = bookingService.createBooking(request.getEventId(), request.getNumberOfSeats(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Booking> bookings = bookingService.getUserBookings(currentUser);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my/confirmed")
    public ResponseEntity<List<Booking>> getMyConfirmedBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Booking> bookings = bookingService.getConfirmedUserBookings(currentUser);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long id){
        User currentUser = getCurrentUser(userDetails);
        Booking booking = bookingService.getBookingById(id, currentUser);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        User currentUser = getCurrentUser(userDetails);
        bookingService.cancelBooking(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Booking>> getEventBookings(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable Long eventId) {
        User currentUser = getCurrentUser(userDetails);
        List<Booking> bookings = bookingService.getEventBookings(eventId, currentUser);
        return ResponseEntity.ok(bookings);
    }

}
