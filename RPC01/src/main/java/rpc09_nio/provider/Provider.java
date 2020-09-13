package rpc09_nio.provider;

import com.caucho.hessian.io.Hessian2StreamingOutput;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Provider {
    static HashMap<String, Class<?>> cache = new HashMap<>();
    static {
        cache.put("rpc.common.UserService", UserServiceImpl.class);
        cache.put("rpc.common.ProductService", ProductServiceImpl.class);
    }

    private static void process(Socket socket) throws Exception {
        // 接受连接过来的socket，用输入流接收数据
        InputStream inputStream = socket.getInputStream();
        // 获取输入流的 通用对象数据包装类
        ObjectInputStream ois = new ObjectInputStream(inputStream);

        // 获取元数据
        // 通过接口名获取实现类
        String interfaceName = (String) ois.readObject();
        String methodName = (String) ois.readObject();
        Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
        Object[] parameters = (Object[]) ois.readObject();

        // 通过接口名获取实现类原信息 强制转换成响应的实现类
        Class<?> clazz = cache.get(interfaceName);
        Method method = clazz.getMethod(methodName, parameterTypes);
        Object o = method.invoke(clazz.newInstance(), parameters);

        // 把数据通过socket 返回给消费者
        OutputStream outputStream = socket.getOutputStream();
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//        objectOutputStream.writeObject(o);
//        Hessian2Output hso =new Hessian2Output(outputStream);
//        hso.writeObject(o);
        Hessian2StreamingOutput hso = new Hessian2StreamingOutput(outputStream);
        hso.writeObject(o);
        System.out.println(o);
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        // 监听 8888端口
        serverSocket.bind(new InetSocketAddress(8888));
        while (true){
            Socket socket = serverSocket.accept();
            process(socket);
            socket.close();
        }
    }
}
