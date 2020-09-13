package rpc08_hession.provider;

import rpc.common.User;
import rpc.common.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User findUserById(Integer id) {
        return new User(id, "jia");
    }

    @Override
    public User findUserByName(String name) {
        return new User(456, name);
    }
}
