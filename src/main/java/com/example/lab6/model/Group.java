package com.example.lab6.model;

import java.util.List;

public class Group extends Entity<Long>{
    public String name;
    public List<Long> members;
    public List<MessageDTO> messages;

    public Group() {

    }

    public List<MessageDTO> getMessages() {
        return this.messages;
    }
    public void addMessage(MessageDTO messageDTO){
        messages.add(messageDTO);
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public Group(String name, List<MessageDTO> messagess) {
        this.name = name;
        this.messages = messagess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
    public void addMember(Long user){
        this.members.add(user);
    }
}
