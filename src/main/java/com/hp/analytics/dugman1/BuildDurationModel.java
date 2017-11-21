package com.hp.analytics.dugman1;

import com.hp.bdi.Context;
import com.hp.bdi.ElasticsearchTenantContextProvider;
import com.hp.bdi.EsConfig;
import com.hp.bdi.TenantContext;
import golan.hello.spark.utils.SparkEnv;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BuildDurationModel {

    public static final Logger LOGGER = LoggerFactory.getLogger(BuildDurationModel.class);
    public static final String ALGO      = "algo_build_duration";
    public static final String DATA_TYPE = "build";
    public static final String GET_ALL_CONTEXTS_WITH_DATA = UUID.randomUUID().toString();     //doesn't matter what we write here as long as none ever called [com.hp.bdi.ElasticsearchTenantContextProvider.finishedTenantContextUpdate] for it

    public static final String ESQ_ALL_BULDs = "{\n" +
            "\t\"size\" : 5, \n" +
            "\t\"query\" : {\n" +
            "\t    \"type\" : {\n" +
            "\t        \"value\" : \"build\"\n" +
            "\t    }\n" +
            "\t}\n" +
            "}";

    public static void main(String[] args) {
        JavaSparkContext sc = null;
        try {
            System.out.println("BD: Create SparkContext...");
            sc = SparkEnv.getJavaSparkContext(BuildDurationModel.class.getSimpleName());

            final ElasticsearchTenantContextProvider mtProvider = new ElasticsearchTenantContextProvider();
            final List<TenantContext> allContextsWithData = mtProvider.getTenantContextUpdatesList(DATA_TYPE, GET_ALL_CONTEXTS_WITH_DATA);
            if (sc==null) {
                System.out.println("BD: No contexts with data - (null was returned)");
            }
            else if (allContextsWithData.isEmpty()) {
                System.out.println("BD: No contexts with data - (isEmpty=true)");
            }
            else {
                System.out.println("BD: Contexts with data ["+allContextsWithData.size()+"] ==> " + allContextsWithData.stream().map(Context::getName).sorted().collect(Collectors.joining(" ; ")));
            }

            System.out.println("BD: Builds per Contexts:");

            printAllBuilds(sc, allContextsWithData);

            System.out.println("BD: The End");
        } catch (Exception e) {
            System.out.println("BD: ERROR: " + e.getMessage());
            System.out.println("BD: STACKTRACE: \n" + exceptionStacktraceToString(e));
            if (sc!=null) sc.close();
        }
    }

    private static void printAllBuilds(JavaSparkContext sc, List<TenantContext> allContextsWithData) {
        for (TenantContext tenantContext : allContextsWithData) {
            System.out.println("BD: Context ["+tenantContext.getName()+"]");
            final String esIndexName = EsConfig.getInstance().getEsIndexName(tenantContext);
            final JavaPairRDD<String, String> rddBuilds = JavaEsSpark.esJsonRDD(sc, esIndexName + "/" + DATA_TYPE, ESQ_ALL_BULDs);
            final JavaRDD<String> rddLefts = rddBuilds.map(t -> "BD: \t\t" + t._1());
            final List<String> lefts = rddLefts.collect();
            System.out.println("BD: \tLeft ones:");
            lefts.forEach(System.out::println);
            final JavaRDD<String> rddRights = rddBuilds.map(t -> "BD: \t\t" + t._2());
            final List<String> rights = rddRights.collect();
            System.out.println("BD: \tRight ones:");
            rights.forEach(System.out::println);
        }
    }

    private static String systemTimeToHumanTime(long systemTime) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(systemTime));
    }

    private static String exceptionStacktraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }



}
