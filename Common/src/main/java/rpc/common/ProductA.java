package rpc.common;

import java.io.Serializable;

public class ProductA implements Serializable {
    private Integer id;
    private String name;

    public ProductA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductA{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
