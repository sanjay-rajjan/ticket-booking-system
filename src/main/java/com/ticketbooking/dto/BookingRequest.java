package com.ticketbooking.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long eventId;
    private Integer numberOfSeats;
}
