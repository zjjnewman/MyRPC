package rpc04;

import rpc.common.User;
import rpc.common.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User FindUserById(Integer id) {
        return new User(id, "jia");
    }

    @Override
    public User FindUserByName(String name) {
        return new User(456, name);
    }
}
