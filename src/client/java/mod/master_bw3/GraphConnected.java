package mod.master_bw3;

import dev.enjarai.trickster.spell.Pattern;

import java.util.*;

public class GraphConnected {
    static class Graph {
        private int vertices;
        private LinkedList<Integer>[] adjList;

        Graph(int vertices) {
            this.vertices = vertices;
            adjList = new LinkedList[vertices];
            for (int i = 0; i < vertices; i++) {
                adjList[i] = new LinkedList<>();
            }
        }

        void addEdge(int src, int dest) {
            adjList[src].add(dest);
            adjList[dest].add(src);
        }

        // Perform DFS to check connectivity
        void dfs(int vertex, boolean[] visited) {
            visited[vertex] = true;
            for (int neighbor : adjList[vertex]) {
                if (!visited[neighbor]) {
                    dfs(neighbor, visited);
                }
            }
        }

        boolean isConnected() {
            boolean[] visited = new boolean[vertices];

            // Find the first vertex with edges to start DFS
            int startVertex = -1;
            for (int i = 0; i < vertices; i++) {
                if (!adjList[i].isEmpty()) {
                    startVertex = i;
                    break;
                }
            }

            if (startVertex == -1) return true;

            dfs(startVertex, visited);

            for (int i = 0; i < vertices; i++) {
                if (!adjList[i].isEmpty() && !visited[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isConnected(List<Pattern.PatternEntry> pattern) {
        Graph graph = new Graph(9);
        for (Pattern.PatternEntry entry : pattern) {
            graph.addEdge(entry.p1(), entry.p2());
        }

        return graph.isConnected();
    }
}
