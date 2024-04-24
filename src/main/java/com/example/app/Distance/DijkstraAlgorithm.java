package com.example.app.Distance;

import com.example.app.Distance.GeographicGraph.Coordinate;

import java.util.*;

public class DijkstraAlgorithm {

    public static Map<Coordinate, Double> dijkstra(GeographicGraph graph, Coordinate source) {
        Map<Coordinate, Double> distance = new HashMap<>();
        Map<Coordinate, Coordinate> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.distance));
        Set<GeographicGraph.Coordinate> visited = new HashSet<>();

        for (GeographicGraph.Coordinate vertex : graph.getVertices()) {
            distance.put(vertex, Double.MAX_VALUE);
            previous.put(vertex, null);
        }

        distance.put(source,0.0);
        pq.add(new Node(source, 0.0));

        while(!pq.isEmpty()) {
            GeographicGraph.Coordinate current = pq.poll().vertex;
            if(visited.contains(current)) continue;
            visited.add(current);

            for (Map.Entry<GeographicGraph.Coordinate, Double> entry : graph.getEdges(current)) {
                GeographicGraph.Coordinate neighbor = entry.getKey();
                double weight = entry.getValue();
                double totalDistance = distance.get(current) + weight;
                if (totalDistance < distance.get(neighbor)) {
                    distance.put(neighbor, totalDistance);
                    previous.put(neighbor, current);
                    pq.add(new Node(neighbor, totalDistance));
                }
            }
        }
        return distance;
    }

    private static class Node {
        Coordinate vertex;
        double distance;

        Node(Coordinate vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
}
