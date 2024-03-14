package com.cringe.books.repository;

import com.cringe.books.model.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepo extends JpaRepository<Whitelist, Long> {
}
