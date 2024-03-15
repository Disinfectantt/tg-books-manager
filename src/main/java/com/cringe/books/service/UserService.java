package com.cringe.books.service;

import com.cringe.books.model.User;
import com.cringe.books.repository.UserRepo;
import com.cringe.books.repository.WhitelistRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final WhitelistRepo whitelistRepo;
    @Value("${isWhitelist}")
    private boolean isWhitelist;

    @Autowired
    public UserService(UserRepo userRepo, WhitelistRepo whitelistRepo) {
        this.userRepo = userRepo;
        this.whitelistRepo = whitelistRepo;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public boolean isInWhitelist(Long id) {
        return whitelistRepo.existsById(id);
    }

    public void addUser(Long id) {
        if (!userRepo.existsById(id)) {
            User user = new User();
            user.setId(id);
            userRepo.save(user);
        }
    }

}
