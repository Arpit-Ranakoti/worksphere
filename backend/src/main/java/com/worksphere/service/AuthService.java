package com.worksphere.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.worksphere.dto.auth.AuthResponse;
import com.worksphere.dto.auth.LoginRequest;
import com.worksphere.dto.auth.RegisterRequest;
import com.worksphere.entity.EmployerProfile;
import com.worksphere.entity.Role;
import com.worksphere.entity.User;
import com.worksphere.entity.WorkerProfile;
import com.worksphere.exception.BadRequestException;
import com.worksphere.repository.EmployerProfileRepository;
import com.worksphere.repository.UserRepository;
import com.worksphere.repository.WorkerProfileRepository;
import com.worksphere.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager auth;
    private final JwtUtil jwt;
    private final WorkerProfileRepository workers;
    private final EmployerProfileRepository employers;

    public AuthService(UserRepository u, PasswordEncoder e, AuthenticationManager a, JwtUtil j, WorkerProfileRepository w, EmployerProfileRepository ep) {
        users = u;
        encoder = e;
        auth = a;
        jwt = j;
        workers = w;
        employers = ep;
    }

    public AuthResponse register(RegisterRequest r) {
        if (users.findByEmail(r.email()).isPresent()) {
            throw new BadRequestException("Email already registered");
        
        }User u = new User();
        u.fullName = r.fullName();
        u.email = r.email();
        u.phone = r.phone();
        u.password = encoder.encode(r.password());
        u.role = r.role() == null ? Role.JOB_SEEKER : r.role();
        u = users.save(u);
        if (u.role == Role.JOB_SEEKER) {
            WorkerProfile p = new WorkerProfile();
            p.user = u;
            workers.save(p);
        } else if (u.role == Role.EMPLOYER) {
            EmployerProfile p = new EmployerProfile();
            p.user = u;
            p.companyName = u.fullName;
            employers.save(p);
        }
        return new AuthResponse(jwt.generateToken(u.email), u);
    }

    public AuthResponse login(LoginRequest r) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
        User u = users.findByEmail(r.email()).orElseThrow();
        return new AuthResponse(jwt.generateToken(u.email), u);
    }
}
