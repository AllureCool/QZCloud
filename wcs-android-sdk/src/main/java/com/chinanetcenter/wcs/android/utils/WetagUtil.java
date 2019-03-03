package com.chinanetcenter.wcs.android.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 文件hash/etag工具
 * Created by xiexb on 2014/5/30.
 */
public class WetagUtil {
    private static final int BLOCK_BITS = 22;
    private static final int BLOCK_SIZE = 1 << BLOCK_BITS;//2^22 = 4M
    private static final byte BYTE_LOW_4 = 0x16;//小于等于4M的文件在最前面拼上单个字节，值为0x16
    private static final byte BYTE_OVER_4 = (byte) 0x96;//大于4M的文件在最前面拼上单个字节，值为0x96

    /**
     * 计算文件块数，4M分块
     *
     * @param fileLength
     * @return
     */
    private static long blockCount(long fileLength) {
        return ((fileLength + (BLOCK_SIZE - 1)) >> BLOCK_BITS);
    }

    /**
     * 读取指定文件块数据Sha1
     *
     * @param fis
     * @return
     */
    private static MessageDigest calSha1(BufferedInputStream fis) {
        MessageDigest sha1 = null;
        try {
            byte[] buffer = new byte[1024];
            int numRead = 0;
            int total = 0;
            sha1 = MessageDigest.getInstance("SHA-1");
            while ((numRead = fis.read(buffer)) > 0) {
                sha1.update(buffer, 0, numRead);
                total += numRead;
                if (total >= BLOCK_SIZE) {//每次最多读入4M
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String getEtagHash(File file) {
        String etagHash = null;
        BufferedInputStream fis = null;
        try {
            if (file.exists()) {
                byte[] ret = new byte[21];
                long blockCount = blockCount(file.length());
                fis = new BufferedInputStream(new FileInputStream(file));
                if (blockCount <= 1) { // 文件块数小于等于1块
                    MessageDigest sha1 = calSha1(fis);
                    if (null != sha1) {
                        byte[] input = sha1.digest();
                        ret[0] = BYTE_LOW_4;
                        for (int i = 0; i < 20; ++i) {//SHA1算法位20字节
                            ret[i + 1] = input[i];
                        }
                    }
                } else {//将所有sha1值按切块顺序拼接
                    byte[] rec = new byte[(int) blockCount * 20];
                    ret[0] = BYTE_OVER_4;
                    int i, cnt = 0;
                    for (i = 0; i < blockCount; i++) {//每块文件分别计算sha1
                        MessageDigest sha1 = calSha1(fis);
                        if (null != sha1) {
                            byte[] tmp = sha1.digest();
                            for (int j = 0; j < 20; j++) {
                                rec[cnt++] = tmp[j];
                            }
                        }
                    }
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");//对拼接好的数据做再做sha1计算
                    sha1.update(rec, 0, (int) blockCount * 20);
                    byte[] tmp = sha1.digest();
                    for (i = 0; i < 20; ++i) {//在最前面拼上单个字节，值为0x96
                        ret[i + 1] = tmp[i];
                    }
                }
                etagHash = EncodeUtils.urlsafeEncodeString(ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return etagHash;
    }

    /**
     * 获取hash/etag，用于对未分块本地文件计算hash值
     *
     * @param filePath 文件物理路径
     * @param fileName 文件名
     * @return
     */
    public static String getEtagHash(String filePath, String fileName) {
        return getEtagHash(new File(filePath, fileName));
    }

    public static String getEtagHash(String filePath) {
        return getEtagHash(new File(filePath));
    }

    /**
     * 获取hash/etag，用于对未分块文件计算hash值
     *
     * @param fileInputStream 文件输入流
     * @param fileLength      文件大小
     * @return
     */
    public static String getEtagHash(InputStream fileInputStream, long fileLength) {
        String etagHash = null;
        BufferedInputStream fis = null;
        try {
            byte[] ret = new byte[21];
            long blockCount = blockCount(fileLength);
            fis = new BufferedInputStream(fileInputStream);
            if (blockCount <= 1) { // 文件块数小于等于1块
                MessageDigest sha1 = calSha1(fis);
                if (null != sha1) {
                    byte[] input = sha1.digest();
                    ret[0] = BYTE_LOW_4;
                    for (int i = 0; i < 20; ++i) {//SHA1算法位20字节
                        ret[i + 1] = input[i];
                    }
                }
            } else {//将所有sha1值按切块顺序拼接
                byte[] rec = new byte[(int) blockCount * 20];
                ret[0] = BYTE_OVER_4;
                int i, cnt = 0;
                for (i = 0; i < blockCount; i++) {//每块文件分别计算sha1
                    MessageDigest sha1 = calSha1(fis);
                    if (null != sha1) {
                        byte[] tmp = sha1.digest();
                        for (int j = 0; j < 20; j++) {
                            rec[cnt++] = tmp[j];
                        }
                    }
                }
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");//对拼接好的数据做再做sha1计算
                sha1.update(rec, 0, (int) blockCount * 20);
                byte[] tmp = sha1.digest();
                for (i = 0; i < 20; ++i) {//在最前面拼上单个字节，值为0x96
                    ret[i + 1] = tmp[i];
                }
            }
            etagHash = EncodeUtils.urlsafeEncodeString(ret);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return etagHash;
    }

    /**
     * 计算指定data的Sha1
     *
     * @param data byte字节数组
     * @return
     */
    private static MessageDigest calSha1(byte[] data) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha1;
    }

    /**
     * 获取hash/etag，用于对已经分块好的文件分别计算每块hash值
     *
     * @param data 文件的byte字节数组
     * @return
     */
    public static String getEtagHash(byte[] data) {
        String etagHash = null;
        try {
            byte[] ret = new byte[21];
            MessageDigest sha1 = calSha1(data);
            if (null != sha1) {
                byte[] input = sha1.digest();
                ret[0] = BYTE_LOW_4;
                for (int i = 0; i < 20; ++i) {//SHA1算法位20字节
                    ret[i + 1] = input[i];
                }
            }
            etagHash = EncodeUtils.urlsafeEncodeString(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return etagHash;
    }
}
