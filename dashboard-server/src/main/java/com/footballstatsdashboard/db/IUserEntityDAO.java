package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserEntityDAO extends IEntityDAO<User> {
    List<User> getExistingUsers(String firstName, String lastName, String emailAddress);
    Optional<User> getUserByEmailAddress(String emailAddress);
}