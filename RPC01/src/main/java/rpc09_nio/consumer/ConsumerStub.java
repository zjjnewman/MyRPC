package rpc09_nio.consumer;

import com.caucho.hessian.io.Hessian2StreamingInput;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConsumerStub {
    static Jedis jedis = new Jedis("remotehost0", 6379);

    public static Object getConsumerStub(Class<?> clazz) throws IOException, ClassNotFoundException {
        // 创建匿名《调用处理对象》
        InvocationHandler consumerInvocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket();

                // 从注册中心获取服务提供者
                // 获取消费者元数据
                // 获取接口名
                String className = clazz.getName();
                System.out.println("className: "+className);
                // 获取方法名
                String methodName = method.getName();
                // 获取参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();


                String providerMeta = jedis.get(className);
                System.out.println("providerMeta： "+providerMeta);
                String[] hostAndPort = providerMeta.split(":");

                // 连接服务提供者
                socket.connect(new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                // 告诉服务提供者诉求元数据
                oos.writeObject(className);
                oos.writeObject(methodName);
                oos.writeObject(parameterTypes);
                oos.writeObject(args);
                oos.flush();

                // 接受结果
                InputStream inputStream = socket.getInputStream();
//                ObjectInputStream ois = new ObjectInputStream(inputStream);
//                Object o = ois.readObject();
                Hessian2StreamingInput hsi = new Hessian2StreamingInput(inputStream);
                Object o = hsi.readObject();

//                ois.close();
                oos.close();
                hsi.close();
                return o;
            }
        };

        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, consumerInvocationHandler);
    }
}
