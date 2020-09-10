package rpc02;

import rpc.common.User;

import java.io.*;
import java.net.Socket;

/**
 * 客户端代理，屏蔽复杂的网络连接
 */
public class Stub {
    public User FindUserById(Integer id) throws IOException {
        Socket socket = new Socket("localhost", 8888);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(123);

        socket.getOutputStream().write(baos.toByteArray());
        socket.getOutputStream().flush();

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int uid = dataInputStream.readInt();
        String name = dataInputStream.readUTF();
        User user = new User(uid, name);

        socket.close();
        baos.close();
        dos.close();
        dataInputStream.close();
        return user;
    }
}
