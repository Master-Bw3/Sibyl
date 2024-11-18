package mod.master_bw3;
import dev.enjarai.trickster.spell.Pattern;

import java.util.*;

public class GraphExtension {

    // Graph class to represent an undirected graph
    public static class Graph {
        Set<Integer> vertices;
        Set<Edge> edges;

        public Graph() {
            vertices = new HashSet<>();
            edges = new HashSet<>();

            // Initialize vertices 0 through 8
            for (int i = 0; i <= 8; i++) {
                vertices.add(i);
            }
        }

        public Graph(List<Pattern.PatternEntry> edges) {
            this();

            for (Pattern.PatternEntry edge : edges) {
                addEdge(edge.p1(), edge.p2());
            }
        }

        public void addEdge(int u, int v) {
            edges.add(new Edge(u, v));
        }

        public Set<Integer> getVertices() {
            return vertices;
        }

        public Set<Edge> getEdges() {
            return edges;
        }
    }

    // Edge class to represent an edge in the graph
    static class Edge {
        int u, v;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Edge edge = (Edge) obj;
            return (u == edge.u && v == edge.v) || (u == edge.v && v == edge.u);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.min(u, v), Math.max(u, v));
        }
    }

    // Main function to check if G1 can be extended to match G2 from a specific point
    public static boolean canExtendPathFrom(Graph G1, Graph G2, int startVertex) {
        Set<Integer> vertices1 = G1.getVertices();
        Set<Integer> vertices2 = G2.getVertices();
        Set<Edge> edges1 = G1.getEdges();
        Set<Edge> edges2 = G2.getEdges();

        // Check basic conditions
        if (!vertices1.equals(vertices2) || !edges2.containsAll(edges1)) {
            //System.out.println("Basic check failed.");
            return false;
        }

        // Simulate path extension starting from the given startVertex
        Set<Edge> extendedEdges = new HashSet<>(edges1);
        Set<Integer> visited = new HashSet<>();
        visited.add(startVertex);

        return extendPathFromVertex(G1, G2, extendedEdges, visited, startVertex, edges2);
    }

    // Recursive function to extend path from a specific vertex
    private static boolean extendPathFromVertex(Graph G1, Graph G2, Set<Edge> extendedEdges,
                                                Set<Integer> visited, int currentVertex, Set<Edge> edges2) {
        // Debug: Show current state
        //System.out.println("Extending from vertex " + currentVertex);
        //System.out.println("Current path: " + extendedEdges);

        // Check if the current path matches G2
        if (extendedEdges.equals(edges2)) {
            //System.out.println("Path matches G2!");
            return true;
        }

        // Try to extend the path by adding edges from G2 that connect to the current vertex
        for (Edge edge : edges2) {
            if (edge.u == currentVertex || edge.v == currentVertex) {
                // Add edge if it's not already part of the path
                if (!extendedEdges.contains(edge)) {
                    int nextVertex = (edge.u == currentVertex) ? edge.v : edge.u;
                    if (!visited.contains(nextVertex)) {
                        extendedEdges.add(edge);
                        visited.add(nextVertex);  // Mark the vertex as visited
                        // Recursively try to extend the path
                        if (extendPathFromVertex(G1, G2, extendedEdges, visited, nextVertex, edges2)) {
                            return true;
                        }
                        // Backtrack if path extension doesn't work
                        extendedEdges.remove(edge);
                        visited.remove(nextVertex);  // Unmark the vertex for backtracking
                    }
                }
            }
        }

        // If no valid extension is found, return false
        //System.out.println("No valid extension from vertex " + currentVertex);
        return false;
    }
}
