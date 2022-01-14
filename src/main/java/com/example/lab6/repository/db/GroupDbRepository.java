package com.example.lab6.repository.db;

import com.example.lab6.model.Group;
import com.example.lab6.model.MessageDTO;
import com.example.lab6.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupDbRepository implements Repository<Long, Group> {
    private String url;
    private String username;
    private String password;

    public GroupDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Group findOne(Long aLong) {

        String sql = "SELECT * FROM groups WHERE id = " + aLong;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Group group = new Group(name, new ArrayList<>());
                group.setId(id);
                String sql1 = "select * from groups_users where \"user\" = " + id;
                List<Long> users = new ArrayList<>();
                try (Connection connection1 = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement1 = connection1.prepareStatement(sql1);
                     ResultSet resultSet1 = statement1.executeQuery()) {

                    while(resultSet1.next()){
                        Long idUser = resultSet1.getLong("user");
                        users.add(idUser);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
                group.setMembers(users);
                return saveMessagesToUsers(group);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Group saveMessagesToUsers(Group group) {
        String sql = "select * from messages_groups where recipient_group = " + group.getId();
        List<MessageDTO> messageDTOList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long from = resultSet.getLong("from");
                String message = resultSet.getString("message");
                Long original_message = resultSet.getLong("original_message");
                List<Long> recipients = new ArrayList<>();
                group.getMembers().forEach(x->{
                    if(!x.equals(from))
                        recipients.add(x);
                });
                MessageDTO messageDTO = new MessageDTO(from, recipients, message, date, null);
                messageDTO.setId(id);
                messageDTOList.add(messageDTO);
            }
            group.setMessages(messageDTOList);
            return group;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Group> findAll() {
        Set<Group> groups = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from \"groups\"");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Group group = new Group(name, new ArrayList<>());
                group.setId(id);
                String sql1 = "select * from groups_users where \"group\" = " + id;
                List<Long> users = new ArrayList<>();
                try (Connection connection1 = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement1 = connection1.prepareStatement(sql1);
                     ResultSet resultSet1 = statement1.executeQuery()) {

                    while(resultSet1.next()){
                        Long idUser = resultSet1.getLong("user");
                        users.add(idUser);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }

                group.setMembers(users);
                groups.add(saveMessagesToUsers(group));
            }
            return  groups;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Group save(Group entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "insert into groups (\"name\") values (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                saveUsersToGroup(id,  entity.getMembers());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveMessages(MessageDTO messages, Long idGroup) {

        String sql = "INSERT INTO messages_groups (date, \"from\", message, \"original_message\", recipient_group) VALUES (?, ? , ? ,? , ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(messages.getDate()));
            ps.setString(3, messages.getMessage());
            ps.setLong(2, messages.getFrom());
            if (messages.getReply() != null)
                ps.setInt(4, Math.toIntExact(messages.getReply()));
            else {
                ps.setNull(4, Types.NULL);
            }
            ps.setLong(5, idGroup);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void saveUsersToGroup(Long id, List<Long> users) {
        String sql = "INSERT INTO groups_users (\"user\", \"group\") VALUES (?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            users.forEach(x -> {
                try {
                    ps.setInt(1, Math.toIntExact(x));
                    ps.setInt(2, Math.toIntExact(id));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Group remove(Group entity) {
        return null;
    }

    @Override
    public Group update(Group entity) {
        saveMessages(entity.getMessages().get(entity.getMessages().size()-1), entity.getId());
        return null;
    }
}
