package com.skyler.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhulei
 */
@Slf4j
public class IpAddressUtils {
    public static final String IP_FORBIDDEN_COUNTRY = "伊朗,朝鲜,叙利亚,苏丹,孟加拉国,玻利维亚,厄瓜多尔,吉尔吉斯斯坦,美国,关岛,波多黎各,美属萨摩亚,北马里亚纳群岛,美属维尔京群岛";
    private static final List<String> IP_WHITE_LIST = new ArrayList<>();
    private static final int[] INDEX = new int[256];
    private static final long FILE_LENGTH = 10 * 1024 * 1024;
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
    public static boolean enableFileWatch = false;
    private static int offset;
    private static ByteBuffer dataBuffer;
    private static ByteBuffer indexBuffer;
    private static Long lastModifyTime = 0L;
    private static File ipFile;

    static {
        IP_WHITE_LIST.add("47.91.149.4");
    }

    static {
        IpAddressUtils.load("/config/17monipdb.dat");
    }

    public static String randomIp() {
        final Random r = new Random();
        final StringBuffer str = new StringBuffer();
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(0);

        return str.toString();
    }

    public static void load(final String filename) {
        InputStream fin = null;

        try {
            dataBuffer = ByteBuffer.allocate(Long.valueOf(FILE_LENGTH).intValue());
            fin = IpAddressUtils.class.getResourceAsStream(filename);
            int readBytesLength;
            final byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                dataBuffer.put(chunk, 0, readBytesLength);
            }
            dataBuffer.position(0);
            final int indexLength = dataBuffer.getInt();
            final byte[] indexBytes = new byte[indexLength];
            dataBuffer.get(indexBytes, 0, indexLength - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            offset = indexLength;

            int loop = 0;
            while (loop++ < 256) {
                INDEX[loop - 1] = indexBuffer.getInt();
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } catch (final Exception e) {
            log.error(" Ip  load ", e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final IOException e) {
            }
        }
        //        ipFile = new File(filename);
        //        load();
        //        if (enableFileWatch) {
        //            watch();
        //        }
    }

    public static void load(final String filename, final boolean strict) throws Exception {
        ipFile = new File(filename);
        if (strict) {
            final int contentLength = Long.valueOf(ipFile.length()).intValue();
            if (contentLength < 512 * 1024) {
                throw new Exception("ip data file error.");
            }
        }
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static String[] find(final String ip) {
        final int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
        final long ip2long_value = ip2long(ip);
        int start = INDEX[ip_prefix_value];
        final int max_comp_len = offset - 1028;
        long index_offset = -1;
        int index_length = -1;
        final byte b = 0;
        for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
            if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
                index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5),
                    indexBuffer.get(start + 4));
                index_length = 0xFF & indexBuffer.get(start + 7);
                break;
            }
        }

        byte[] areaBytes;

        REENTRANT_LOCK.lock();
        try {
            dataBuffer.position(offset + (int)index_offset - 1024);
            areaBytes = new byte[index_length];
            dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            REENTRANT_LOCK.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static void watch() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final long time = ipFile.lastModified();
                if (time > lastModifyTime) {
                    lastModifyTime = time;
                    load();
                }
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    private static void load() {
        lastModifyTime = ipFile.lastModified();
        FileInputStream fin = null;
        REENTRANT_LOCK.lock();
        try {
            dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
            fin = new FileInputStream(ipFile);
            int readBytesLength;
            final byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                dataBuffer.put(chunk, 0, readBytesLength);
            }
            dataBuffer.position(0);
            final int indexLength = dataBuffer.getInt();
            final byte[] indexBytes = new byte[indexLength];
            dataBuffer.get(indexBytes, 0, indexLength - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            offset = indexLength;

            int loop = 0;
            while (loop++ < 256) {
                INDEX[loop - 1] = indexBuffer.getInt();
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            REENTRANT_LOCK.unlock();
        }
    }

    private static long bytesToLong(final byte a, final byte b, final byte c, final byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static int str2Ip(final String ip) {
        final String[] ss = ip.split("\\.");
        final int a;
        final int b;
        final int c;
        final int d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static long ip2long(final String ip) {
        return int2long(str2Ip(ip));
    }

    private static long int2long(final int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }

    public static boolean checkUSA(final String ip) {
        try {
            if (StringUtils.isEmpty(ip)) {
                return false;
            }
            if (IP_WHITE_LIST.contains(ip.trim())) {
                return false;
            }
            if (System.currentTimeMillis() % 100 == 0) {
                log.error(" Ip checkUSA  [" + ip + "]");
            }
            final String[] values = IpAddressUtils.find(ip);
            if (values != null && values.length > 1 && !StringUtils.isEmpty(values[0]) && "美国".equalsIgnoreCase(
                values[0])) {
                return true;
            }
        } catch (final Exception e) {
            log.error(" Ip checkUSA error ip[" + ip + "]", e);
        }
        return false;
    }

    /**
     * 禁止用户
     *
     * @param ip
     * @return
     */
    public static boolean checkForbidden(final String ip) {
        try {
            if (StringUtils.isEmpty(ip)) {
                return false;
            }
            if (IP_WHITE_LIST.contains(ip.trim())) {
                return false;
            }
            if (System.currentTimeMillis() % 100 == 0) {
                log.error(" Ip checkUSA  [" + ip + "]");
            }
            final String[] values = IpAddressUtils.find(ip);
            if (values != null && values.length > 1 && !StringUtils.isEmpty(values[0]) && IP_FORBIDDEN_COUNTRY.indexOf
                (values[0].trim()) != -1) {
                return true;
            }
        } catch (final Exception e) {
            log.error(" Ip checkUSA error ip[" + ip + "]", e);
        }
        return false;
    }

    public static boolean check(final String ip, final String country) {
        return check(ip, country, null, null);
    }

    public static boolean check(final String ip, final String country, final String province) {
        return check(ip, country, province, null);
    }

    /**
     * 检查ip是否来自指定的区域
     *
     * @param ip       需要检查的IP地址
     * @param country  国家
     * @param province 省份，如果为null则以该国家为区域
     * @param city     城市，如果为null则以该省份为区域
     * @return
     */
    public static boolean check(final String ip, final String country, final String province, final String city) {
        try {
            if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(country)) {
                return false;
            }
            if (IP_WHITE_LIST.contains(ip.trim())) {
                return false;
            }
            if (System.currentTimeMillis() % 100 == 0) {
                log.error(" Ip check  [" + ip + "]");
            }
            final String[] values = IpAddressUtils.find(ip);

            if (values.length > 0 && !StringUtils.isEmpty(values[0]) && country.equalsIgnoreCase(values[0])) {
                if (StringUtils.isEmpty(province)) {
                    return true;
                }
            }
            if (!StringUtils.isEmpty(province) && values.length > 1
                && !StringUtils.isEmpty(values[1]) && province.equalsIgnoreCase(values[1])) {
                if (StringUtils.isEmpty(city)) {
                    return true;
                }
            }
            if (!StringUtils.isEmpty(city) && values.length > 2
                && !StringUtils.isEmpty(values[2]) && city.equalsIgnoreCase(values[2])) {
                return true;
            }
        } catch (final Exception e) {
            log.error(" Ip checkUSA error ip[" + ip + "]", e);
        }
        return false;
    }

}
