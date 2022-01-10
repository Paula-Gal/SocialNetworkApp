package com.example.lab6.service;

import com.example.lab6.model.Friendship;
import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.Tuple;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.utils.events.UserChangeEvent;
import com.example.lab6.utils.observer.Observable;
import com.example.lab6.utils.observer.Observer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class UserService implements Observable<UserChangeEvent> {

   // UserRepository<Long, User> repoUser;
    UserRepository<Long, User> repoUser;
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    UserValidator userValidator;

    public UserService(UserRepository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship, UserValidator userValidator) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.userValidator = userValidator;
        //setFriendships();
    }

    /**
     * @param entity must be not null
     * @return entity if is saved
     * throw ValidationException if the entity already exits
     */
    public User add(User entity) {
        try {
            userValidator.validateEmail(entity.getEmail());
            if (repoUser.findOneByEmail(entity.getEmail()) != null)
                throw new ValidationException("Email already exists");
            return repoUser.save(entity);
        } catch (ValidationException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    /**
     * @param id must be not null
     * @return the entity if is removed
     * throw ValidationException if ID does not exist
     */
    public User remove(Long id) {
        if (repoUser.findOne(id) == null)
            throw new ValidationException("Does not exist!");
        removeallFriendships(repoUser.findOne(id));
        return repoUser.remove(repoUser.findOne(id));
    }

    public void removeallFriendships(User user) {
        for (Friendship friendship : repoFriendship.findAll()) {
            if (friendship.getE1() == user.getId() || friendship.getE2() == user.getId()) {
                repoFriendship.remove(friendship);
            }
        }
    }

    public List<User> getUsers() {
        Iterable<User> list = repoUser.findAll();
        List<User> users = new ArrayList<>();
        list.forEach(users::add);

        return users;

    }


    public User update(User user) {
        repoUser.update(user);
        return user;
    }

    public User exists(String email) {
        return repoUser.findOneByEmail(email);
    }

    public List<User> filter1(Long id, String str) {

        Iterable<User> userIterable = repoUser.findAll();
        List<User> usersList = new ArrayList<>();
        userIterable.forEach(usersList::add);

        Predicate<User> firstName = x -> x.getFirstName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        Predicate<User> lastName = x -> x.getLastName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        Predicate<User> friends = x -> !x.getFriendsList().contains(repoUser.findOne(id));
        Predicate<User> user = x -> !x.getId().equals(id);


        Predicate<User> userPredicate = firstName.or(lastName);

        List<User> users = usersList.stream().filter(userPredicate.and(friends).and(user)).collect(Collectors.toList());
        return users;

        //notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, ));

    }

    public List<User> friends(Long id, String str) {

        Iterable<Friendship> friendships = repoFriendship.findAll();
        List<Friendship> friendshipsList = new ArrayList<>();
        friendships.forEach(friendshipsList::add);

        Predicate<Friendship> id1 = x->x.getE1().equals(id);
        Predicate<Friendship> id2 = x->x.getE2().equals(id);
        Predicate<Friendship> friendshipPredicate = id1.or(id2);

        List<Friendship> friends = friendshipsList.stream().filter(friendshipPredicate).collect(Collectors.toList());


        List<User> usersList = new ArrayList<>();
        friends.forEach(x->{
            if(x.getE1().equals(id))
                usersList.add(repoUser.findOne(x.getE2()));
            else
                usersList.add(repoUser.findOne(x.getE1()));
        });

        Predicate<User> firstName = x -> x.getFirstName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        Predicate<User> lastName = x -> x.getLastName().toLowerCase(Locale.ROOT).contains(str.toLowerCase(Locale.ROOT));
        //Predicate<User> friends = x -> x.getFriendsList().contains(repoUser.findOne(id));

        Predicate<User> userPredicate = firstName.or(lastName);

        List<User> users = usersList.stream().filter(userPredicate).collect(Collectors.toList());
        return users;

        //notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, ));

    }

    public List<User> searchingFriends(int leftLimit, int rightLimit, Long id, String string){
        List<User> users = friends(id, string);
        return users.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

    private List<Observer<UserChangeEvent>> observers = new ArrayList<>();


    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {

    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }

    public static String getSecurePassword(String password) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public boolean checkPassword(String inputPassword, String email) {

        String hashedInputPassword = getSecurePassword(inputPassword);

        return (hashedInputPassword).equals(exists(email).getPassword());
    }

    public void savePicture(String email, String url) {
        repoUser.savePicture(email, url);
    }

    public void updatePicture(String email, String url) {
        repoUser.updatePicture(email, url);
    }

    public String findPhoto(String email) {
        return repoUser.findPhoto(email);
    }

    public List<FriendshipDTO> getFriendshipsByDate(LocalDateTime startDate, LocalDateTime endDate, Long loggedUser) {
        Iterable<Friendship> friendships = repoFriendship.findAll();
        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();

        friendships.forEach(x -> {
            if (x.getE1().equals(loggedUser)) {
                FriendshipDTO friendshipDTO = new FriendshipDTO(repoUser.findOne(x.getE2()), x.getDate());
                friendshipDTOS.add(friendshipDTO);
            } else if(x.getE2().equals(loggedUser)) {
                FriendshipDTO friendshipDTO = new FriendshipDTO(repoUser.findOne(x.getE1()), x.getDate());
                friendshipDTOS.add(friendshipDTO);
            }
        });

        Predicate<FriendshipDTO> isAfter = x -> x.getDate().isAfter(startDate);
        Predicate<FriendshipDTO> isBefore = x -> x.getDate().isBefore(endDate);
        Predicate<FriendshipDTO> isEqual = x -> x.getDate().isEqual(endDate);
        Predicate<FriendshipDTO> isFinal = isAfter.and(isBefore).or(isEqual);

        List<FriendshipDTO> friends = friendshipDTOS.stream().filter(isFinal).collect(Collectors.toList());

        return friends;
    }

    public User getUserByID(Long userID) {
        return repoUser.findOne(userID);
    }


    public List<User> getSearchOnPage(int leftLimit,int rightLimit, Long id, String string) {
        List<User> users = filter1(id, string);
        return users.stream().skip(leftLimit)
                .limit(rightLimit)
                .collect(Collectors.toList());
    }

}

