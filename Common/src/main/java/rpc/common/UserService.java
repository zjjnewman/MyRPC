package rpc.common;

public interface UserService {
    public User FindUserById(Integer id);

    public User FindUserByName(String name);
}
