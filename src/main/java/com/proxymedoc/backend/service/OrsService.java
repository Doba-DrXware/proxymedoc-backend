package com.proxymedoc.backend.service;

import com.proxymedoc.backend.model.Pharmacie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class OrsService {

    @Value("${openrouteservice.api-key}")
    private String apiKey;

    private static final String MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/driving-car";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<Long, Double> getDrivingDistances(double originLat, double originLon, List<Pharmacie> pharmacies) {
        if (pharmacies == null || pharmacies.isEmpty()) {
            return Map.of();
        }

        List<List<Double>> locations = new ArrayList<>();
        locations.add(List.of(originLon, originLat));

        List<Pharmacie> validPharmacies = new ArrayList<>();
        for (Pharmacie pharmacy : pharmacies) {
            if (pharmacy.getLongitude() == null || pharmacy.getLatitude() == null) {
                continue;
            }
            locations.add(List.of(pharmacy.getLongitude(), pharmacy.getLatitude()));
            validPharmacies.add(pharmacy);
        }

        if (validPharmacies.isEmpty()) {
            return Map.of();
        }

        List<Integer> destinations = new ArrayList<>();
        for (int i = 0; i < validPharmacies.size(); i++) {
            destinations.add(i + 1);
        }

        Map<String, Object> request = new HashMap<>();
        request.put("locations", locations);
        request.put("sources", List.of(0));
        request.put("destinations", destinations);
        request.put("metrics", List.of("distance"));
        request.put("units", "km");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(MATRIX_URL, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Map.of();
            }

            Object distancesObj = response.getBody().get("distances");
            if (!(distancesObj instanceof List<?> distances)) {
                return Map.of();
            }

            Map<Long, Double> result = new HashMap<>();
            if (!distances.isEmpty()) {
                Object rowObj = distances.get(0);
                if (rowObj instanceof List<?> row) {
                    int index = 0;
                    for (Object distanceValue : row) {
                        if (distanceValue instanceof Number number) {
                            Pharmacie pharmacy = validPharmacies.get(index);
                            result.put(pharmacy.getId(), number.doubleValue());
                        }
                        index++;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            return Map.of();
        }
    }
}
