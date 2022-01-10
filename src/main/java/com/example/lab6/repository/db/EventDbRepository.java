package com.example.lab6.repository.db;

import com.example.lab6.model.Event;
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

public class EventDbRepository implements PagingRepository<Long, Event> {

    private String url;
    private String username;
    private String password;

    public EventDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Event findOne(Long aLong) {

        String sql = "SELECT * FROM events WHERE id = " + aLong;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                LocalDateTime startDate = resultSet.getTimestamp("start_date").toLocalDateTime();
                LocalDateTime endDate = resultSet.getTimestamp("end_date").toLocalDateTime();
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                Long admin = resultSet.getLong("admin");
                LocalDateTime creationDate = resultSet.getTimestamp("creation_date").toLocalDateTime();
                Event event = new Event(title, creationDate, startDate, endDate, description, location, admin);
                event.setId(id);

                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                LocalDateTime startDate = resultSet.getTimestamp("start_date").toLocalDateTime();
                LocalDateTime endDate = resultSet.getTimestamp("end_date").toLocalDateTime();
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                Long admin = resultSet.getLong("admin");
                LocalDateTime creationDate = resultSet.getTimestamp("creation_date").toLocalDateTime();
                Event event = new Event(title, creationDate, startDate, endDate, description, location, admin);
                event.setId(id);

                String sql = "SELECT * FROM events_subscribers WHERE \"eventID\" = " + event.getId();
                PreparedStatement statement1 = connection.prepareStatement(sql);
                ResultSet resultSet1 = statement1.executeQuery();
                List<Long> subscribers = new ArrayList<>();
                while (resultSet1.next()) {
                    Long subscriber = resultSet1.getLong("userID");
                    subscribers.add(subscriber);
                }
                event.setSubscribers(subscribers);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "insert into events (title, start_date, end_date, description, location, admin, creation_date ) values (?, ?, ?, ?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getName());
            ps.setTimestamp(2, Timestamp.valueOf(entity.getStart()));
            ps.setTimestamp(3, Timestamp.valueOf(entity.getEnd()));
            ps.setString(4, entity.getDescription());
            ps.setString(5, entity.getLocation());
            ps.setInt(6, Math.toIntExact(entity.getAdmin()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                saveSubscribers(id, entity.getSubscribers());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void saveSubscribers(Long eventID, List<Long> subscribers) {

        String sql_subs = "insert into events_subscribers (\"eventID\", \"userID\") values (?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql_subs)) {

            subscribers.forEach(x -> {
                try {
                    ps.setInt(1, Math.toIntExact(eventID));
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
    public Event remove(Event entity) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "DELETE FROM events WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, (int) (long) entity.getId());

            List<Long> subs = entity.getSubscribers();
            removeSubscribers(entity.getId(), subs);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void removeSubscribers(Long id, List<Long> subs) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "DELETE FROM events_subscribers WHERE \"eventID\" = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            subs.forEach(x -> {
                try {
                    statement.setInt(1, Math.toIntExact(id));
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
