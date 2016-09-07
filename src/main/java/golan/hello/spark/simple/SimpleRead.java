package golan.hello.spark.simple;

import golan.hello.spark.utils.SparkEnv;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by golaniz on 05/09/2016.
 */
public class SimpleRead {

    public static void main(String[] args) {
        JavaSparkContext context = null;
        try {
            System.out.println("FQDN: " + InetAddress.getLocalHost().getHostName());
            System.out.println("Thread: " + Thread.currentThread().getName());
            System.out.println("QQ: Create context...");
            context = SparkEnv.getJavaSparkContext(SimpleRead.class.getSimpleName());
            System.out.println("QQ: Read file...");
            JavaRDD<String> rdd = context.textFile("/data/bdi/use-case/rca/algorithms/izik/PairRDDFunctions.txt");
            List<String> list = rdd.collect();
            System.out.println("QQ: Calc total...");
            int total = 0;
            for (String s : list) {
                total+=s.length();
            }
            System.out.println("QQ: Total = " + total);
        } catch (Exception e) {
            if (context!=null) context.close();
        }
    }
}
