package rpc01;

import rpc.common.UserService;
import rpc.common.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static boolean running = true;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket = serverSocket.accept();

        while (running){
            process(socket);
            socket.close();
        }

    }

    public static void process(Socket socket) throws IOException {
        // 获取客户端输入的值
        InputStream in = socket.getInputStream();
        // 获取这个socket的输出流
        OutputStream out = socket.getOutputStream();

        DataInputStream dataInputStream = new DataInputStream(in);
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        Integer id =  dataInputStream.readInt();
        UserService service = new UserServiceImpl();
        User user = service.findUserById(id);

        dataOutputStream.writeInt(user.getId());
        dataOutputStream.writeUTF(user.getName());

        dataOutputStream.flush();
    }

}
