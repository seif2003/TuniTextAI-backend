package com.security.auth.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.auth.entity.Text;
import com.security.auth.entity.TextCollection;
import com.security.auth.repository.TextRepository;
import com.security.auth.repository.TextCollectionRepository;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    AiRequests aiRequests = new AiRequests();
    @Autowired
    private TextRepository textRepository;

    @Autowired
    private TextCollectionRepository textCollectionRepository;

    @PostMapping("/summarize")
    public ResponseEntity<Text> summarize(@RequestBody JsonNode json) throws IOException, InterruptedException {

        String response = aiRequests.ai(json.get("message").asText(), aiRequests.SystemSummary);


        int id = json.get("collection_id").asInt();
        
        TextCollection collection = textCollectionRepository.findById(id);
        Text result = new Text(0, json.get("title").asText(), json.get("message").asText(), "summarize", response, collection);
        textRepository.save(result);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/correct")
    public ResponseEntity<Text> correct(@RequestBody JsonNode json) throws IOException, InterruptedException {

        String response = aiRequests.ai(json.get("message").asText(), aiRequests.SystemCorrect);

        int id = json.get("collection_id").asInt();
        
        TextCollection collection = textCollectionRepository.findById(id);
        
        Text result = new Text(0, json.get("title").asText(), json.get("message").asText(), "correct", response, collection);
        textRepository.save(result);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrase")
    public ResponseEntity<Text> paraphrase(@RequestBody JsonNode json) throws IOException, InterruptedException {

        String response = aiRequests.ai(json.get("message").asText(), aiRequests.SystemPrafrase);

        int id = json.get("collection_id").asInt();
        
        TextCollection collection = textCollectionRepository.findById(id);
        
        Text result = new Text(0, json.get("title").asText(), json.get("message").asText(), "paraphrase", response, collection);
        textRepository.save(result);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteText(@RequestBody String idJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(idJson);
            Long id = jsonNode.get("id").asLong();  

            textRepository.deleteById(id);  

            return ResponseEntity.ok("Text deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/regenerate")
    public ResponseEntity<Text> regenerate(@RequestBody JsonNode json) throws IOException, InterruptedException {

        long id = json.get("id").asLong();
        Optional<Text> optionalText = textRepository.findById(id);
        Text text = optionalText.orElse(null);
        if (text == null) {
            return ResponseEntity.badRequest().build();
        }
        String operation = text.getOperation();
        if (operation.equals("summarize")) {
            text.setResult(aiRequests.reSummarize(text.getBody(), text.getResult()));
        } else if (operation.equals("correct")) {
            text.setResult(aiRequests.reCorrect(text.getBody(), text.getResult()));
        } else if (operation.equals("paraphrase")) {
            text.setResult(aiRequests.rePrafrase(text.getBody(), text.getResult()));
        }
        textRepository.save(text);
        return ResponseEntity.ok(text);
    }

}
