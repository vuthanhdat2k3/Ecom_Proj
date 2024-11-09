package com.bt1.bt11_httt.Auth;

import com.bt1.bt11_httt.Config.JwtService;
import com.bt1.bt11_httt.Exception.UserNotFoundException;
import com.bt1.bt11_httt.Model.ResetPasswordRequest;
import com.bt1.bt11_httt.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> signup(@RequestBody RegisterRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> signupAdmin(@RequestBody RegisterRequest request) {
        return authenticationService.signupAdmin(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return authenticationService.logout(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String username) {
        try {
            userService.generateAndSendResetToken(username);
            return ResponseEntity.ok("Reset token has been sent to your email.");
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        System.out.println(request.getToken());
        if (userService.validateResetToken(request.getUsername(), request.getToken())) {
            System.out.println(request);
            userService.resetPassword(request.getUsername(), request.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
    }

}
