package rpc07_threadpool;

import rpc.common.ProductA;
import rpc.common.ProductService;

public class Client {
    public static boolean isMoreClient = false;

    public static void main(String[] args) throws InterruptedException {

        doManyClient();
        Thread.sleep(1000);
//        doClient();
    }

    public static void doManyClient(){
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                doClient();
            }).start();
        }
    }

    public static void doClient(){
        ProductService t = (ProductService) Stub.getStub(ProductService.class);
        System.out.println("t: " + t.getClass());
        ProductA productA = t.getProductByName("ppA");
        System.out.println(productA);
    }
}
