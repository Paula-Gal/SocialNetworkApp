package com.example.lab6.repository.db;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.Validator;

import com.example.lab6.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UtilizatorDbRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;


    public UtilizatorDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;


    }


    @Override
    public User findOne(Long aLong) {

        String sql = "SELECT * from users where id = " + String.valueOf(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                // if(id == aLong) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    User utilizator = new User(firstName, lastName);
                    utilizator.setId(id);
                    return utilizator;
               // }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User utilizator = new User(firstName, lastName);
                utilizator.setId(id);
                String sql = "select * from friendships f where f.first_friend = " + String.valueOf(utilizator.getId())+ " or f.second_friend = " + String.valueOf(utilizator.getId());
                PreparedStatement statement1 = connection.prepareStatement(sql);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    Long first = resultSet1.getLong("first_friend");
                    Long second = resultSet1.getLong("second_friend");
                    if(first == utilizator.getId())
                        utilizator.addFriend(findOne(second));
                    if(second == utilizator.getId())
                        utilizator.addFriend(findOne(first));
                    }
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "insert into users (first_name, last_name ) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User remove(User entity) {

        String sql = "delete from users where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                if (id == entity.getId()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    User utilizator = new User(firstName, lastName);
                    utilizator.setId(id);

                    PreparedStatement statement1 = connection.prepareStatement(sql);
                    statement1.setInt(1, Integer.parseInt(String.valueOf(id)));
                    statement1.executeUpdate();
                    return utilizator;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }



    @Override
    public User update(User entity) {
        String sql = "update from users set first_name = ? where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                if (id == entity.getId()) {

                    PreparedStatement statement1 = connection.prepareStatement(sql);
                    statement1.setString(2, entity.getFirstName());
                    statement1.executeUpdate();
                    return entity;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }





}