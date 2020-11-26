package rpc11_spi.provider.service;

import rpc11_spi.pojo.User;

public interface UserService {
    public User findUserById(Integer id);

    public User findUserByName(String name);
}
