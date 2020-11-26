package rpc11_spi.pojo;

import java.io.Serializable;

/**
 * 暴露出对user的接口
 */
public class User implements Serializable {
    private static final long serialVersionID = 1L;

    private Integer id;
    private String name;

    public User(Integer id, String name){
        this.name = name;
        this.id = id;
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
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
