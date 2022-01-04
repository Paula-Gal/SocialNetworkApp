package com.example.lab6.repository.db;

import com.example.lab6.model.User;
import com.example.lab6.model.validators.Validator;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.Paginator;
import com.example.lab6.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDbRepository implements PagingRepository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;


    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;


    }

    @Override
    public User findOne(Long aLong) {

        String sql = "SELECT * from users where id = " + aLong;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                // if(id == aLong) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    User utilizator = new User(firstName, lastName, email, password);
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
    public User findOneByEmail(String email) {
        try {
             Connection connection = DriverManager.getConnection(url, username, password);
             String sql = "Select * from users where email = ?";
             PreparedStatement statement = connection.prepareStatement(sql);
             statement.setString(1, email);
             ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String password = resultSet.getString("password");
                    User user = new User(firstName, lastName, email, password);
                    user.setId(id);
                    return user;

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
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User utilizator = new User(firstName, lastName, email, password);
                utilizator.setId(id);
                String sql = "select * from friendships f where f.first_friend = " + utilizator.getId() + " or f.second_friend = " + String.valueOf(utilizator.getId());
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

        String sql = "insert into users (first_name, last_name, email, password ) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void savePicture(String email, String urlphoto) {


        String sql = "insert into users_profile_pictures (email, url) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, urlphoto);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updatePicture(String email, String urlphoto) {
        String sql = "update users_profile_pictures set url = ? where email = \'" + email + "\'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, urlphoto);
                ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String findPhoto(String email) {

        String sql = "SELECT * from users_profile_pictures where email = \'" + email + "\'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                String urlphoto = resultSet.getString("url");
                return urlphoto;
            }
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
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    User utilizator = new User(firstName, lastName, email, password);
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

    @Override
    public Page<User> findAll(Pageable pageable) {
        Paginator<User> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}