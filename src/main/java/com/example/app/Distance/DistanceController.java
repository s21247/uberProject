package com.example.app.Distance;

import com.example.app.GeoIP.GeoIPLocationService;
import com.example.app.User.UserEntity;
import com.example.app.User.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/findDrive")
public class DistanceController {

    private final DistanceService distanceService;
    private final UserService userService;
    private final GeoIPLocationService geoIPLocationService;

    @GetMapping
    public Map<GeographicGraph.Coordinate, Double> test() throws JsonProcessingException {
        UserEntity userEntity = userService.findAuthenticatedUser();
        GeographicGraph graph = distanceService.initializeGraph(userEntity.getId(), geoIPLocationService.findAllActiveUsers());

        return DijkstraAlgorithm.dijkstra(graph, new GeographicGraph.Coordinate(userEntity.getId(),
                userEntity.getGeoIPEntity().getLongitude(),userEntity.getGeoIPEntity().getLatitude()));
    }
}
