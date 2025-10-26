import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;

public class MSTAlgorithmsTest {
    private boolean isAcyclic(List<String> edges, String[] nodes) {
        Map<String, String> parent = new HashMap<>();
        for (String node : nodes) parent.put(node, node);

        for (String e : edges) {
            String[] parts = e.split(" - ");
            String from = parts[0];
            String to = parts[1].split(" ")[0];

            String rootFrom = find(parent, from);
            String rootTo = find(parent, to);

            if (rootFrom.equals(rootTo)) return false; // cycle detected
            parent.put(rootTo, rootFrom); // union
        }
        return true;
    }

    private String find(Map<String, String> parent, String v) {
        if (!parent.get(v).equals(v)) parent.put(v, find(parent, parent.get(v)));
        return parent.get(v);
    }

    private boolean isConnected(List<String> edges, String[] nodes) {
        Map<String, List<String>> adj = new HashMap<>();
        for (String node : nodes) adj.put(node, new ArrayList<>());
        for (String e : edges) {
            String[] parts = e.split(" - ");
            String from = parts[0];
            String to = parts[1].split(" ")[0];
            adj.get(from).add(to);
            adj.get(to).add(from);
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(nodes[0]);
        visited.add(nodes[0]);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            for (String neigh : adj.get(curr)) {
                if (!visited.contains(neigh)) {
                    visited.add(neigh);
                    queue.add(neigh);
                }
            }
        }

        return visited.size() == nodes.length;
    }

    @Test
    void testGraphId5_PrimVsKruskal() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("input.json");
        assertNotNull(inputStream, "input.json not found in resources folder");
        Reader reader = new InputStreamReader(inputStream);
        Gson gson = new Gson();
        GraphDataset dataset = gson.fromJson(reader, GraphDataset.class);

        int targetId = 5;
        Graph g = Arrays.stream(dataset.graphs)
                .filter(graph -> graph.id == targetId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Graph with id=" + targetId + " not found"));

        Map<String, List<Edge>> graph = new HashMap<>();
        for (String node : g.nodes) graph.put(node, new ArrayList<>());
        for (EdgeInput e : g.edges) {
            graph.get(e.from).add(new Edge(e.to, e.weight));
            graph.get(e.to).add(new Edge(e.from, e.weight));
        }

        long primStart = System.nanoTime();
        PrimMST.primMST(graph, g.nodes);
        long primEnd = System.nanoTime();
        long primTimeMs = (primEnd - primStart) / 1_000_000;

        MSTResult primResult = new MSTResult(
                g.id,
                PrimMST.getMstEdgesGlobal(),
                PrimMST.getTotalWeightGlobal(),
                primTimeMs,
                PrimMST.getOperationCount()
        );


        List<KruskalMST.KruskalEdge> edges = new ArrayList<>();
        for (EdgeInput e : g.edges) edges.add(new KruskalMST.KruskalEdge(e.from, e.to, e.weight));

        long kruskalStart = System.nanoTime();
        MSTResult kruskalResult = KruskalMST.runKruskal(g.nodes, edges, g.id);
        long kruskalEnd = System.nanoTime();
        kruskalResult.executionTimeMs = (kruskalEnd - kruskalStart) / 1_000_000;


        assertEquals(primResult.totalWeight, kruskalResult.totalWeight, "MST total weights must match");

        assertEquals(g.nodes.length - 1, primResult.edges.size(), "Prim MST must have V-1 edges");
        assertEquals(g.nodes.length - 1, kruskalResult.edges.size(), "Kruskal MST must have V-1 edges");

        assertTrue(isAcyclic(primResult.edges, g.nodes), "Prim MST must be acyclic");
        assertTrue(isAcyclic(kruskalResult.edges, g.nodes), "Kruskal MST must be acyclic");

        assertTrue(isConnected(primResult.edges, g.nodes), "Prim MST must connect all vertices");
        assertTrue(isConnected(kruskalResult.edges, g.nodes), "Kruskal MST must connect all vertices");


        assertTrue(primResult.executionTimeMs >= 0, "Prim execution time must be non-negative");
        assertTrue(kruskalResult.executionTimeMs >= 0, "Kruskal execution time must be non-negative");

        assertTrue(primResult.operationCount >= 0, "Prim operation count must be non-negative");
        assertTrue(kruskalResult.operationCount >= 0, "Kruskal operation count must be non-negative");

        System.out.println("Prim MST weight: " + primResult.totalWeight);
        System.out.println("Kruskal MST weight: " + kruskalResult.totalWeight);
        System.out.println("Prim execution time: " + primResult.executionTimeMs + " ms");
        System.out.println("Kruskal execution time: " + kruskalResult.executionTimeMs + " ms");
    }
}
