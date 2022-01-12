package com.example.lab6.repository.db;

import com.example.lab6.model.Event;
import com.example.lab6.model.Post;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostDbRepository implements PagingRepository<Long, Post> {
    private String url;
    private String username;
    private String password;

    public PostDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    @Override
    public Post findOne(Long aLong) {
        String sql = "SELECT * FROM posts WHERE id = " + aLong;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user = resultSet.getLong("user");
                String url = resultSet.getString("photo");
                String description = resultSet.getString("description");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Post post = new Post(user, url, description, date);
                post.setId(id);
                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Post> findAll() {

        Set<Post> posts = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from posts");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user = resultSet.getLong("user");
                String url = resultSet.getString("photo");
                String description = resultSet.getString("description");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Post post = new Post(user, url, description, date);
                post.setId(id);


                posts.add(post);
            }
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post save(Post entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        String sql = "insert into posts (\"user\", photo, description, date) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Math.toIntExact(entity.getAdmin()));
            if (entity.getUrl() != null)
                ps.setString(2, (entity.getUrl()));
            else {
                ps.setNull(2, Types.NULL);
            }
            if (entity.getDescription() != null)
                ps.setString(3, (entity.getDescription()));
            else {
                ps.setNull(3, Types.NULL);
            }
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Post remove(Post entity) {

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "DELETE FROM posts WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, (int) (long) entity.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Post update(Post entity) {
        return null;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return null;
    }
}
