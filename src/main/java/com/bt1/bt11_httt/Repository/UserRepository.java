package com.bt1.bt11_httt.Repository;


import com.bt1.bt11_httt.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findByRoleContaining(String role);

}
