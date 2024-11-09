package com.bt1.bt11_httt.Config;//package com.examHaha.Config;
//
//import com.examHaha.Model.User;
//import com.examHaha.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UserRepository userRepository;
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//        String password = authentication.getCredentials().toString();
//        Optional<User> optionalCustomer = userRepository.findByUsername(username);
//
//        if (optionalCustomer.isEmpty()) {
//            throw new BadCredentialsException("No customer registered with this username= " + username);
//        }
//
//        User user = optionalCustomer.get();
//        if (passwordEncoder.matches(password, user.getPassword())) {
//            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRole());
//            return new UsernamePasswordAuthenticationToken(username, password, authorities);
//        } else {
//            throw new BadCredentialsException("Invalid password for username= " + username);
//        }
//    }
//
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
//    }
//}
