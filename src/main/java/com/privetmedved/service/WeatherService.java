package com.privetmedved.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privetmedved.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherDto getWeather(String city) {
        try {
            String url = "https://wttr.in/" + city + "?format=j1";
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to get weather for {}: {}", city, response.code());
                    return null;
                }
                String body = response.body() != null ? response.body().string() : "";
                JsonNode root = mapper.readTree(body);
                JsonNode currentConditions = root.get("current_condition");
                if (currentConditions != null && currentConditions.isArray() && currentConditions.size() > 0) {
                    JsonNode current = currentConditions.get(0);
                    double temp = current.get("temp_C").asDouble();
                    JsonNode weatherDescNode = current.get("weatherDesc");
                    String desc = (weatherDescNode != null && weatherDescNode.isArray() && weatherDescNode.size() > 0)
                            ? weatherDescNode.get(0).get("value").asText() : "N/A";
                    WeatherDto dto = new WeatherDto();
                    dto.setCity(city);
                    dto.setTemperature(temp);
                    dto.setDescription(desc);
                    return dto;
                } else {
                    log.warn("No current_condition data for city {}", city);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching weather for {}", city, e);
            return null;
        }
    }
}
