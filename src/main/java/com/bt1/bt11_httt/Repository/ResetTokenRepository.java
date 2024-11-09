package com.bt1.bt11_httt.Repository;

import com.bt1.bt11_httt.Model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends CrudRepository<ResetToken, Long> {
    ResetToken findByUsername(String username);
    void deleteByUsername(String username);
    Optional<ResetToken> findByToken(String token);

}

