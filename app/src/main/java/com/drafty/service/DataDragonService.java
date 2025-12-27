package com.drafty.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import com.drafty.entity.Champion;
import com.drafty.repository.ChampionRepository;

@Service
public class DataDragonService {

    private static final Logger log = LoggerFactory.getLogger(DataDragonService.class);
    private static final String BASE_URL = "https://ddragon.leagueoflegends.com";

    private final ChampionRepository championRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper; 

    public DataDragonService(ChampionRepository championRepository, 
                           RestClient.Builder restClientBuilder,
                           ObjectMapper objectMapper) { 
    this.championRepository = championRepository;
    this.objectMapper = objectMapper;
    this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
}

public void importChampions() {
    log.info("Starting Data Dragon Import...");

    try {
        // 1. Get versions (this part is likely fine as a List)
        String versionsJson = restClient.get()
                .uri("/api/versions.json")
                .retrieve()
                .body(String.class); // Get raw string
        JsonNode versionsNode = objectMapper.readTree(versionsJson);
        String latestVersion = versionsNode.get(0).asText();
        
        log.info("Latest Patch Version detected: {}", latestVersion);

        // 2. Fetch Champion Data as String first
        String rawJson = restClient.get()
                .uri("/cdn/" + latestVersion + "/data/en_US/champion.json")
                .retrieve()
                .body(String.class); 

        // 3. Manually parse with ObjectMapper
        JsonNode rootNode = objectMapper.readTree(rawJson);
        JsonNode dataNode = rootNode.path("data");
        
        List<Champion> champsToSave = new ArrayList<>();

        if (dataNode.isObject()) {
            dataNode.properties().forEach(entry -> {
                JsonNode c = entry.getValue();
                champsToSave.add(new Champion(
                    c.get("id").asText(),
                    c.get("name").asText(),
                    c.get("title").asText(),
                    // c.get("tags"),
                    c.get("image").get("full").asText()
                ));
            });
        }

        championRepository.saveAll(champsToSave);
        log.info("Successfully imported {} champions!", champsToSave.size());

    } catch (Exception e) {
        log.error("Failed to import champions: " + e.getMessage(), e);
    }
    }
}