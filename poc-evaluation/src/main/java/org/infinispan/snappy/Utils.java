package org.infinispan.snappy;

public enum Utils {
   INSTANCE;

   public char[] getRandomData(int size) {
      char[] data = new char[size];
      for(int i = 0; i < data.length; ++i) {
         data [i] = (char)(i % 0xFF);
      }
      return data;
   }
}
