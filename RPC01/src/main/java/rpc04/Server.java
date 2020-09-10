package rpc04;

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
        OutputStream outputStream = socket.getOutputStream();

        // 负责把 客户端 所需要的参数往外发。
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        // 负责吧从服务端接受到的参数接收过来。
        ObjectInputStream objInputStream = new ObjectInputStream(inputStream);

        /**
         *                 objOutputStream.writeObject(methodName);
         *                 objOutputStream.writeObject(parameterTypes);
         *                 objOutputStream.writeObject(args);
         */
        String methodName = objInputStream.readUTF();
        Class[] parameterTypes = (Class[]) objInputStream.readObject();
        Object[] ags = (Object[]) objInputStream.readObject();

        UserService userService = new UserServiceImpl();
        Method method = userService.getClass().getMethod(methodName, parameterTypes);
        User user = (User) method.invoke(userService, ags);

        dataOutputStream.writeInt(user.getId());
        dataOutputStream.writeUTF(user.getName());
        dataOutputStream.flush();
    }
}
