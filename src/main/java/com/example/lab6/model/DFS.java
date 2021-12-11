package com.example.lab6.model;

import java.util.Arrays;
import java.util.List;

public abstract class DFS {

    public final Graph graph;
    public final boolean[] visited;


    public DFS(Graph g) {
        this.graph = g;
        this.visited = new boolean[g.size()];
        init();

    }


    /**
     * Initialize boolean[] discovered with false
     */
    private void init() {
        Arrays.fill(visited, false);
    }

    /**
     *
     * @param v
     * find a connected component
     */
    public void DFS1(int v) {
        visited[v] = true;
        for (int u : graph.getEdges(v)) {
            if (!visited[u]) {
                DFS1(u);
            }
        }
    }

    /**
     * calculate the path of a connected component
     * @param s
     * @param previousLength
     * @param path
     */
    public void dfsUtil(int s, int previousLength, List<Integer> path) {
        visited[s] = true;
        path.add(s);
        int currentLength = 0;
            for (int u : graph.getEdges(s)) {
                if (!visited[u]) {
                    currentLength = previousLength + 1;
                    dfsUtil(u, currentLength, path);
                }
            }

        }

    }

