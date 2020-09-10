package rpc05;

import rpc.common.ProductA;
import rpc.common.ProductService;

public class ProductServiceImpl implements ProductService {
    @Override
    public ProductA getProductById(Integer id) {
        return new ProductA(id, "pA");
    }

    @Override
    public ProductA getProductByName(String name) {
        return new ProductA(789, name);
    }

    @Override
    public ProductA createProduct(Integer id, String name) {
        return new ProductA(id, name);
    }
}
