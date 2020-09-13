package hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.Hessian2StreamingOutput;
import com.caucho.hessian.io.HessianOutput;
import org.junit.jupiter.api.Test;
import rpc.common.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializeTest {
    User user = new User(1, "z");

    @Test
    public void all() throws IOException {
        hessian();
        jdk();
    }

    @Test
    public void hessian() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(bos);
        hessianOutput.writeObject(user);
        System.out.println("hessianOutput: "+bos.toByteArray().length);

        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(bos2);
        hessian2Output.writeObject(user);
        System.out.println("hessian2Output: " + bos2.toByteArray().length);

        ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
        Hessian2StreamingOutput hso = new Hessian2StreamingOutput(bos3);
        hso.writeObject(user);
        System.out.println("Hessian2StreamingOutput: "+bos3.toByteArray().length);
    }

    @Test
    public void jdk() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(user);
        byte[] bytes = bos.toByteArray();
        System.out.println("jdk: "+bytes.length);
    }
}
