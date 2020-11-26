public class T1 {

    public T1 t1 = null;

    private T1(){

    }

    public T1 getT1(){
        if(t1 == null){
            synchronized (T1.class){
                if(t1 == null){
                    t1 = new T1();
                }
            }
        }
        return t1;
    }
}
