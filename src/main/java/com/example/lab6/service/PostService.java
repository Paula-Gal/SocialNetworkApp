package com.example.lab6.service;

import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.Post;
import com.example.lab6.model.User;

import com.example.lab6.model.validators.PostValidator;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.paging.PagingRepository;

import com.example.lab6.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostService {
    PagingRepository<Long, Post> repoPost;
    UserRepository<Long, User> repoUser;
    PostValidator postValidator;

    public PostService(PagingRepository<Long, Post> repoEvent, UserRepository<Long, User> repoUser, PostValidator postValidator) {
        this.repoPost = repoEvent;
        this.repoUser = repoUser;
        this.postValidator = postValidator;
    }

    public void addPost(Post post) {
        try {
            postValidator.validate(post);
            repoPost.save(post);
        } catch (ValidationException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    public void deletePost(Post post){
        if(repoPost.findOne(post.getId()) == null)
            throw  new ValidationException("The post do not exists");
        else
            repoPost.remove(post);
    }


    public List<Post> getAllPosts() {
        Iterable<Post> list = repoPost.findAll();
        List<Post> events = new ArrayList<>();
        list.forEach(events::add);

        return events;
    }

    public List<Post> getMyPosts(Long id) {
        Iterable<Post> subscribedEvents = repoPost.findAll();

        List<Post> myPosts = new ArrayList<>();
        subscribedEvents.forEach(myPosts::add);
        myPosts.sort(Comparator.comparing(Post::getDate).reversed());
        Predicate<Post> event = x -> x.getAdmin().equals(id);
        myPosts = myPosts.stream().filter(event).collect(Collectors.toList());

        return myPosts;
    }

    public List<Post> getMyPostsOnPage(int leftLimit, int rightLimit, Long id) {
        List<Post> postList= getMyPosts(id);

        return postList.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }


    public List<Post> getFriendsPosts(List<FriendshipDTO> users) {
        Iterable<Post> iterable = repoPost.findAll();
        List<Post> list = new ArrayList<>();
        iterable.forEach(list::add);
        list.sort(Comparator.comparing(Post::getDate).reversed());

        List<Long> ids = new ArrayList<>();
        users.forEach(x->{
            ids.add(x.getUser().getId());
        });

        Predicate<Post> friend = x->ids.contains(x.getAdmin());
        List<Post> posts = new ArrayList<>();
        posts = list.stream().filter(friend).collect(Collectors.toList());


        return posts;
    }
    public List<Post> getHomePostsOnPage(int leftLimit, int rightLimit, List<FriendshipDTO> users) {
        List<Post> postList= getFriendsPosts(users);
        return postList.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

    public List<Post> getFriendPosts(Long id) {
        Iterable<Post> subscribedEvents = repoPost.findAll();
        List<Post> myPosts = new ArrayList<>();
        subscribedEvents.forEach(myPosts::add);
        Predicate<Post> event = x -> x.getAdmin().equals(id);
        myPosts = myPosts.stream().filter(event).collect(Collectors.toList());

        return myPosts;
    }

    public List<Post> getFriendPostsOnPage(int leftLimit, int rightLimit, Long id) {
        List<Post> postList= getFriendPosts(id);
        return postList.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

}
