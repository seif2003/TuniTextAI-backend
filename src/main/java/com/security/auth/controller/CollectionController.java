package com.security.auth.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.auth.entity.TextCollection;
import com.security.auth.entity.User;
import com.security.auth.repository.TextCollectionRepository;
import com.security.auth.repository.UserRepository;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {
    // recive title of collection and create a new collection
    @Autowired
    private TextCollectionRepository textCollectionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/create")
    public ResponseEntity<TextCollection> createCollection(@RequestBody String titleJson, Authentication authentication) {
        try {
            // Parse the JSON string to extract the title
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(titleJson);
            String title = jsonNode.get("title").asText();

            // Get the authenticated user
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername());

            // Save the new collection
            TextCollection newCollection = new TextCollection(user, title);
            textCollectionRepository.save(newCollection);
            //initialize the texts list 
            newCollection.setTexts(new ArrayList<>());
            
            //return the collection object
            return ResponseEntity.ok(newCollection);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public List<TextCollection> getAllCollections(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        return textCollectionRepository.findByUser(user);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCollection(@RequestBody String idJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(idJson);
            Long id = jsonNode.get("id").asLong();  
    
            textCollectionRepository.deleteById(id);  
    
            return ResponseEntity.ok("Collection deleted successfully");
    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
