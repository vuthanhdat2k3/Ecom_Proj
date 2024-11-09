package com.bt1.bt11_httt.Auth;


import com.bt1.bt11_httt.Config.JwtService;
import com.bt1.bt11_httt.Model.Token;
import com.bt1.bt11_httt.Model.TokenType;
import com.bt1.bt11_httt.Model.User;
import com.bt1.bt11_httt.Repository.TokenRepository;
import com.bt1.bt11_httt.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public ResponseEntity<AuthResponse> signup(RegisterRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("USER đã tồn tại!").build());
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthResponse.builder()
                        .token("")
                        .username(user.getUsername())
                        .userId(String.valueOf(user.getUserId()))
                        .role(user.getRole())
                        .message("Tạo USER thành công!").build());
    }

    public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken((UserDetails) user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthResponse.builder()
                        .token(jwtToken)
                        .username(user.getUsername())
                        .userId(String.valueOf(user.getUserId()))
                        .role(user.getRole())
                        .message(user.getRole() + " Đăng nhập thành công!")
                        .build()
                );
    }



    public ResponseEntity<AuthResponse> signupAdmin(RegisterRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("ADMIN đã tồn tại!").build());
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("ADMIN");

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthResponse.builder()
                        .token("")
                        .username(user.getUsername())
                        .userId(String.valueOf(user.getUserId()))
                        .role(user.getRole())
                        .message("Tạo ADMIN thành công!").build());
    }

    public ResponseEntity<?> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Failed!").build());
        }

        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if(storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    private void revokeAllUserTokens(User user){
        var validUserToken = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if(validUserToken.isEmpty()){
            return;
        }
        validUserToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = new Token();
        token.setUser(user);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        token.setToken(jwtToken);

        tokenRepository.save(token);
    }


}
