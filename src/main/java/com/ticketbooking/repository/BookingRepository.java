package com.ticketbooking.repository;

import com.ticketbooking.entity.Booking;
import com.ticketbooking.entity.BookingStatus;
import com.ticketbooking.entity.Event;
import com.ticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);
    List<Booking> findByEvent(Event event);
    List<Booking> findByUserAndStatus(User user, BookingStatus status);
    List<Booking> findByEventAndStatus(Event event, BookingStatus status);
    boolean existsByUserAndEventAndStatus(User user, Event event, BookingStatus status);
}
