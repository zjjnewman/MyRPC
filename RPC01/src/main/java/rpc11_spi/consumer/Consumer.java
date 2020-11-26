package rpc11_spi.consumer;

import rpc11_spi.consumer.service.ProductService;
import rpc11_spi.consumer.service.UserService;
import rpc11_spi.pojo.User;
import rpc11_spi.pojo.ProductA;

public class Consumer {


    public static void main(String[] args) throws Exception {
//        doManyClient();
        doClient();
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
//            Thread.sleep(1000);
        }
    }

    public static void doClient() throws Exception {
        UserService userService = (UserService) ConsumerStub.getConsumerStub(UserService.class);
        User user = userService.findUserById(1);
        System.out.println(user);

//        ProductService productService = (ProductService) ConsumerStub.getConsumerStub(ProductService.class);
//        ProductA product = productService.getProductById(5);
//        System.out.println(product);
    }
}
