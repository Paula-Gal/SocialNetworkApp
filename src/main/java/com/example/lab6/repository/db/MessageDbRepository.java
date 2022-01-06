package com.example.lab6.repository.db;


import com.example.lab6.model.MessageDTO;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.Paginator;
import com.example.lab6.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDbRepository implements PagingRepository<Long, MessageDTO> {
    private String url;
    private String username;
    private String password;

    public MessageDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private List<Long> getAllReceivers(Long id) {
        List<Long> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message_recipients WHERE message = " + id);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSet.getLong(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public MessageDTO findOne(Long aLong) {

        String sql = "SELECT * FROM messages WHERE id = " + aLong;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
               // LocalDate date = resultSet.getDate("date").toLocalDate();
                //LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(resultSet.getTimestamp("date"))).format(DateTimeFormatter.ofPattern(""));
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String messageText = resultSet.getString("message_text");
                Long from = resultSet.getLong("from");
                Long originalMessage = resultSet.getLong("original_message");
                MessageDTO messageDTO = new MessageDTO(from, getAllReceivers(id), messageText, dateTime, originalMessage);
                return messageDTO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<MessageDTO> findAll() {
        Set<MessageDTO> mess = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
               // LocalDate date = resultSet.getDate("date").toLocalDate();
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String messageText = resultSet.getString("message_text");
                Long from = resultSet.getLong("from");
                Long originalMessage = resultSet.getLong("original_message");
                MessageDTO messageDTO = new MessageDTO(from, getAllReceivers(id), messageText, dateTime, originalMessage);
                messageDTO.setId(id);
                mess.add(messageDTO);
            }
            return mess;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public MessageDTO save(MessageDTO entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "INSERT INTO messages (date, message_text, \"from\", \"original_message\") VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime dateTime = LocalDateTime.now();
            ps.setTimestamp(1, Timestamp.valueOf(dateTime));
            ps.setString(2, entity.getMessage());

            ps.setInt(3, Math.toIntExact(entity.getFrom()));
            if (entity.getReply() != null)
                ps.setInt(4, Math.toIntExact(entity.getReply()));
            else {
                ps.setNull(4, Types.NULL);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                saveMessagesToUsers(id, entity.getTo());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveMessagesToUsers(Long id, List<Long> users) {
        String sql = "INSERT INTO message_recipients (message, recipient) VALUES (?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            users.forEach(x -> {
                try {
                    ps.setInt(1, Math.toIntExact(id));
                    ps.setInt(2, Math.toIntExact(x));
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
    public MessageDTO remove(MessageDTO entity) {
        return null;
    }

    @Override
    public MessageDTO update(MessageDTO entity) {
        return null;
    }


    @Override
    public Page<MessageDTO> findAll(Pageable pageable) {
        Paginator<MessageDTO> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
