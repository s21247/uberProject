package com.example.app.Distance;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class GeographicGraph {

    private Map<Coordinate, Map<Coordinate, Double>> graph;

    public GeographicGraph(List<GeoIPWithTravelInfo> vertices, GeoIPWithTravelInfo user) {
        this.graph = new HashMap<>();
        Coordinate source = new Coordinate(user.getId(), user.getLongitude(), user.getLatitude());
        addVertex(source);

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

    public Set<Coordinate> getVertices() {
        return  graph.keySet();
    }

    public Set<Map.Entry<Coordinate, Double>> getEdges(Coordinate vertex) {
        Map<Coordinate, Double> edgesForVertex = graph.get(vertex);
        if (edgesForVertex != null) {
            return edgesForVertex.entrySet();
        } else {
            return Collections.emptySet();
        }
    }

    public void addEdge(Coordinate source, Coordinate destination, double weight) {
        if (!graph.containsKey(destination)) {
            addVertex(destination);
        }
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
