package com.security.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.security.auth.entity.TextCollection;
import com.security.auth.entity.User;

public interface TextCollectionRepository extends JpaRepository<TextCollection, Long> {
    TextCollection findById(int id);
    List<TextCollection> findByUser(User user);
}