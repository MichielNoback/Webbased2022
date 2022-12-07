package nl.bioinf.dao;

import nl.bioinf.model.Role;
import nl.bioinf.model.User;

public interface UserDao {
    User getUser(String userName, String userPass);

    void insertUser(String userName, String userPass, String email, Role role);
}
