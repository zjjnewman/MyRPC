package rpc.common;

public interface UserService {
    public User findUserById(Integer id);

    public User findUserByName(String name);
}
