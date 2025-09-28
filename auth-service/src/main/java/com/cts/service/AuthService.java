package com.cts.service;

import com.cts.dtos.LoginDto;
import com.cts.dtos.RegisterDto;


public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}