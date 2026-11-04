package com.worksphere.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.entity.User;
import com.worksphere.exception.ResourceNotFoundException;
import com.worksphere.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository u) {
        users = u;
    }

    public User current(Authentication a) {
        return users.findByEmail(a.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateCurrent(Authentication a, com.worksphere.dto.profile.UserProfileRequest r) {
        User user = current(a);
        if (r.fullName() != null && !r.fullName().isBlank()) user.fullName = r.fullName().trim();
        user.phone = r.phone();
        return users.save(user);
    }

    public User byId(Long id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> all() {
        return users.findAllByOrderByCreatedAtDesc();
    }
}
