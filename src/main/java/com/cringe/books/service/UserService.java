package com.cringe.books.service;

import com.cringe.books.model.User;
import com.cringe.books.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void addUser(Long id) {
        if (!userRepo.existsById(id)) {
            User user = new User();
            user.setId(id);
            userRepo.save(user);
        }
    }

}
