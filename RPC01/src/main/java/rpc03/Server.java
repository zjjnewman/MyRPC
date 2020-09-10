package rpc03;

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

    private static void process(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        int id = dataInputStream.readInt();

        User user = new UserServiceImpl().FindUserById(id);

        dataOutputStream.writeInt(user.getId());
        dataOutputStream.writeUTF(user.getName());
        dataOutputStream.flush();

        dataInputStream.close();
        dataOutputStream.close();


    }
}
