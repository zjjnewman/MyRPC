package rpc05;

import redis.clients.jedis.Jedis;
import rpc.common.User;
import rpc.common.UserService;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static boolean running = true;
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket = serverSocket.accept();
        while (running){
            process(socket);
            socket.close();
        }

    }

    private static void process(Socket socket) throws Exception {

        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        /**
         *                 objectOutputStream.writeObject(methodName);
         *                 objectOutputStream.writeObject(parameterTypes);
         *                 objectOutputStream.writeObject(args);
         */

        String className = objectInputStream.readUTF();
        String methodName = objectInputStream.readUTF();
        Class[] parameterTypes = (Class[]) objectInputStream.readObject();
        Object[] args = (Object[]) objectInputStream.readObject();

        Class clazz = null;

        // 从服务注册表找到具体的类(这里用redis实现服务注册，服务指的是接口的具体实现类)
        Jedis jedis = new Jedis("remotehost1", 6379);
        String classImplName = jedis.get(className);
        System.out.println("className: " + className);
        System.out.println("classImplName: " + classImplName);
        clazz = Class.forName(className).getClassLoader().loadClass(classImplName);

        Method method = clazz.getMethod(methodName, parameterTypes);
        Object o = method.invoke(clazz.newInstance(), args);

        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(o);
        objectOutputStream.flush();

        objectOutputStream.close();
        objectInputStream.close();
    }
}
