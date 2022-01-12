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
        List<Post> myEvents = new ArrayList<>();
        subscribedEvents.forEach(myEvents::add);
        Predicate<Post> event = x -> x.getAdmin().equals(id);
        myEvents = myEvents.stream().filter(event).collect(Collectors.toList());

        return myEvents;
    }

    public List<Post> getMyPostsOnPage(int leftLimit, int rightLimit, Long id) {
        List<Post> postList= getMyPosts(id);
        return postList.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }


    public List<Post> getPostsOnPage(int leftLimit, int rightLimit) {
        List<Post> postList= getAllPosts();
        return postList.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

}
