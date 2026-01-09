package com.ticketbooking.repository;

import com.ticketbooking.entity.Event;
import com.ticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedBy(User user);
    List<Event> findByEventDateAfter(LocalDateTime date);
    List<Event> findByVenueContainingIgnoreCase(String venue);
    boolean existsByNameAndVenueAndEventDate(String name, String venue, LocalDateTime eventDate);
}
