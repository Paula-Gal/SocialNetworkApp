package com.example.lab6.repository.paging;


import com.example.lab6.model.Entity;
import com.example.lab6.repository.Repository;


public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
