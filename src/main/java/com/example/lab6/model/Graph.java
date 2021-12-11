package com.example.lab6.model;

import com.example.lab6.model.User;

import com.example.lab6.repository.Repository;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {

    Repository<Long, User> repoUser;
    //Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    public int N;
    public List<Integer>[] graph;

    public Graph(Repository<Long, User> repoUser) {
        this.repoUser = repoUser;

    }



    public void createGraph(){
        final Long[] maxim = {Long.valueOf(0)};
        repoUser.findAll().forEach(x -> {
            if (x.getId() > maxim[0])
                maxim[0] = x.getId();
        });
        this.N = Math.toIntExact(maxim[0] + 1);
        graph = new List[N];
        for(int i = 1; i < N; ++i) {
            graph[i] = new LinkedList<Integer>();
        }
        for (User user : repoUser.findAll()) {
            for (User user1 : user.getFriendsList()) {
                graph[Math.toIntExact(user.getId())].add(Math.toIntExact(user1.getId()));
            }
        }
    }


    public int size() {
        return this.N;
    }


    public List<Integer> getEdges(int s) {
        List<Integer> list = new ArrayList<>();
        if(graph[s] == null)
            return null;
        for(int i: graph[s])
            list.add(i);
        return list;
    }


    public List<Integer> getVertices(){
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < size(); i ++){
            if(graph[i] != null)
                if(!graph[i].isEmpty())
                    list.add(i);

        }
        System.out.println();
        return list;
    }
}
