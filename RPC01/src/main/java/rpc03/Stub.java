package rpc03;

import rpc.common.UserService;
import rpc.common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * 这个代理类，有网络连接的具体实现，帮助客户端屏蔽复杂的内部实现
 */

public class Stub {

    public static UserService getStub() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 客户端发数据，然后接受数据

                System.out.println("proxy = " + proxy.getClass().getInterfaces()[0]);
                System.out.println("method = " + method.getName());
                Socket s = new Socket("localhost", 8888);

                OutputStream outputStream = s.getOutputStream();
                InputStream inputStream = s.getInputStream();

                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                dataOutputStream.writeInt(123);
                dataOutputStream.flush();

                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();

                User user = new User(id, name);
                dataInputStream.close();
                dataOutputStream.close();
                s.close();
                return user;
            }
        };

        Object o = Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class[]{UserService.class},
                invocationHandler);

        System.out.println(o.getClass().getName());
        return (UserService)o;

    }

}
