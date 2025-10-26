import com.google.gson.*;
import java.io.*;
import java.util.*;

public class KruskalMST {
    private static long operationCount = 0;

    static class KruskalEdge {
        String from, to;
        int weight;

        KruskalEdge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();

        void makeSet(String v) {
            parent.put(v, v);
            operationCount++;
        }

        String find(String v) {
            operationCount++;
            if (!parent.get(v).equals(v))
                parent.put(v, find(parent.get(v)));
            return parent.get(v);
        }

        void union(String a, String b) {
            operationCount++;
            a = find(a);
            b = find(b);
            if (!a.equals(b))
                parent.put(b, a);
        }
    }

    public static void main(String[] args) {
        try {
            InputStream inputStream = KruskalMST.class.getClassLoader().getResourceAsStream("input.json");

            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            GraphDataset dataset = gson.fromJson(reader, GraphDataset.class);

            int targetId = 1;
            Graph selectedGraph = null;
            for (Graph g : dataset.graphs) {
                if (g.id == targetId) {
                    selectedGraph = g;
                    break;
                }
            }

            if (selectedGraph == null) {
                System.out.println("Graph with id " + targetId + " not found");
                return; //problem-proofing again
            }

            List<KruskalEdge> edges = new ArrayList<>();
            for (EdgeInput e : selectedGraph.edges) {
                edges.add(new KruskalEdge(e.from, e.to, e.weight));
            }

            long startTime = System.nanoTime();
            MSTResult result = runKruskal(selectedGraph.nodes, edges, targetId);
            long endTime = System.nanoTime();

            result.executionTimeMs = (endTime - startTime) / 1_000_000;

            System.out.println("\nExecution time: " + result.executionTimeMs + " ms");
            System.out.println("Operation count: " + operationCount);

            // Write to output.json
            try (FileWriter writer = new FileWriter("src/main/resources/output.json")) {
                gson.toJson(result, writer);
                System.out.println("Saved Kruskal MST to output.json");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static MSTResult runKruskal(String[] nodes, List<KruskalEdge> edges, int graphId) {
        List<String> mstEdges = new ArrayList<>();
        int totalWeight = 0;

        UnionFind uf = new UnionFind();
        for (String v : nodes) uf.makeSet(v);

        edges.sort(Comparator.comparingInt(e -> e.weight));
        operationCount += edges.size();

        for (KruskalEdge edge : edges) {
            String root1 = uf.find(edge.from);
            String root2 = uf.find(edge.to);
            if (!root1.equals(root2)) {
                uf.union(edge.from, edge.to);
                mstEdges.add(edge.from + " - " + edge.to + " (weight " + edge.weight + ")");
                totalWeight += edge.weight;
            }
            operationCount++;
        }

        System.out.println("\nKruskal MST edges:");
        for (String s : mstEdges) System.out.println("  " + s);
        System.out.println("Total weight of MST: " + totalWeight);

        return new MSTResult(graphId, mstEdges, totalWeight, 0, operationCount);
    }
}
