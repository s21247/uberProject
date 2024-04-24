package com.example.app.Distance;

import com.example.app.GeoIP.GeoIPEntity;
import com.example.app.GeoIP.GeoIPLocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequiredArgsConstructor
@Service
public class DistanceService {

    final String baseUrl = "https://api.openrouteservice.org/v2/matrix/driving-car"; // Profile: driving-car
    @Value("${api_openrouteservice_key}")
    private String API_KEY;

    private final GeoIPLocationService geoIPLocationService;
    private final RestTemplate restTemplate;

    public List<TravelInfo> getDistancesFromOpenRouteService(List<GeoIPEntity> list, long id) throws JsonProcessingException {
        List<List<Double>> locations = new ArrayList<>();
        GeoIPEntity driver = geoIPLocationService.getDriverById(id);
        locations.add(Arrays.asList(driver.getLongitude(), driver.getLatitude()));

        for (GeoIPEntity element : list) {
            locations.add(Arrays.asList(element.getLongitude(), element.getLatitude()));
        }

        List<Integer> sources = Collections.singletonList(0);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sources", sources);
        requestBody.put("locations", locations);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        return parseCalculateDistanceJson(responseEntity);
    }

    private List<TravelInfo> parseCalculateDistanceJson(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        String responseBody = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        JsonNode durationsNode = jsonResponse.get("durations");
        JsonNode destinationsNode = jsonResponse.get("destinations");

        List<TravelInfo> travelInfoList = new ArrayList<>();

        if (durationsNode != null && durationsNode.isArray() && destinationsNode != null && destinationsNode.isArray()) {
            for (int i = 0; i < destinationsNode.size(); i++) {
                JsonNode destinationNode = destinationsNode.get(i);
                JsonNode locationNode = destinationNode.get("location");
                JsonNode snappedDistanceNode = destinationNode.get("snapped_distance");
                double duration = durationsNode.get(0).get(i).asDouble();

                if (locationNode != null && locationNode.isArray() && locationNode.size() >= 2 && snappedDistanceNode != null && snappedDistanceNode.isDouble()) {
                    double longitude = locationNode.get(0).asDouble();
                    double latitude = locationNode.get(1).asDouble();
                    double snappedDistance = snappedDistanceNode.asDouble();

                    TravelInfo travelInfo = new TravelInfo();
                    travelInfo.setLongitude(BigDecimal.valueOf(longitude).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    travelInfo.setLatitude(BigDecimal.valueOf(latitude).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    travelInfo.setSnappedDistance(snappedDistance);
                    travelInfo.setDuration(duration);
                    travelInfoList.add(travelInfo);
                } else {
                    System.err.println("There is an issue processing destination data");
                }
            }
        } else {
            System.err.println("Duration array or destination array not initialized from outside API");
        }

        return travelInfoList;
    }

    public List<GeoIPWithTravelInfo> mergeLists(List<TravelInfo> travelInfoList, List<GeoIPEntity> geoIPEntityList ) {
        Map<GeoCoordinate, GeoIPEntity> geoIPMap = new HashMap<>();

        for (GeoIPEntity geoIP : geoIPEntityList) {
            GeoCoordinate key = new GeoCoordinate(geoIP.getLongitude(), geoIP.getLatitude());
            geoIPMap.put(key, geoIP);
        }

        List<GeoIPWithTravelInfo> mergedList = new ArrayList<>();
        for (TravelInfo travelInfo : travelInfoList) {
            GeoCoordinate key = new GeoCoordinate(travelInfo.getLongitude(), travelInfo.getLatitude());
            GeoIPEntity geoIPEntity = geoIPMap.get(key);
            if (geoIPEntity != null) {
                GeoIPWithTravelInfo geoIPWithTravelInfo = new GeoIPWithTravelInfo();
                geoIPWithTravelInfo.setId(geoIPEntity.getId());
                geoIPWithTravelInfo.setLatitude(geoIPEntity.getLatitude());
                geoIPWithTravelInfo.setLongitude(geoIPEntity.getLongitude());
                geoIPWithTravelInfo.setDuration(travelInfo.getDuration());
                geoIPWithTravelInfo.setSnapped_distance(travelInfo.getSnappedDistance());

                mergedList.add(geoIPWithTravelInfo);
            }
        }
        return mergedList;

    }

    public GeographicGraph initializeGraph(Long id, List<GeoIPEntity> geoIPEntityList) throws JsonProcessingException {
        List<TravelInfo> travelInfoList = getDistancesFromOpenRouteService(geoIPEntityList, id);
        List<GeoIPWithTravelInfo> mergedList = mergeLists(travelInfoList, geoIPEntityList);

        GeoIPEntity driver = geoIPLocationService.getDriverById(id);
        GeoIPWithTravelInfo source = null;
        if (driver != null) {
            source = new GeoIPWithTravelInfo();
            source.setId(driver.getId());
            source.setLatitude(driver.getLatitude());
            source.setLongitude(driver.getLongitude());
            source.setDuration(0.0);
            source.setSnapped_distance(0.0);
        }
        return getGeographicGraph(source, mergedList);
    }

    private static GeographicGraph getGeographicGraph(GeoIPWithTravelInfo source, List<GeoIPWithTravelInfo> mergedList) {
        GeographicGraph graph = null;
        if(source != null && !mergedList.isEmpty()) {
             graph = new GeographicGraph(mergedList, source);

            for (GeoIPWithTravelInfo destination : mergedList) {
                if (destination != source && destination.getDuration() != 0) {
                    GeographicGraph.Coordinate sourceCoordinate = new GeographicGraph.Coordinate(source.getId(), source.getLongitude(), source.getLatitude());
                    GeographicGraph.Coordinate destinationCoordinate = new GeographicGraph.Coordinate(destination.getId(), destination.getLongitude(), destination.getLatitude());
                    graph.addEdge(sourceCoordinate, destinationCoordinate, destination.getSnapped_distance());
                }
            }
        }
        return graph;
    }


}
