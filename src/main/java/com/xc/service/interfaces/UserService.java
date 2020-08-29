package com.xc.service.interfaces;

import com.xc.po.User;

public interface UserService {

    User checkUser(String username, String password);
}
