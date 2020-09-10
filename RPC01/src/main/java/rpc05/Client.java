package rpc05;

import rpc.common.User;
import rpc.common.UserService;

public class Client {

    public static void main(String[] args) {
        UserService t = (UserService) Stub.getStub(UserService.class);
        User user = t.FindUserById(123);
        System.out.println(user);
    }
}
