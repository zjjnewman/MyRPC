package rpc09_nio.consumer;

import rpc.common.ProductA;
import rpc.common.ProductService;
import rpc.common.User;
import rpc.common.UserService;

import java.io.IOException;

public class Consumer {


    public static void main(String[] args) throws Exception {
        UserService userService = (UserService) ConsumerStub.getConsumerStub(UserService.class);
        User user = userService.findUserById(1);

        ProductService productService = (ProductService) ConsumerStub.getConsumerStub(ProductService.class);
        ProductA product = productService.getProductById(5);
        System.out.println(user);
        System.out.println(product);
    }
}
