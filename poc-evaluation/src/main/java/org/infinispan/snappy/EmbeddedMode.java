package org.infinispan.snappy;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.google.caliper.api.VmOptions;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

@VmOptions("-XX:-TieredCompilation")
public class EmbeddedMode {

   @Param
   boolean useMarshaller;

   @Param
   boolean storeAsBinary;

   @Param({"10", "1024", "32768", "65536"})
   int size;

   private org.infinispan.manager.EmbeddedCacheManager cacheManager;
   private Cache<Object, Object> cache;

   @BeforeExperiment
   void setUp() {
      GlobalConfigurationBuilder globalConfigurationBuilder = new GlobalConfigurationBuilder();
      globalConfigurationBuilder.globalJmxStatistics().allowDuplicateDomains(true);

      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
      if(useMarshaller) {
         globalConfigurationBuilder.serialization().marshaller(new SnappyMarshaller());
      }
      if(storeAsBinary) {
         configurationBuilder.storeAsBinary().storeKeysAsBinary(true).storeValuesAsBinary(true).enable();
      }

      cacheManager = new DefaultCacheManager(globalConfigurationBuilder.build(), configurationBuilder.build());
      cache = cacheManager.getCache();
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
