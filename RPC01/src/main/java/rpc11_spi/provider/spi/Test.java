package rpc11_spi.provider.spi;

import rpc11_spi.provider.service.ProductService;

import java.util.Iterator;
import java.util.ServiceLoader;

public class Test {
    public static <T> void loadServiceImpl(Class<T> clazz){
        ServiceLoader<T> load = ServiceLoader.load(clazz);
        Iterator<T> iterator = load.iterator();
        while (iterator.hasNext()){
            T t = iterator.next();
            System.out.println(t.getClass().getName());
        }
    }
    public static void main(String[] args) throws ClassNotFoundException {
//        ServiceLoader<ProductService> sl = ServiceLoader.load(ProductService.class);
//        Iterator<ProductService> iterator = sl.iterator();
//        while (iterator.hasNext()){
//            ProductService p = iterator.next();
//            System.out.println(p.getProductById(1));
//            System.out.println(p.getClass().getName());
//        }
        loadServiceImpl(Class.forName("rpc11_spi.provider.service.UserService"));
    }
}
