package com.example.lab6.service;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;
import java.time.LocalDateTime;

public class FriendRequestService {
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    Repository<Tuple<Long, Long>, FriendRequest> friendRequestRepo;
    Repository<Long, User> repoUser;

    /**
     * The constructor
     * @param friendRequestRepo
     */
    public FriendRequestService(Repository<Tuple<Long, Long>, FriendRequest> friendRequestRepo,Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship ) {
        this.friendRequestRepo = friendRequestRepo;
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    /**
     * Send a friend request method
     * @param from - id of the sender user
     * @param to - id of the receiver user
     */
    public void sendFriendRequest(Long from, Long to)
    {
        if(repoUser.findOne(from) == null)
            throw new ValidationException("The user do not exist!");

        if(repoUser.findOne(to) == null)
            throw new ValidationException("The user do not exist!");

        Tuple<Long, Long> ship = new Tuple<>(from, to);
        if(friendRequestRepo.findOne(ship) != null)
            throw new ValidationException("The request already exist!");

        FriendRequest friendRequest = new FriendRequest(from, to, Status.PENDING, LocalDateTime.now());
        friendRequestRepo.save(friendRequest);
    }

    /**
     * Accept a friend request method
     * @param from - id of the sender user
     * @param to - id of the receiver user
     */
    public void acceptFriendRequest(Long from, Long to)
    {
        if(repoUser.findOne(from) == null)
            throw new ValidationException("The user do not exist!");

        if(repoUser.findOne(to) == null)
            throw new ValidationException("The user do not exist!");

        Tuple<Long, Long> ship = new Tuple<>(from, to);
        if(friendRequestRepo.findOne(ship) != null) {
            if (friendRequestRepo.findOne(ship).getStatus() != Status.PENDING)
                throw new ValidationException("The request is not possible");
        }
        FriendRequest friendRequest = new FriendRequest(friendRequestRepo.findOne(ship).getFrom(), friendRequestRepo.findOne(ship).getTo(), friendRequestRepo.findOne(ship).getStatus(), friendRequestRepo.findOne(ship).getLastUpdatedDate());
        friendRequest.setStatus(Status.APPROVED);
        friendRequestRepo.update(friendRequest);
        Friendship friendship = new Friendship(ship);
        repoFriendship.save(friendship);
    }

    /**
     * Reject a friend request method
     * @param from - id of the sender user
     * @param to - id of the receiver user
     */
    public void rejectFriendRequest(Long from, Long to)
    {
        if(repoUser.findOne(from) == null)
            throw new ValidationException("The user do not exist!");

        if(repoUser.findOne(to) == null)
            throw new ValidationException("The user do not exist!");

        Tuple<Long, Long> ship = new Tuple<>(from, to);
        if(friendRequestRepo.findOne(ship) != null) {
            if (friendRequestRepo.findOne(ship).getStatus() != Status.PENDING)
                throw new ValidationException("The request is not possible");
        }
        FriendRequest friendRequest = new FriendRequest(friendRequestRepo.findOne(ship).getFrom(), friendRequestRepo.findOne(ship).getTo(), friendRequestRepo.findOne(ship).getStatus(), friendRequestRepo.findOne(ship).getLastUpdatedDate());
        friendRequest.setStatus(Status.REJECTED);
        friendRequest.setId(ship);
        friendRequestRepo.remove(friendRequest);
    }
}
