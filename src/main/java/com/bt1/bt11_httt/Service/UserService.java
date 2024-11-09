package com.bt1.bt11_httt.Service;

import com.bt1.bt11_httt.Exception.UserNotFoundException;
import com.bt1.bt11_httt.Model.ChangePasswordRequest;
import com.bt1.bt11_httt.Model.ResetToken;
import com.bt1.bt11_httt.Model.User;
import com.bt1.bt11_httt.Repository.ResetTokenRepository;
import com.bt1.bt11_httt.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Autowired
    private EmailService emailService;

    private boolean isValidPassword(String password) {
        return pattern.matcher(password).matches();
    }

    public User createUser(User user){
        if (userRepository.existsByUsername(user.getUsername())) {
            return null; // Nếu username đã tồn tại, trả về null
        }
        if (!isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the required criteria.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String username, String password, String Role) {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            User user = optionalCustomer.get();
            if (passwordEncoder.matches(password, user.getPassword()) && Role.equals("user")) {
                return user; // Trả về khách hàng nếu mật khẩu khớp
            }
        }
        return null; // Trả về null nếu đăng nhập thất bại
    }

    public User loginAdmin(String username, String password, String Role) {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            User user = optionalCustomer.get();
            if (passwordEncoder.matches(password, user.getPassword()) && Role.equals("admin")) {
                return user; // Trả về khách hàng nếu mật khẩu khớp
            }
        }
        return null; // Trả về null nếu đăng nhập thất bại
    }


    public boolean existsByUsername(String username) {
        // Kiểm tra xem username đã tồn tại trong cơ sở dữ liệu hay chưa
        return userRepository.existsByUsername(username);
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
    public User updateUser(String username, User userDetails) {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            User existingUser = optionalCustomer.get();
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            return userRepository.save(existingUser);
        } else {
            return null;
        }

    }
    public User changedUser(String username, ChangePasswordRequest request) {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            User existingUser = optionalCustomer.get();
            if (passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
                if(!isValidPassword(request.getNewPassword())) return null;
                existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                return userRepository.save(existingUser);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public User resetUser(String username, String password) {
        Optional<User> optionalCustomer = userRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            User existingUser = optionalCustomer.get();
            existingUser.setPassword(passwordEncoder.encode(password));
            return userRepository.save(existingUser);
        } else {
            return null;
        }

    }

    public void deleteUser(String username) {
        int id = userRepository.findByUsername(username).get().getUserId();
        userRepository.deleteById(id);
    }


    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void generateAndSendResetToken(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = generateResetToken();
            ResetToken resetToken = resetTokenRepository.findByUsername(username);

            if (resetToken != null) {
                resetToken.setToken(token);
                resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
            } else {
                resetToken = new ResetToken();
                resetToken.setUsername(username);
                resetToken.setToken(token);
                resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
            }

            resetTokenRepository.save(resetToken);

            String emailText = "Use the following token to reset your password: " + token;
            emailService.sendEmail(user.getEmail(), "Password Reset Request", emailText);
        } else {
            throw new UserNotFoundException("User with username " + username + " not found");
        }
    }


    public boolean validateResetToken(String username, String token) {
        ResetToken resetToken = resetTokenRepository.findByUsername(username);
        if (resetToken != null && resetToken.getToken().equals(token.strip()) && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    @Transactional
    public void resetPassword(String username, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!isValidPassword(newPassword)) {
                throw new IllegalArgumentException("Password does not meet the required criteria.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            resetTokenRepository.deleteByUsername(username); // Clean up used token
        }
    }

    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        int token = 100000 + random.nextInt(900000); // Sinh số ngẫu nhiên từ 100000 đến 999999
        return String.valueOf(token);
    }
}
