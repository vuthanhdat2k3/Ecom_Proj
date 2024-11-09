package com.bt1.bt11_httt.Service;

import com.bt1.bt11_httt.Model.User;
import com.bt1.bt11_httt.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizUserDetails implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isEmpty()) {
            throw new UsernameNotFoundException("User details not found for username= " + username);
        }

        User user = optionalCustomer.get();
        String password = user.getPassword();
        String role = user.getRole();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);

        return new org.springframework.security.core.userdetails.User(username, password, authorities);
    }

}
