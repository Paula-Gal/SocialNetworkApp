package com.example.lab6.repository.db;

import com.example.lab6.model.Friendship;
import com.example.lab6.model.Tuple;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.Validator;
import com.example.lab6.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements  Repository<Tuple<Long, Long>, Friendship> {

    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;



    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }

    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        String sql = "SELECT * from friendships where first_friend = " + String.valueOf(longLongTuple.getE1()) + "  and second_friend = " + longLongTuple.getE2() +
                " or second_friend = " + longLongTuple.getE2() + " and first_friend = longLongTuple.getE2()";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long first = resultSet.getLong("first_friend");
                Long second = resultSet.getLong("second_friend");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Tuple<Long, Long> ship = new Tuple<>(first, second);
               // if (ship.getE1() == longLongTuple.getE1() && ship.getE2() == longLongTuple.getE2()) {
                Friendship friendship = new Friendship(ship);
                friendship.setId(ship);
                friendship.setDate(date);
                return friendship;
                //}

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long first = resultSet.getLong("first_friend");
                Long second = resultSet.getLong("second_friend");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Tuple<Long, Long> ship = new Tuple<>(first, second);
                Friendship friendship = new Friendship(ship);
                friendship.setId(ship);
                friendship.setDate(date);
                friendships.add(friendship);
            }

            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

   @Override
   public Friendship save(Friendship entity) {
       if (entity == null)
           throw new IllegalArgumentException("Entity must not be null!");

        String sql = "insert into friendships (first_friend, second_friend, date) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(String.valueOf(entity.getE1())));
            ps.setInt(2, Math.toIntExact(entity.getE2()));
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship remove(Friendship entity) {
        String sql = "delete from friendships where first_friend = ? and second_friend = ? or second_friend = ? and first_friend = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long first = resultSet.getLong("first_friend");
                Long second = resultSet.getLong("second_friend");

                Tuple<Long, Long> ship = new Tuple<>(first, second);
                if (ship.getE1() == entity.getE1() && ship.getE2() == entity.getE2()) {
                    Friendship friendship = new Friendship(ship);
                    friendship.setId(ship);
                    PreparedStatement statement1 = connection.prepareStatement(sql);
                    statement1.setInt(1, Math.toIntExact(ship.getE1()));
                    statement1.setInt(2, Math.toIntExact(ship.getE2()));
                    statement1.executeUpdate();
                    return friendship;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }

  /* public Iterable<User> getFriends(User user){
       Set<User> friendships = new HashSet<>();
       String sql = "select * from friendships f where f.first_friend = " + String.valueOf(user.getId())+ " or f.second_friend = " + String.valueOf(user.getId());
       try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
           while (resultSet.next()) {
               Long first = resultSet.getLong("first_friend");
               Long second = resultSet.getLong("second_friend");
               if(first == user.getId())
                   friendships.add(repoUser.findOne(second));
               if(second == user.getId())
                   friendships.add(repoUser.findOne(first));
           }

           return friendships;
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return friendships;
   }
   */


    @Override
    public Friendship update(Friendship entity) {
        String sql = "update from friendhsips set date = ? where first_friend = ? and second_friend = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long first = resultSet.getLong("first_friend");
                Long second = resultSet.getLong("second_friend");
                Tuple<Long, Long> ship = new Tuple<>(first, second);
                if (ship.getE1() == entity.getE1() && ship.getE2() == entity.getE2()) {
                    PreparedStatement statement1 = connection.prepareStatement(sql);
                    statement1.setDate(3, Date.valueOf(LocalDate.now()));
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

