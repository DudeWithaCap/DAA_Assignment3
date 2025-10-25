import com.google.gson.*;
import java.io.*;
import java.util.*;

class Edge {
    String to;
    int weight;
    Edge(String to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}

class EdgeInput {
    String from;
    String to;
    int weight;
}

class Graph {
    int id;
    String[] nodes;
    EdgeInput[] edges;
}

class GraphDataset {
    Graph[] graphs;
}

public class PrimMST {
    private static int operationCount = 0;
    public static void main(String[] args) {
        try {
            InputStream inputStream = PrimMST.class.getClassLoader().getResourceAsStream("input.json");

            Reader reader = new InputStreamReader(inputStream); //reading json file
            Gson gson = new Gson();
            GraphDataset dataset = gson.fromJson(reader, GraphDataset.class);

            int targetId = 4; //hardcoded choosing of which dataset you will use
            Graph selectedGraph = null;
            for (Graph g : dataset.graphs) {
                if (g.id == targetId) {
                    selectedGraph = g;
                    break;
                }
            }

            if (selectedGraph == null) {
                System.out.println("Graph with id " + targetId + " not found"); // problem-proofing
                return;
            }

            Map<String, List<Edge>> graph = new HashMap<>();
            for (String node : selectedGraph.nodes) {
                graph.put(node, new ArrayList<>());
            }
            for (EdgeInput e : selectedGraph.edges) {
                graph.get(e.from).add(new Edge(e.to, e.weight));
                graph.get(e.to).add(new Edge(e.from, e.weight));
            }

            long startTime = System.nanoTime();
            primMST(graph, selectedGraph.nodes);
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            System.out.println("\nExecution time: " + durationMs + " ms");
            System.out.println("Operation count: " + operationCount); // count execution time in MS


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void primMST(Map<String, List<Edge>> graph, String[] nodes) {
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        String start = nodes[0];
        visited.add(start);
        pq.addAll(graph.get(start));
        operationCount += graph.get(start).size();

        int totalWeight = 0;
        List<String> mstEdges = new ArrayList<>();

        while (!pq.isEmpty() && visited.size() < nodes.length) {
            Edge edge = pq.poll();
            operationCount++; // count extraction
            if (visited.contains(edge.to)) continue;

            // Find the vertex that connects to this edge
            String from = findConnectingVertex(graph, visited, edge.to, edge.weight);
            mstEdges.add(from + " - " + edge.to + " (weight " + edge.weight + ")");
            totalWeight += edge.weight;

            visited.add(edge.to);
            for (Edge next : graph.get(edge.to)) {
                operationCount++; // count comparison/insertion
                if (!visited.contains(next.to)) pq.add(next);
            }
        }

        System.out.println("\nMinimum Spanning Tree edges:");
        for (String s : mstEdges) System.out.println("  " + s);
        System.out.println("Total weight of MST: " + totalWeight);
    }

    // Helper to find which visited node connects to the given target
    static String findConnectingVertex(Map<String, List<Edge>> graph, Set<String> visited, String target, int weight) {
        for (String v : visited) {
            for (Edge e : graph.get(v)) {
                operationCount++; // edge check
                if (e.to.equals(target) && e.weight == weight) {
                    return v;
                }
            }
        }
        return "";
    }
}
