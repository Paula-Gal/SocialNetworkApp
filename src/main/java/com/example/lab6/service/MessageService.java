package com.example.lab6.service;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MessageService {
    Repository<Long, MessageDTO> repoMessage;
    Repository<Long, User> repoUser;
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;

    /**
     * Constructor
     *
     * @param repoMessage
     * @param repoUser
     */
    public MessageService(Repository<Long, MessageDTO> repoMessage, Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship) {
        this.repoMessage = repoMessage;
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
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
            long fromAux = from;
            if (user < fromAux) {
                long aux = user;
                user = fromAux;
                fromAux = aux;
            }
            if ((repoFriendship.findOne(new Tuple<>(fromAux, user)) != null)) {
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

            MessageDTO messageDTO = new MessageDTO(from, distinctTos, message, LocalDateTime.now(), null);
            repoMessage.save(messageDTO);
        } else
            throw new ValidationException("The user has no friends!");

        if (existentFriendship.size() != to.size())
            throw new ValidationException("The message was sent only to friends!");
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
}
