package rpc08_hession.consumer;

import redis.clients.jedis.Jedis;
import rpc.common.ProductA;
import rpc.common.ProductService;
import rpc.common.User;
import rpc.common.UserService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Consumer {


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UserService userService = (UserService) ConsumerStub.getConsumerStub(UserService.class);
        User user = userService.findUserById(1);

        ProductService productService = (ProductService) ConsumerStub.getConsumerStub(ProductService.class);
        ProductA product = productService.getProductById(5);
        System.out.println(user);
        System.out.println(product);
    }
}
