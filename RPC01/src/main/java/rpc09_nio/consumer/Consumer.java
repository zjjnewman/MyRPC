package rpc09_nio.consumer;

import com.sun.java.browser.plugin2.DOM;
import rpc.common.ProductA;
import rpc.common.ProductService;
import rpc.common.User;
import rpc.common.UserService;

import java.io.IOException;

public class Consumer {


    public static void main(String[] args) throws Exception {
        doManyClient();
//        doClient();
    }

    public static void doManyClient() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                try {
                    doClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void doClient() throws Exception {
        UserService userService = (UserService) ConsumerStub.getConsumerStub(UserService.class);
        User user = userService.findUserById(1);

        ProductService productService = (ProductService) ConsumerStub.getConsumerStub(ProductService.class);
        ProductA product = productService.getProductById(5);
        System.out.println(user);
        System.out.println(product);
    }
}
