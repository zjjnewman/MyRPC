package rpc08_hession.consumer;

import redis.clients.jedis.Jedis;

import java.net.Socket;

public class ConsumerStub {
    public static Object getConsumerStub(Class<?> clazz){
        Jedis jedis = new Jedis("remotehost1", 6379);
        // key是调用的服务名，value是可以提供服务的 host:prot
//        String host = jedis.get(clazz.getna);
        Socket socket = new Socket();
        return null;
    }
}
