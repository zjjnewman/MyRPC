package rpc03;

import rpc.common.UserService;
import rpc.common.User;

public class Client {

    public static void main(String[] args) {
        UserService service = Stub.getStub();
        User user = service.findUserById(123);
        System.out.println(user);
    }
}
