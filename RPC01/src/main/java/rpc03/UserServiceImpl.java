package rpc03;

import rpc.common.UserService;
import rpc.common.User;

public class UserServiceImpl implements UserService {

    @Override
    public User findUserById(Integer id) {
        return new User(id, "WangWu");
    }

    @Override
    public User findUserByName(String name) {
        return new User(456, name);
    }
}
