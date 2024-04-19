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

import java.util.*;

@RequiredArgsConstructor
@Service
public class DistanceService {

    final String baseUrl = "https://api.openrouteservice.org/v2/matrix/driving-car"; // Profile: driving-car
    @Value("${api_openrouteservice_key}")
    final private String API_KEY;

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

        for (int i = 0; i < destinationsNode.size(); i++) {
            TravelInfo travelInfo = new TravelInfo();
            travelInfo.setDuration(durationsNode.get(i).asDouble());
            travelInfo.setLongitude(destinationsNode.get("location").get(0).asDouble());
            travelInfo.setLatitude(destinationsNode.get("location").get(1).asDouble());
            travelInfo.setSnappedDistance(destinationsNode.get("snapped_distance").asDouble());
            travelInfoList.add(travelInfo);
        }
        return travelInfoList;

    }

    public List<GeoIPWithTravelInfo> mergeLists(List<TravelInfo> travelInfoList, List<GeoIPEntity> geoIPEntityList ) {
        Map<List<Double>, GeoIPEntity> geoIPMap = new HashMap<>();

        for (GeoIPEntity geoIP : geoIPEntityList) {
            List<Double> key = Arrays.asList(geoIP.getLatitude(), geoIP.getLongitude());
            geoIPMap.put(key, geoIP);
        }

        List<GeoIPWithTravelInfo> mergedList = new ArrayList<>();
        for (TravelInfo travelInfo : travelInfoList) {
            List<Double> key = Arrays.asList(travelInfo.getLatitude(), travelInfo.getLongitude());
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

    public void initializeGraph(Long id, List<GeoIPEntity> geoIPEntityList) throws JsonProcessingException {
        List<TravelInfo> travelInfoList = getDistancesFromOpenRouteService(geoIPEntityList, id);
        List<GeoIPWithTravelInfo> mergedList = mergeLists(travelInfoList, geoIPEntityList);

        GeographicGraph graph = new GeographicGraph(mergedList);

        GeoIPWithTravelInfo source = null;
        for (GeoIPWithTravelInfo element : mergedList) {
            if(element.getDuration() == 0) {
                source = element;
                break;
            }
        }
        if (source != null) {
            for (int i = 0; i < mergedList.size(); i++) {
                GeoIPWithTravelInfo destination = mergedList.get(i);
                if(destination != source && destination.getDuration() != 0) {
                    GeographicGraph.Coordinate sourceCoordinate = new GeographicGraph.Coordinate(source.getId(), source.getLongitude(), source.getLatitude());
                    GeographicGraph.Coordinate destinationCoordinate = new GeographicGraph.Coordinate(destination.getId(), destination.getLongitude(),destination.getLatitude());
                    graph.addEdge(sourceCoordinate, destinationCoordinate, destination.getSnapped_distance());
                }
            }
        }
    }


}
