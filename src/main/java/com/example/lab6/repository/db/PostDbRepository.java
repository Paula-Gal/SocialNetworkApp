package com.example.lab6.repository.db;

import com.example.lab6.model.Post;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.PagingRepository;

public class PostDbRepository implements PagingRepository<Long, Post> {
    @Override
    public Post findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Post> findAll() {
        return null;
    }

    @Override
    public Post save(Post entity) {
        return null;
    }

    @Override
    public Post remove(Post entity) {
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
