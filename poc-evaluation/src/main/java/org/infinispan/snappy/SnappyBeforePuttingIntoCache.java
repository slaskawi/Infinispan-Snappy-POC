package org.infinispan.snappy;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.google.caliper.api.VmOptions;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.xerial.snappy.Snappy;

import java.io.IOException;

@VmOptions({"-XX:-TieredCompilation", "-Xmx4G"})
public class SnappyBeforePuttingIntoCache {

   @Param
   private boolean useSnappy;

   @Param({"10", "1024", "16384"})
   int size;

   private org.infinispan.manager.EmbeddedCacheManager cacheManager;
   private Cache<Object, Object> cache;

   @BeforeExperiment
   void setUp() {
      GlobalConfigurationBuilder globalConfigurationBuilder = new GlobalConfigurationBuilder();
      globalConfigurationBuilder.globalJmxStatistics().allowDuplicateDomains(true);

      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

      cacheManager = new DefaultCacheManager(globalConfigurationBuilder.build(), configurationBuilder.build());
      cache = cacheManager.getCache();
   }

   @Benchmark
   void timePutEntries(int reps) throws IOException {
      char[] data = Utils.INSTANCE.getRandomData(size);
      for (int i = 0; i < reps; i++) {
         data[0] = (char) (i % 0xFF);
         if(useSnappy) {
            cache.put(Snappy.compress(data), Snappy.compress(data));
         } else {
            cache.put(data, data);
         }
      }
   }

   @Benchmark
   void timeGetEntries(int reps) throws IOException {
      char[] data = Utils.INSTANCE.getRandomData(size);
      for (int i = 0; i < reps; i++) {
         data[0] = (char) (i % 0xFF);
         if(useSnappy) {
            byte[] dataToBeUncompressed = (byte[]) cache.get(data);
            if(dataToBeUncompressed != null) {
               Snappy.uncompress(dataToBeUncompressed);
            }
         } else {
            cache.get(data);
         }

      }
   }

}
