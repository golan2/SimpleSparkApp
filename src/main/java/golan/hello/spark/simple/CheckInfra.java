package golan.hello.spark.simple;

import com.hp.bdi.Context;
import com.hp.bdi.ElasticsearchTenantContextProvider;
import com.hp.bdi.EsConfig;
import com.hp.bdi.TenantContext;
import com.hp.indi.analytics.storage.GroupKeyStorage;
import com.hp.indi.analytics.storage.StorageFactory;
import golan.hello.spark.utils.SparkEnv;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CheckInfra {

    public static final String ALGO      = "algo_checkinfra";
    public static final String DATA_TYPE = "build";

    public static void main(String[] args) {
        JavaSparkContext context = null;
        try {
            System.out.println("QQ: Create SparkContext...");
            context = SparkEnv.getJavaSparkContext(CheckInfra.class.getSimpleName());

            System.out.println("QQ: ");
            System.out.println("QQ: [[EsConfig]]");
            final EsConfig esConfig = EsConfig.getInstance();
            System.out.println("QQ: EsConfig: Host=["+esConfig.getElasticSearchHostName()+"] Port=["+esConfig.getElasticSearchPort()+"]");


            System.out.println("QQ: ");
            System.out.println("QQ: [[ElasticsearchTenantContextProvider]]");
            final ElasticsearchTenantContextProvider mtProvider = new ElasticsearchTenantContextProvider();
            System.out.println("QQ: getTenantContextUpdatesList...");
            final List<TenantContext> contextsWithDeltaUpdate = mtProvider.getTenantContextUpdatesList(DATA_TYPE, ALGO);
            if (context==null) {
                System.out.println("QQ: no contextsWithDeltaUpdate - null was returned");
            }
            else if (contextsWithDeltaUpdate.isEmpty()) {
                System.out.println("QQ: no contextsWithDeltaUpdate - (isEmpty=true)");
            }
            else {
                System.out.println("QQ: contextsWithDeltaUpdate size is ["+contextsWithDeltaUpdate.size()+"]");
                System.out.println("QQ: contextsWithDeltaUpdate ==> " + contextsWithDeltaUpdate.stream().map(Context::getName).collect(Collectors.joining(" ; ")));
            }
            final HashSet<String> deltaContextNames = new HashSet<>();
            contextsWithDeltaUpdate.stream().map(TenantContext::getName).forEach(deltaContextNames::add);


            System.out.println("QQ: SKIP ACTUAL READ FROM ELASTIC SEARCH (todo?)");


            final long ts = System.currentTimeMillis();
            System.out.println("QQ: finishedTenantContextUpdate...");
            for (TenantContext tenantContext : contextsWithDeltaUpdate) {
                mtProvider.finishedTenantContextUpdate(tenantContext, ALGO, ts);
            }
            System.out.println("QQ: ["+contextsWithDeltaUpdate.size()+"] lastUpdate were updated. ts=["+ts+"] ts=["+systemTimeToHumanTime(ts)+"]");


            System.out.println("QQ: ");
            System.out.println("QQ: [[ModelDB]]");
            final GroupKeyStorage modelDB = StorageFactory.getModelDB();
            final StringBuilder modelPerContext = new StringBuilder();
            final List<TenantContext> allContextList = mtProvider.getTenantContextUpdatesList(DATA_TYPE, "");
            for (TenantContext tenantContext : allContextList) {
                String value = modelDB.read(tenantContext, DATA_TYPE, ALGO);
                modelPerContext.append("\t[").append(tenantContext.getName()).append("] ==> [").append(value).append("]\n");
                if (deltaContextNames.contains(tenantContext.getName())) {
                    if (value == null || value.length() == 0) {
                        value = "0";
                    }
                    value = String.valueOf(Integer.parseInt(value) + 1);
                    modelDB.store(tenantContext, DATA_TYPE, ALGO, value);
                }
            }
            System.out.println("QQ: modelPerContext ==> \n" + modelPerContext.toString());




            System.out.println("QQ: The End");
        } catch (Exception e) {
            System.out.println("QQ: ERROR: " + e.getMessage());
            System.out.println("QQ: STACKTRACE: \n" + exceptionStacktraceToString(e));
            if (context!=null) context.close();
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
