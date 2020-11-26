package rpc11_spi.provider.service.impl;

import rpc11_spi.pojo.User;
import rpc11_spi.provider.service.UserService;

public class UserServiceImpl1 implements UserService {
    @Override
    public User findUserById(Integer id) {
        return new User(id, "jia");
    }

    @Override
    public User findUserByName(String name) {
        return new User(456, name);
    }
}
