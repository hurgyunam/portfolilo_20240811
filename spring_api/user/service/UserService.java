package com.lab2ai.cosign.user.service;

import com.lab2ai.cosign.user.entity.User;
import com.lab2ai.cosign.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));
    }

    public boolean save(String username, String password, String nickName) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent() == false) {
            userRepository.save(new User(username, passwordEncoder.encode(password), nickName));
            return true;
        } else {
            return false;
        }
    }
}
