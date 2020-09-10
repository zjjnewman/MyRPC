package rpc02;

import rpc.common.User;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        // 代理
        Stub stub = new Stub();
        User user = stub.FindUserById(123);
        System.out.println(user);
    }
}
