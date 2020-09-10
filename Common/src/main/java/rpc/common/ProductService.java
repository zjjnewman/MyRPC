package rpc.common;

import java.util.Iterator;

public interface ProductService {
    public ProductA getProductById(Integer id);
    public ProductA getProductByName(String name);
    public ProductA createProduct(Integer id, String name);
}
