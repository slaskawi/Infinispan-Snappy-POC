package org.infinispan.snappy;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.google.caliper.api.VmOptions;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

@VmOptions("-XX:-TieredCompilation")
public class ServerClientMode {

   @Param({"127.0.0.1", "10.3.9.0"})
   String address;

   @Param
   boolean useMarshaller;

   @Param({"10", "1024", "32768", "65536"})
   int size;

   private RemoteCacheManager cacheManager;
   private RemoteCache<Object, Object> cache;

   @BeforeExperiment
   void setUp() {
      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
      configurationBuilder.addServer().host("127.0.0.1");
      configurationBuilder.pingOnStartup(true);
      if(useMarshaller) {
         configurationBuilder.marshaller(SnappyMarshaller.class);
      }
      cacheManager = new RemoteCacheManager(configurationBuilder.build());
      cache = cacheManager.getCache();
      cache.clear();
   }

   @Benchmark
   void timePutEntries(int reps) {
      char[] data = Utils.INSTANCE.getRandomData(size);
      for (int i = 0; i < reps; i++) {
         data[0] = (char) (i % 0xFF);
         cache.put(data, data);
      }
   }

   @Benchmark
   void timeGetEntries(int reps) {
      char[] data = Utils.INSTANCE.getRandomData(size);
      for (int i = 0; i < reps; i++) {
         data[0] = (char) (i % 0xFF);
         cache.get(data);
      }
   }
}
