package com.security.auth.controller;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AiRequests {
    final String SystemSummary = "You are AI system that summarizes text. just summarize the text, do not include any other information.";
    final String SystemCorrect = "You are AI system that corrects text. just correct the text, do not include any other information.";
    final String SystemPrafrase = "You are AI system that prafrases text. just prafrase the text, do not include any other information.";

    public String ai(String message,String system) throws IOException, InterruptedException {
        String apiKey = "lm-studio";
        String model = "lmstudio-community/Meta-Llama-3-8B-Instruct-GGUF";
        double temperature = 0.7;
        // Create the JSON objects
        JSONObject jsonObject = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        // Create the system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", system);

        // Create the user message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        // Add messages to the array
        messagesArray.put(systemMessage);
        messagesArray.put(userMessage);

        // Add elements to the main JSON object
        jsonObject.put("model", model);
        jsonObject.put("messages", messagesArray);
        jsonObject.put("temperature", temperature);

        // Convert the JSON object to a string
        String jsonString = jsonObject.toString();
        URL url = new URL("http://localhost:1234/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes(jsonString);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = bf.readLine()) != null) {
                response.append(line);
            }
        }
                ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the JSON string
            JsonNode jsonNode = mapper.readTree(response.toString());

            // Extract the message content from the first choice
            String messageContent = jsonNode.get("choices").get(0).get("message").get("content").asText();

            // return the message content
            return messageContent;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String summarize(String message) throws IOException, InterruptedException {
        return ai(message,SystemSummary);
    }

    public String correct(String message) throws IOException, InterruptedException {
        return ai(message,SystemCorrect);
    }

    public String prafrase(String message) throws IOException, InterruptedException {
        return ai(message,SystemPrafrase);
    }

    public String reAi(String oldBody,String oldResult,String system) throws IOException, InterruptedException {
        String apiKey = "lm-studio";
        String model = "lmstudio-community/Meta-Llama-3-8B-Instruct-GGUF";
        double temperature = 0.7;
        // Create the JSON objects
        JSONObject jsonObject = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        // Create the system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", system);

        // Create the user message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", oldBody);

        JSONObject userMessage2 = new JSONObject();
        userMessage2.put("role", "assistant");
        userMessage2.put("content", oldResult);

        JSONObject userMessage3 = new JSONObject();
        userMessage3.put("role", "user");
        userMessage3.put("content", "Regenerate");

        // Add messages to the array
        messagesArray.put(systemMessage);
        messagesArray.put(userMessage);
        messagesArray.put(userMessage2);
        messagesArray.put(userMessage3);

        // Add elements to the main JSON object
        jsonObject.put("model", model);
        jsonObject.put("messages", messagesArray);
        jsonObject.put("temperature", temperature);

        // Convert the JSON object to a string
        String jsonString = jsonObject.toString();
        URL url = new URL("http://localhost:1234/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes(jsonString);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = bf.readLine()) != null) {
                response.append(line);
            }
        }
                ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the JSON string
            JsonNode jsonNode = mapper.readTree(response.toString());

            // Extract the message content from the first choice
            String messageContent = jsonNode.get("choices").get(0).get("message").get("content").asText();

            // return the message content
            return messageContent;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String reSummarize(String oldBody,String oldResult) throws IOException, InterruptedException {
        return reAi(oldBody,oldResult,SystemSummary);
    }

    public String reCorrect(String oldBody,String oldResult) throws IOException, InterruptedException {
        return reAi(oldBody,oldResult,SystemCorrect);
    }

    public String rePrafrase(String oldBody,String oldResult) throws IOException, InterruptedException {
        return reAi(oldBody,oldResult,SystemPrafrase);
    }

}
