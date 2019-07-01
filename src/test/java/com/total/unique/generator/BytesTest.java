package com.total.unique.generator;

import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;

public class BytesTest {

  private static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int BASE = DIGITS.length();
  private static final double LIMIT = Math.pow(BASE, 10);

  @Test
  public void test() {

    final String hex = "4ebdaf8b1e5d1c0d1d1c9af8d84666fd2a7864354d94132f";
    final byte[] bytes = hexStringToByteArray(hex);
    System.out.println(bytes.length);
  }

  public static byte[] hexStringToByteArray(final String s) {
    final byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < b.length; i++) {
      final int index = i * 2;
      final int v = Integer.parseInt(s.substring(index, index + 2), 16);
      b[i] = (byte) v;
    }
    return b;
  }

  public static String cryptBC(final String data, final String key) throws Exception {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    byte[] input = data.getBytes();
    final byte[] keyBytes = key.getBytes();
    final SecretKeySpec skey = new SecretKeySpec(keyBytes, "DESede");
    final Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding", "BC");

    if (input.length % 8 != 0) {
      final byte[] padded = new byte[input.length + 8 - (input.length % 8)];
      System.arraycopy(input, 0, padded, 0, input.length);
      input = padded;
    }
    System.out.println("input : " + new String(input));
    cipher.init(Cipher.ENCRYPT_MODE, skey);
    final byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
    int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
    ctLength += cipher.doFinal(cipherText, ctLength);

    return new String(Base64.encodeBase64(cipherText));
  }
}
