package rpc07_threadpool;

import redis.clients.jedis.Jedis;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {

    private static boolean running = true;
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5), new ThreadPoolExecutor.AbortPolicy());

        while (running){
            Socket socket = serverSocket.accept();
//            threadPoolExecutor.execute(new ProcessTask(socket));

            FutureTask<Object> futureTask = new FutureTask<>(new ProcessTaskRet(socket));
            threadPoolExecutor.submit(futureTask);
            System.out.println("getTaskCount 线程需要执行的任务数量: " + threadPoolExecutor.getTaskCount());
            System.out.println("getCompletedTaskCount 线程池已经完成的任务数量: " + threadPoolExecutor.getCompletedTaskCount());
            System.out.println("getLargestPoolSize线程池曾经创建过的最大线程数量" + threadPoolExecutor.getLargestPoolSize());
            System.out.println("getPoolSize 线程池的线程数量：" + threadPoolExecutor.getPoolSize());
            System.out.println("getActiveCount 获取活动的线程数：" + threadPoolExecutor.getActiveCount());
            Thread.sleep(1000);
            System.out.println("process(socket) 用futurnTask获取结果: " + futureTask.get());
            System.out.println("----------------------------------------");
        }

    }

    static class ProcessTask implements Runnable{
        Socket socket;
        public ProcessTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                process(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ProcessTaskRet implements Callable {
        Socket socket;
        public ProcessTaskRet(Socket socket) {
            this.socket = socket;
        }


        @Override
        public Object call() throws Exception {
            Object process = process(socket);
            System.out.println("call 获取返回值：" + process);
            return process;
        }
    }

    private static Object process(Socket socket) throws Exception {

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
        System.out.println("o 服务端执行客户端调用的方法后获取的结果: "+o);
        return o;
    }
}
