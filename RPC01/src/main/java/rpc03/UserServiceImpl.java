package rpc03;

import rpc.common.UserService;
import rpc.common.User;

public class UserServiceImpl implements UserService {

    @Override
    public User FindUserById(Integer id) {
        return new User(id, "WangWu");
    }

    @Override
    public User FindUserByName(String name) {
        return new User(456, name);
    }
}
