package com.example.lab6.service;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.max;

public class UserService {

    Repository<Long, User> repoUser;
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;

    public UserService(Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship =  repoFriendship;
        //setFriendships();
    }

    /**
     *
     * @param entity must be not null
     * @return entity if is saved
     * throw ValidationException if the entity already exits
     */
    public User add(User entity) {
        if(repoUser.findOne(entity.getId()) != null)
            throw new ValidationException("Already exists");
        return repoUser.save(entity);
    }

    /**
     *
     * @param id must be not null
     * @return the entity if is removed
     * throw ValidationException if ID does not exist
     */
    public User remove(Long id) {
        if(repoUser.findOne(id) == null)
            throw new ValidationException("Does not exist!");
        removeallFriendships(repoUser.findOne(id));
        return repoUser.remove(repoUser.findOne(id));
    }

    public void removeallFriendships(User user){
        for(Friendship friendship: repoFriendship.findAll())
        {
            if(friendship.getE1() == user.getId() || friendship.getE2() == user.getId()){
                repoFriendship.remove(friendship);
            }
        }}
    /**
     *
     * @return number of connected components
     */
    public int nrConnectedComponents() {

        int nr = 0;
        // setFriendships();
        Graph graph = new Graph(repoUser);
        graph.createGraph();
        final SocialNetwork socialNetwork = new SocialNetwork(graph);
        nr = socialNetwork.nrCommunities();
        for(User user : repoUser.findAll())
            if(user.getFriendsList().size() == 0)
                nr = nr + 1;
        return nr;
    }

    /**
     *
     * @return List<User> which contains the user of the longest path
     */
    public List<User> sociableCommunity() {
        //setFriendships();
        Graph graph = new Graph(repoUser);
        graph.createGraph();
        final SocialNetwork longestPath = new SocialNetwork(graph);
        List<Integer> path = longestPath.findLongestPath();
        System.out.println(path);
        List<User> community = new ArrayList<User>();
        for(int id: path)
            community.add(repoUser.findOne(Long.parseLong(String.valueOf(id))));
        return community;
    }


    public List<User> getUsers(){
        Iterable<User> list = repoUser.findAll();
        List<User> users = new ArrayList<>();
        list.forEach(users::add);

        return users;

    }
    /**
     * upload the list of friends for every user
     */
    /*public void setFriendships(){
        for(User user: repoUser.findAll()) {
            for (User user1 : repoFriendship.getFriends(user)) {
                user.addFriend(user1);
            }
        }
    }*

     */

    public User update(User user){
        repoUser.update(user);
        return user;
    }

    public User exists(Long id){
        return repoUser.findOne(id);
    }

    public List<User> filter1(Long id, String str){

        Iterable<User> userIterable = repoUser.findAll();
        List<User> usersList = new ArrayList<>();
        userIterable.forEach(usersList::add);

        Predicate<User> firstName = x->x.getFirstName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        Predicate<User> lastName = x->x.getLastName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        Predicate<User> friends = x-> !x.getFriendsList().contains(repoUser.findOne(id));
        Predicate<User> user = x-> !x.getId().equals(id);


        Predicate<User> userPredicate = firstName.or(lastName);

        List<User> users = usersList.stream().filter(userPredicate.and(friends).and(user)).collect(Collectors.toList());
        return users;


    }
}

