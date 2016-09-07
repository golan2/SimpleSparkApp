package com.hp.analytics.dugman1;

import com.hp.bdi.ConfigException;
import com.hp.bdi.ElasticsearchTenantContextProvider;
import com.hp.bdi.EsConfig;
import com.hp.bdi.TenantContext;
import golan.hello.spark.utils.SparkEnv;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ParityPrediction {
    public static final String DATA_TYPE = "build";
    public static final String ALGORITHM = "parity_predict";
    private static final Logger LOGGER = LoggerFactory.getLogger(ParityPrediction.class);

    public static void main(String[] args) throws ConfigException {
        JavaSparkContext jsc = SparkEnv.getJavaSparkContext("ParityPrediction");

        EsConfig esConfig = EsConfig.getInstance();
        ElasticsearchTenantContextProvider tcp = new ElasticsearchTenantContextProvider();
        List<TenantContext> contexts = tcp.getTenantContextUpdatesList(DATA_TYPE, ALGORITHM);

        for (TenantContext context : contexts) {
            String indexName = esConfig.getEsIndexName(context);

            JavaPairRDD<String, String> prdd = JavaEsSpark.esJsonRDD(jsc, indexName + "/build", "_search?size=100");

            List<String> list = prdd.map(p -> "[" + p._1() + " = " + p._2() + "]").collect();
            String all = list.stream().collect(Collectors.joining("\n"));

            LOGGER.warn(all);
        }

    }


}
