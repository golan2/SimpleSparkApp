package golan.hello.spark.utils;

import org.apache.spark.api.java.function.Function;

import java.lang.management.ManagementFactory;

/**
 * Created by golaniz on 28/07/2016.
 */
public class ClasspathCalculator implements Function<Integer, String> {


    // WHY???
    // It is my way to force the code to run on the Worker instead on the Driver

    @Override
    public String call(Integer s) throws Exception {
        String classpath = System.getProperty("java.class.path");
        return  classpath.replace(":", "\n");
    }
}
