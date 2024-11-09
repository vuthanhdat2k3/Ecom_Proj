package com.bt1.bt11_httt.Controller;

import com.bt1.bt11_httt.Model.ChangePasswordRequest;
import com.bt1.bt11_httt.Model.User;
import com.bt1.bt11_httt.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;


    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> optionalCustomer = userService.findUserByUsername(username);
        if (optionalCustomer.isPresent()) {
            return ResponseEntity.ok(optionalCustomer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/listUser")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/edit/{username}")
    public ResponseEntity<User> updateUser(@PathVariable("username") String username, @RequestBody ChangePasswordRequest request) {
        System.out.println(request);
        User changedUser = userService.changedUser(username, request);
        if (changedUser != null) {
            return ResponseEntity.ok(changedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }



}
