package com.example._team.service;

import com.example._team.domain.Users;
import com.example._team.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
	
	private final UserRepository userRepository;

	@Autowired
    UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Users loadUserByUsername(String email){

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
