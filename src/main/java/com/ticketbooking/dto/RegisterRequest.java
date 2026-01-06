package com.ticketbooking.dto;

import com.ticketbooking.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;
}
