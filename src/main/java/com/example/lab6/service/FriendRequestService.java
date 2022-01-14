package com.example.lab6.service;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.utils.events.ChangeEventType;
import com.example.lab6.utils.events.FriendRequestChangeEvent;
import com.example.lab6.utils.observer.Observable;
import com.example.lab6.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendRequestService implements Observable<FriendRequestChangeEvent> {
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    Repository<Tuple<Long, Long>, FriendRequest> friendRequestRepo;
    UserRepository<Long, User> repoUser;

    /**
     * The constructor
     * @param friendRequestRepo
     */
    public FriendRequestService(Repository<Tuple<Long, Long>, FriendRequest> friendRequestRepo,UserRepository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship ) {
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
        if(friendRequestRepo.findOne(ship) != null){

       // FriendRequest friendRequest = new FriendRequest(friendRequestRepo.findOne(ship).getFrom(), friendRequestRepo.findOne(ship).getTo(), friendRequestRepo.findOne(ship).getStatus(), friendRequestRepo.findOne(ship).getLastUpdatedDate());
            FriendRequest friendRequest = friendRequestRepo.findOne(ship);

        FriendRequest old = friendRequest;
        friendRequest.setStatus(Status.APPROVED);
        friendRequestRepo.remove(friendRequest);
        //friendRequestRepo.update(friendRequest);
        Friendship friendship = new Friendship(ship);
        repoFriendship.save(friendship);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.ADD, friendRequest));}
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
        if(friendRequestRepo.findOne(ship) != null){
        FriendRequest friendRequest = new FriendRequest(friendRequestRepo.findOne(ship).getFrom(), friendRequestRepo.findOne(ship).getTo(), friendRequestRepo.findOne(ship).getStatus(), friendRequestRepo.findOne(ship).getLastUpdatedDate());
        friendRequest.setStatus(Status.REJECTED);
        friendRequest.setId(ship);
        friendRequestRepo.remove(friendRequest);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.ADD, friendRequest));}
    }

    /**
     * remove a friend request method
     * @param from - id of the sender user
     * @param to - id of the receiver user
     */
    public void deleteFriendRequest(Long from, Long to)
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

        if(friendRequestRepo.findOne(ship) != null)
        { FriendRequest  friendRequest= new FriendRequest(friendRequestRepo.findOne(ship).getFrom(), friendRequestRepo.findOne(ship).getTo(), friendRequestRepo.findOne(ship).getStatus(), friendRequestRepo.findOne(ship).getLastUpdatedDate());
        friendRequest.setId(ship);
        friendRequestRepo.remove(friendRequest);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.ADD, friendRequest));}
    }

    /**
     * return all the friend request of a user
     * @param id
     * @return
     */
    public List<FriendRequestDTO> getFriendRequest(Long id){
        if(repoUser.findOne(id) == null)
            throw new ValidationException("Invalid id");

        List<FriendRequestDTO> requestlist = new ArrayList<>();
        Iterable<FriendRequest> requestIterable = friendRequestRepo.findAll();

       // Predicate<FriendRequest> from = x->x.getFrom().equals(id);
        Predicate<FriendRequest> to = x->x.getTo().equals(id);
        Predicate<FriendRequest> status = x->x.getStatus().equals(Status.PENDING);
        Predicate<FriendRequest> requestPredicate = status.and(to);
        List<FriendRequest> list = new ArrayList<>();
        requestIterable.forEach(list::add);
        list.stream().filter(requestPredicate).map(x ->{
            return new FriendRequestDTO(repoUser.findOne(x.getFrom()), repoUser.findOne(x.getTo()), x.getStatus(), x.getLastUpdatedDate());

        }).forEach(requestlist::add);

        return requestlist;
    }

    /**
     * return all the friend request of a user
     * @param id
     * @return
     */
    public List<FriendRequestDTO> getMyFriendsRequestes(Long id){
        if(repoUser.findOne(id) == null)
            throw new ValidationException("Invalid id");

        List<FriendRequestDTO> requestlist = new ArrayList<>();
        Iterable<FriendRequest> requestIterable = friendRequestRepo.findAll();

        Predicate<FriendRequest> from = x->x.getFrom().equals(id);
        Predicate<FriendRequest> status = x->x.getStatus().equals(Status.PENDING);
        //Predicate<FriendRequest> to = x->x.getTo().equals(id);
        Predicate<FriendRequest> requestPredicate = from.and(status);
        List<FriendRequest> list = new ArrayList<>();
        requestIterable.forEach(list::add);
        list.stream().filter(requestPredicate).map(x ->{
            return new FriendRequestDTO(repoUser.findOne(x.getFrom()), repoUser.findOne(x.getTo()), x.getStatus(), x.getLastUpdatedDate());

        }).forEach(requestlist::add);

        return requestlist;
    }

    public List<FriendRequestDTO> getFriendRequestsOnPage(int leftLimit,int rightLimit, Long id) {
        List<FriendRequestDTO> friendslist = getFriendRequest(id);
        return friendslist.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

    public List<FriendRequestDTO> getFriendRequestsByMeOnPage(int leftLimit,int rightLimit, Long id) {
        List<FriendRequestDTO> friendslist = getMyFriendsRequestes(id);
        return friendslist.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

    public FriendRequest existsFriendRequests(Long id1, Long id2){
        Tuple<Long, Long> ship = new Tuple<>(id1, id2);
        if(friendRequestRepo.findOne(ship) == null)
            return null;
        else {
            if(friendRequestRepo.findOne(ship).getStatus().equals(Status.PENDING))
                return friendRequestRepo.findOne(ship);
            else
                return null;
            }
    }

    private List<Observer> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<FriendRequestChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendRequestChangeEvent> e) {

    }

    @Override
    public void notifyObservers(FriendRequestChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
