package rpc01;

import rpc.common.User;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8888);

        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(2);
        dataOutputStream.flush();

        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        System.out.println(new User(dataInputStream.readInt(), dataInputStream.readUTF()));

        dataInputStream.close();
        dataOutputStream.close();
    }
}
