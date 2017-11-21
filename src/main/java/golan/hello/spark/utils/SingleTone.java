package golan.hello.spark.utils;

public class SingleTone {

    private static class SingletonHolder {
        public static final SingleTone instance = new SingleTone();
    }

    public static SingleTone getInstance() {
        return SingletonHolder.instance;
    }

    public void tone() {
        System.out.println("This is a single tone :-)");
    }
}
