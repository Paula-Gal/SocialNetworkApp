package com.example.lab6.model;

import java.util.*;


public class SocialNetwork extends DFS{

    public SocialNetwork(Graph g){
        super(g);
    }

    /**
     *
     * @return a List<Integer> with of User id that form the longest path
     */
    public List<Integer> findLongestPath(){
        List<Integer> path = new ArrayList<Integer>();
        List<Integer> path1 = new ArrayList<Integer>();
        int length = 0, maxLength= -1;

        for(int u: graph.getVertices())
            if (!visited[u]) {
                path.clear();
                dfsUtil(u, 0, path);
                if (length > maxLength) {
                    maxLength = length;
                    path1 = path;
                }
            }
        return path1;
    }

    /**
     *
     * @return number of connected components
     */
    public int nrCommunities(){
        int nr = 0;
        for(int u: graph.getVertices())
            if (!visited[u]) {
                DFS1(u);
                nr = nr + 1;
            }
        return nr;
    }

}