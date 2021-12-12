package com.example.lab6.repository.db;

import com.example.lab6.model.FriendRequest;
import com.example.lab6.model.Status;
import com.example.lab6.model.Tuple;
import com.example.lab6.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;

public class FriendRequestDbRepository implements Repository<Tuple<Long, Long>, FriendRequest> {
    private String url;
    private String username;
    private String password;

    public FriendRequestDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> longLongTuple) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM friend_requests WHERE \"from\" = ? AND \"to\" = ? ";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, (int) (long) longLongTuple.getE1());
            statement.setInt(2, (int) (long) longLongTuple.getE2());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Tuple<Long, Long> ship = new Tuple<>(longLongTuple.getE1(), longLongTuple.getE2());
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("last_update_date").toLocalDateTime();
                Status status1;
                if(status.equals("APPROVED"))
                    status1 = Status.APPROVED;
                else if(status.equals("PENDING"))
                    status1 = Status.PENDING;
                else status1 = Status.REJECTED;

                FriendRequest friendRequest = new FriendRequest(from, to, status1, dateTime);
                friendRequest.setId(ship);
                return friendRequest;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<FriendRequest> findAll() {
        return null;
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "INSERT INTO friend_requests (\"from\", \"to\", status, last_update_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Math.toIntExact(entity.getFrom()));
            ps.setInt(2, Math.toIntExact(entity.getTo()));
            ps.setString(3, String.valueOf(entity.getStatus()));
            ps.setTimestamp(4, Timestamp.valueOf(entity.getLastUpdatedDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FriendRequest remove(FriendRequest entity) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "DELETE FROM friend_requests WHERE \"from\" = ? AND \"to\" = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, (int) (long) entity.getId().getE1());
            statement.setInt(2, (int) (long) entity.getId().getE2());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public FriendRequest update(FriendRequest entity) {

        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "update friend_requests set (last_update_date, status) = (?, ?) where \"from\" = ? and \"to\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(3, Math.toIntExact(entity.getFrom()));
            ps.setInt(4, Math.toIntExact(entity.getTo()));
            ps.setString(2, String.valueOf(entity.getStatus()));
            ps.setTimestamp(1, Timestamp.valueOf(entity.getLastUpdatedDate()));

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
