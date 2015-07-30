package org.infinispan.snappy;


import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class TestSuite {

   @VmOptions("-XX:-TieredCompilation")
   public static class WithMarshaller {

      @Param
      boolean useMarshaller;

      @Param({"10", "100", "1000"})
      int size;

      private RemoteCacheManager cacheManagerWithMarshaller;
      private RemoteCache<Object, Object> cacheWithMarshaller;

      private RemoteCacheManager cacheManagerWithoutMarshaller;
      private RemoteCache<Object, Object> cacheWithoutMarshaller;

      @BeforeExperiment
      void setUp() {
         ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
         configurationBuilder.addServer().host("127.0.0.1");
         configurationBuilder.marshaller(SnappyMarshaller.class);
         configurationBuilder.pingOnStartup(true);
         cacheManagerWithMarshaller = new RemoteCacheManager(configurationBuilder.build());
         cacheWithMarshaller = cacheManagerWithMarshaller.getCache();
         cacheWithMarshaller.clear();

         ConfigurationBuilder configurationBuilderWithoutMarshaller = new ConfigurationBuilder();
         configurationBuilderWithoutMarshaller.addServer().host("127.0.0.1");
         configurationBuilderWithoutMarshaller.marshaller(SnappyMarshaller.class);
         configurationBuilderWithoutMarshaller.pingOnStartup(true);
         cacheManagerWithoutMarshaller = new RemoteCacheManager(configurationBuilderWithoutMarshaller.build());
         cacheWithoutMarshaller = cacheManagerWithoutMarshaller.getCache();
         cacheWithoutMarshaller.clear();
      }

      @Benchmark
      void timePutEntries(int reps) {
         RemoteCache<Object, Object> cache = useMarshaller ? cacheWithMarshaller : cacheWithoutMarshaller;
         char[] data = new char[size];
         for (int i = 0; i < reps; i++) {
            data[0] = (char) (i % 0xFF);
            cache.put(data, data);
         }
      }

      @Benchmark
      void timeGetEntries(int reps) {
         RemoteCache<Object, Object> cache = useMarshaller ? cacheWithMarshaller : cacheWithoutMarshaller;
         char[] data = getRandomData();
         for (int i = 0; i < reps; i++) {
            data[0] = (char) (i % 0xFF);
            cache.get(data);
         }
      }

      private char[] getRandomData() {
         char[] data = new char[size];
         for(int i = 0; i < data.length; ++i) {
            data [i] = (char)(i % 0xFF);
         }
         return data;
      }
   }

   public static void main(String[] args) {
      CaliperMain.main(WithMarshaller.class, args);
   }

}
