package org.infinispan.snappy;


import com.google.caliper.runner.CaliperMain;

public class TestSuite {

   public static void main(String[] args) {
      CaliperMain.main(ServerClientMode.class, args);
      CaliperMain.main(EmbeddedMode.class, args);
      CaliperMain.main(SnappyBeforePuttingIntoCache.class, args);
   }

}
