package golan.hello.spark.core;

import com.hp.bdi.MyLog;
import golan.hello.spark.utils.SparkEnv;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by golaniz on 28/07/2016.
 */
public class PrintoutClasspath {

    public static void main(String[] args) {

        //Get the classpath from the driver

        MyLog.logme("Driver Classpath:\n" + getClassPathPlusDetails());


        //get the classpath from the worker

        JavaSparkContext sparkContext = SparkEnv.getJavaSparkContext("PrintoutClasspath");
        //noinspection RedundantTypeArguments
        JavaRDD<String> dummy = sparkContext.<String>parallelize(Collections.<String>singletonList("dummy"));
        JavaRDD<String> classpath = dummy.<String>map(PrintoutClasspath::bbb);

        MyLog.logme("Worker Classpath:\n" + classpath.take(1));

    }

    private static String getClassPathPlusDetails() {

        String classpath = System.getProperty("java.class.path");
        String[] split = classpath.split("[;]");
        List<String> list = Arrays.asList(split);
        list.sort(String::compareTo);
        return list.stream().collect(Collectors.joining("", "\t - ", "\n"));
    }

    private static String bbb(String s) {
        return getClassPathPlusDetails();
    }
}
