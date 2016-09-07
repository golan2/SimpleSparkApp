package golan.hello.spark.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by golaniz on 28/07/2016.
 */
public class SparkEnv {
    public static JavaSparkContext getJavaSparkContext(String appName) {
        SparkConf sparkConf = new SparkConf().setAppName(appName).setMaster("local[*]");
        return new JavaSparkContext(sparkConf);
    }
}
