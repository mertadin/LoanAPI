package com.bank.loan.service;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                       .username("admin")
                       .password("$2a$12$OJiNgL8BSgfBrnJiwRtinuhgcGN4D75ah3ROINE1ptXUesKvAYSx.")  // $2a$12$OJiNgL8BSgfBrnJiwRtinuhgcGN4D75ah3ROINE1ptXUesKvAYSx.
                       .roles("ADMIN")
                       .build();
        } else if ("customer".equals(username)) {
            return User.builder()
                       .username("customer")
                       .password("$2a$12$OJiNgL8BSgfBrnJiwRtinuhgcGN4D75ah3ROINE1ptXUesKvAYSx.") //$2a$12$OJiNgL8BSgfBrnJiwRtinuhgcGN4D75ah3ROINE1ptXUesKvAYSx.
                       .roles("CUSTOMER")
                       .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
