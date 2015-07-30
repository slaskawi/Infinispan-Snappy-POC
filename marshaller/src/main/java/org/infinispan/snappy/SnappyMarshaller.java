package org.infinispan.snappy;

import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.BufferSizePredictor;
import org.infinispan.commons.marshall.jboss.AbstractJBossMarshaller;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SnappyMarshaller extends AbstractJBossMarshaller {

   @Override
   public BufferSizePredictor getBufferSizePredictor(Object o) {
      System.out.println("getBufferSizePredictor with object");
      return super.getBufferSizePredictor(o);
   }

   @Override
   public ByteBuffer objectToBuffer(Object obj) throws IOException, InterruptedException {
      byte[] uncompressedData = super.objectToBuffer(obj).getBuf();
      byte[] compressedData = Snappy.compress(uncompressedData);
      System.out.println("objectToBuffer with object, ratio: " + (uncompressedData.length / compressedData.length));
      return new ByteBufferImpl(compressedData, 0, compressedData.length);
   }

   @Override
   public byte[] objectToByteBuffer(Object o) throws IOException, InterruptedException {
      byte[] uncompressedData = super.objectToByteBuffer(o);
      byte[] compressedData = Snappy.compress(uncompressedData);
      System.out.println("objectToByteBuffer with object, ratio: " + (uncompressedData.length / compressedData.length));
      return compressedData;
   }

   @Override
   public byte[] objectToByteBuffer(Object obj, int estimatedSize) throws IOException, InterruptedException {
      byte[] uncompressedData = super.objectToByteBuffer(obj, estimatedSize);
      byte[] compressedData = Snappy.compress(uncompressedData);
      System.out.println("Compressed data: " + Arrays.toString(compressedData));
      System.out.println("objectToByteBuffer with object and int, ratio: " + (uncompressedData.length / compressedData.length));
      return uncompressedData;
   }

   @Override
   public Object objectFromByteBuffer(byte[] buf) throws IOException, ClassNotFoundException {
      System.out.println("Uncompressing data: " + Arrays.toString(buf));
      byte [] compressedData = buf;
      byte [] uncompressedData = Snappy.uncompress(compressedData);
      System.out.println("objectFromByteBuffer with byte array, ratio: " + (uncompressedData.length / compressedData.length));
      return super.objectFromByteBuffer(buf);
   }

   @Override
   public Object objectFromInputStream(InputStream inputStream) throws IOException, ClassNotFoundException {
      throw new UnsupportedOperationException("Streaming is not supported");
   }
}
