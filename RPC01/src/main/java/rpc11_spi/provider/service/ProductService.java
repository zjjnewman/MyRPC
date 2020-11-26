package rpc11_spi.provider.service;


import rpc11_spi.pojo.ProductA;

public interface ProductService {
    public ProductA getProductById(Integer id);
    public ProductA getProductByName(String name);
    public ProductA createProduct(Integer id, String name);
}
