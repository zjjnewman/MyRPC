package rpc04;

import rpc.common.User;
import rpc.common.UserService;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.function.ObjIntConsumer;

/**
 * 对客户端进行代理，代理客户端对于某种 接口，乃至接口方法的需求
 * 提供对于同一个接口里面随意方法的支持
 */
public class Stub {
    public static UserService getStub(){
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket("localhost", 8888);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                // 负责把 客户端 所需要的参数往外发。
                ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
                // 负责吧从服务端接受到的参数接收过来。
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();

                System.out.println("methodName:  "+methodName);
                for (Class<?> parameterType : parameterTypes) {
                    System.out.println("parameterType:  "+parameterType.getName());
                }

                objOutputStream.writeUTF(methodName);
                objOutputStream.writeObject(parameterTypes);
                objOutputStream.writeObject(args);
                objOutputStream.flush();

                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();

                objOutputStream.close();
                dataInputStream.close();
                return new User(id, name);
            }
        };

        Object o = Proxy.newProxyInstance(UserService.class.getClassLoader(), new Class[]{UserService.class}, invocationHandler);
        return (UserService) o;
    }
}
