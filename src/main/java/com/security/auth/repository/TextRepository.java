package com.security.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.security.auth.entity.Text;

public interface TextRepository extends JpaRepository<Text, Long> {
    Text findByTitle(String title);
}
