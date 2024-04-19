package com.example.app.Distance;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class GeographicGraph {

    private Map<Coordinate, Map<Coordinate, Double>> graph;

    public GeographicGraph(List<GeoIPWithTravelInfo> vertices) {
        this.graph = new HashMap<>();

        for (GeoIPWithTravelInfo vertex : vertices) {
            Coordinate coordinate = new Coordinate(vertex.getId(), vertex.getLongitude(), vertex.getLatitude());
            addVertex(coordinate);
        }
    }

    public void addVertex(Coordinate coordinate) {
        if(!graph.containsKey(coordinate)) {
            graph.put(coordinate, new HashMap<>());
        }
    }

    public void addEdge(Coordinate source, Coordinate destination, double weight) {
        graph.get(source).put(destination, weight);
    }

    public static class Coordinate {
        private Long id;
        private double longitude;
        private double latitude;

        public Coordinate(Long id, double longitude, double latitude) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0 && id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, longitude, latitude);
        }

        public Long getId() {
            return id;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }
}
