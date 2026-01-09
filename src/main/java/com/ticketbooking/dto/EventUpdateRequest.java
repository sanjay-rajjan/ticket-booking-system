package com.ticketbooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventUpdateRequest {
    private String name;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private Integer totalSeats;
}
