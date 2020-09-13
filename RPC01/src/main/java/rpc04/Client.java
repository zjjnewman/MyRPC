package rpc04;


import rpc.common.User;
import rpc.common.UserService;

public class Client {
    public static void main(String[] args) {
        UserService stub = Stub.getStub();
        User user = stub.findUserById(123);
        System.out.println(user);
    }
}
