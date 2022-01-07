package com.example.lab6.service;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.PageableImplementation;
import com.example.lab6.repository.paging.PagingRepository;
import com.example.lab6.utils.events.ChangeEventType;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observable;
import com.example.lab6.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessageService implements Observable<MessageChangeEvent> {
    PagingRepository<Long, MessageDTO> repoMessage;
    UserRepository<Long, User> repoUser;
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    Repository<Long, Group> repoGroup;

    /**
     * Constructor
     *
     * @param repoMessage
     * @param repoUser
     */
    public MessageService(PagingRepository<Long, MessageDTO> repoMessage, UserRepository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship, Repository<Long, Group> repoGroup) {
        this.repoMessage = repoMessage;
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.repoGroup = repoGroup;
    }

    /**
     * send a message to a user
     *
     * @param from
     * @param to
     * @param message
     */
    public void sendMessage(Long from, List<Long> to, String message) {
        if (repoUser.findOne(from) == null)
            throw new ValidationException("The user doesn't exist!");

        List<Long> existentFriendship = new ArrayList<>();
        for (Long user : to) {

            if ((repoFriendship.findOne(new Tuple<>(from, user)) != null)) {
                existentFriendship.add(user);
            }
        }

        if (existentFriendship.size() > 0) {
            List<Long> distinctTos = existentFriendship.stream().distinct().collect(Collectors.toList());
            distinctTos.forEach(
                    x -> {
                        if (repoUser.findOne(x) == null)
                            throw new ValidationException("The user doesn't exist!");
                    }
            );

            if (message.length() == 0)
                throw new ValidationException("The message is empty!");

            MessageDTO messageDTO = new MessageDTO(from, to, message, LocalDateTime.now(), null);
            repoMessage.save(messageDTO);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, messageDTO));
        } else
            throw new ValidationException("The user has no friends!");

        if (existentFriendship.size() != to.size())
            throw new ValidationException("The message was sent only to friends!");
    }


    public void sendMessageGroup(Group group,  MessageDTO messageDTO)
    {

        if(repoGroup.findOne(group.getId())!= null)
        {
            group.addMessage(messageDTO);
            repoGroup.update(group);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, messageDTO));
        }
        else{
            repoGroup.save(group);
            group.addMessage(messageDTO);
            repoGroup.update(group);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, messageDTO));
        }


    }

    /**
     * reply to a message
     *
     * @param toMessage
     * @param fromUser
     * @param message
     */
    public void replyToOne(Long toMessage, Long fromUser, String message) {
        if (repoMessage.findOne(toMessage) == null)
            throw new ValidationException("The message do not exist!");
        if (repoUser.findOne(fromUser) == null)
            throw new ValidationException("The user do not exist!");
        if (message.length() == 0)
            throw new ValidationException("The message is empty!");

        List<Long> toUsersList = new ArrayList<>();
        List<Long> recipients = new ArrayList<>();
        //toUsers.add(fromUser);
        repoMessage.findOne(toMessage).getTo().forEach(x -> {
            User user = new User(repoUser.findOne(x).getFirstName(), repoUser.findOne(x).getLastName());
            user.setId(repoUser.findOne(x).getId());
            recipients.add(user.getId());
        });
        if (recipients.contains(fromUser)) {
            toUsersList.add(repoMessage.findOne(toMessage).getFrom());
            MessageDTO messageDTO = new MessageDTO(fromUser, toUsersList, message, LocalDateTime.now(), toMessage);
            repoMessage.save(messageDTO);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, messageDTO));
        } else
            throw new ValidationException("The user did not receive the message!");

    }

    /**
     * @param toMessage - id-ul mesajului la care se raspunde
     * @param fromUser  - user-ul care da replyAll
     * @param message   - mesajul pe care il trimite
     */
    public void replyToAll(Long toMessage, Long fromUser, String message) {
        if (repoMessage.findOne(toMessage) == null)
            throw new ValidationException("The message do not exist!");
        if (repoUser.findOne(fromUser) == null)
            throw new ValidationException("The user do not exist!");
        if (message.length() == 0)
            throw new ValidationException("The message is empty!");

        List<Long> recipients = new ArrayList<>();

        repoMessage.findOne(toMessage).getTo().forEach(x -> {
            if (!fromUser.equals(x)) {
                User user = new User(repoUser.findOne(x).getFirstName(), repoUser.findOne(x).getLastName());
                user.setId(repoUser.findOne(x).getId());
                recipients.add(user.getId());
            }
        });

        recipients.add(repoMessage.findOne(toMessage).getFrom());

        MessageDTO messageDTO = new MessageDTO(fromUser, recipients, message, LocalDateTime.now(), toMessage);
        repoMessage.save(messageDTO);

    }

    /**
     * get the conversation of two users
     *
     * @param id1
     * @param id2
     * @return
     */
    public List<Message> getConversation(Long id1, Long id2) {
        Iterable<MessageDTO> messages = this.repoMessage.findAll();
        List<MessageDTO> result = new ArrayList<>();
        for (MessageDTO mess : messages) {
            if ((mess.getFrom().equals(id1) && mess.getTo().contains(id2)) || (mess.getFrom().equals(id2) && mess.getTo().contains(id1)))
                result.add(mess);
        }
        result.sort(Comparator.comparing(MessageDTO::getDate));
        return convertMessages(result);
    }

    /**
     * transform a list of MessageDTOs in a list of Messages
     *
     * @param list
     * @return
     */
    public List<Message> convertMessages(List<MessageDTO> list) {
        List<Message> result = new ArrayList<>();
        list.forEach(x -> {
            List<User> toUsers = new ArrayList<>();
            x.getTo().forEach(y -> {
                toUsers.add(repoUser.findOne(y));
            });
            if (repoMessage.findOne(x.getReply()) == null) {
                Message message = new Message(x.getId(), repoUser.findOne(x.getFrom()), toUsers, x.getMessage(), x.getDate());
                result.add(message);
            } else {
                Message message = new Message(x.getId(), repoUser.findOne(x.getFrom()), toUsers, x.getMessage(), x.getDate(), result.get(result.size() - 1));
                result.add(message);
            }
        });
        return result;
    }

    public List<MessageDTO> getMessagesByDate(LocalDateTime startDate, LocalDateTime endDate, Long loggedUser) {
        Iterable<MessageDTO> messages = repoMessage.findAll();
        List<MessageDTO> messageDTOS = new ArrayList<>();
        List<Long> tos = new ArrayList<>();
        tos.add(loggedUser);

        messages.forEach(x -> {
            Long to = x.getTo().get(0);
            if (to.equals(loggedUser)) {
                MessageDTO message = new MessageDTO(x.getFrom(), tos, x.getMessage(), x.getDate(), x.getReply());
                messageDTOS.add(message);
            }
        });

        Predicate<MessageDTO> isAfter = x -> x.getDate().isAfter(startDate);
        Predicate<MessageDTO> isBefore = x -> x.getDate().isBefore(endDate);
        Predicate<MessageDTO> isEqual = x -> x.getDate().isEqual(endDate);
        Predicate<MessageDTO> isFinal = isAfter.and(isBefore).or(isEqual);

        List<MessageDTO> messageDTOS1 = messageDTOS.stream().filter(isFinal).collect(Collectors.toList());

        return messageDTOS1;
    }

    public List<MessageDTO> getMessagesFromAFriend(LocalDateTime startDate, LocalDateTime endDate, Long loggedUser, Long fromUser) {
        Iterable<MessageDTO> messages = repoMessage.findAll();
        List<MessageDTO> messageDTOS = new ArrayList<>();
        List<Long> tos = new ArrayList<>();
        tos.add(loggedUser);

        messages.forEach(x -> {
            Long to = x.getTo().get(0);
            Long from  = x.getFrom();
            if (to.equals(loggedUser) && from.equals(fromUser)) {
                MessageDTO message = new MessageDTO(x.getFrom(), tos, x.getMessage(), x.getDate(), x.getReply());
                messageDTOS.add(message);
            }
        });

        Predicate<MessageDTO> isAfter = x -> x.getDate().isAfter(startDate);
        Predicate<MessageDTO> isBefore = x -> x.getDate().isBefore(endDate);
        Predicate<MessageDTO> isEqual = x -> x.getDate().isEqual(endDate);
        Predicate<MessageDTO> isFinal = isAfter.and(isBefore).or(isEqual);

        List<MessageDTO> messageDTOS1 = messageDTOS.stream().filter(isFinal).collect(Collectors.toList());

        return messageDTOS1;
    }

    /**
     * print a conversation
     *
     * @param conv
     */
    public void listConversation(List<Message> conv) {
        conv.forEach(x -> {
            System.out.println(x.getFrom().getFirstName() + " " + x.getFrom().getLastName() + " sent " + x.getMessage());
        });
    }

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public List<Message> getConversationGroup(Long from, List<Long> groupConversation) {
        Iterable<MessageDTO> messages = this.repoMessage.findAll();
        List<MessageDTO> result = new ArrayList<>();
        for (MessageDTO mess : messages) {
            if ((mess.getFrom().equals(from) && mess.getTo().containsAll(groupConversation)) || (groupConversation.contains(mess.getFrom()) && mess.getTo().contains(from)))
                result.add(mess);
        }
        result.sort(Comparator.comparing(MessageDTO::getDate));

        return convertMessages(result);
    }

    public void saveGroup(Group group){
        repoGroup.save(group);
    }

    public List<Group> getGroups(){
        Iterable<Group> groupIterable = repoGroup.findAll();
        List<Group> groups = new ArrayList<>();
        groupIterable.forEach(groups::add);
       return groups;
    }


    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
    }

    private Pageable pageable;
    private int size = 1;
    private int page = 0;

    public void setPageSize(int size) {
        this.size = size;
    }

    //    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }
    public Set<MessageDTO> getNextMessages() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getMessagesOnPage(this.page);
    }

    public Set<MessageDTO> getMessagesOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<MessageDTO> messagePage = repoMessage.findAll(pageable);
        return messagePage.getContent().collect(Collectors.toSet());
    }

}

