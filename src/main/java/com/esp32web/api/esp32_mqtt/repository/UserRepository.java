package com.esp32web.api.esp32_mqtt.repository;

import com.esp32web.api.esp32_mqtt.model.Capteur;
import com.esp32web.api.esp32_mqtt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
