package rpc11_spi.provider.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

public class FindImplByJdkSpi {
    public static HashMap<String, Class<?>> implCache = new HashMap<>();

    public static Class<?> getInterfaceImpl(String interfaceName) throws ClassNotFoundException {
        Class<?> clazz;
        if((clazz = implCache.get(interfaceName)) == null){
            clazz = loadServiceImpl(interfaceName).getClass();
        }
        return clazz;
    }

    private static Object loadServiceImpl(String interFaceName) throws ClassNotFoundException {
        ServiceLoader<?> load = ServiceLoader.load(Class.forName(interFaceName));
        Iterator<?> iterator = load.iterator();
        Object nextImpl = null;
        while (iterator.hasNext()){
            nextImpl = iterator.next();
//            implCache.put(interFaceName, nextImpl.getClass());
            System.out.println("spi: " + nextImpl.getClass().getName());
            break;
        }
        return nextImpl;
    }

    private static boolean updateZK(){

        return true;
    }
}
