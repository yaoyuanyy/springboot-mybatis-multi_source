package com.skyler.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实现Mersenne Twister的随机算法
 * @User gong_wei_tao
 * @Date 2016/1/5 0005
 */
public class RandomUtils implements Serializable{
    static final long serialVersionUID = 1L;

    private volatile static RandomUtils randomUtils = null;
    private static final AtomicLong seedUnique = new AtomicLong(8682522807148012L);
    private final AtomicLong seed;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long mask = (1L << 48) - 1;
    private static final int NN = 312;
    private static final int MM = 156;
    private static final long MATRIX_A = 0xB5026F5AA96619E9L ;
    private static final long UM = 0xFFFFFFFF80000000L ;
    private static final long LM = 0x7FFFFFFF ;

    private static long []mt  = new long[NN];
    private static int mti = NN + 1;
    public static long mtRand() {
        return getSingleton().genRand64Int64();
    }

    public static long mtRand(long min, long max) {
        return getSingleton().genRand64Int64() % (max - min + 1) + min;
    }
    public static int mtRandInt(int min, int max) {
        if (min > Integer.MAX_VALUE || max > Integer.MAX_VALUE){
            return 0;
        }
        long result = getSingleton().genRand64Int64() % (max - min + 1) + min;
        return (int)result;
    }

    public static double genRand64Real1(){
        return (getSingleton().genRand64Int64() >> 11) * (1.0/9007199254740991.0);
    }
    public static double genRand64Real1(int decimal,double min){
        double result = DoubleUtil.round((getSingleton().genRand64Int64() >> 11) * (1.0/9007199254740991.0),decimal);
        if(result < min){
            result = DoubleUtil.add(min,result);
        }
        return result;
    }
    private long genRand64Int64(){
        int i;
        long x;
        long mag01[]={0L, MATRIX_A};
        if (mti >= NN) {
            if (mti == NN + 1){
                initGenRand64(seedUnique() ^ System.nanoTime());
            }
            for (i = 0;i < NN - MM;i++) {
                x = (mt[i] & UM) | (mt[i+1] & LM);
                mt[i] = mt[i+MM] ^ (x>>1) ^ mag01[(int)(x & 1L)];
            }
            for (;i<NN-1;i++) {
                x = (mt[i] & UM) | (mt[i+1] & LM);
                mt[i] = mt[i+(MM-NN)] ^ (x>>1) ^ mag01[(int)(x & 1L)];
            }
            x = (mt[NN-1] & UM)|(mt[0] & LM);
            mt[NN-1] = mt[MM-1] ^ (x>>1) ^ mag01[(int)(x & 1L)];
            mti = 0;
        }
        x = mt[mti++];
        x ^= (x >> 29) & 0x5555555555555555L;
        x ^= (x << 17) & 0x71D67FFFEDA60000L;
        x ^= (x << 37) & 0xFFF7EEE000000000L;
        x ^= (x >> 43);
        return x;
    }

    public RandomUtils(){
        this(seedUnique() ^ System.nanoTime());
    }
    public RandomUtils(long seed){
        this.seed = new AtomicLong(initialScramble(seed));
        initGenRand64(this.seed.get());
    }
    public RandomUtils(long initKey[]){
        this.seed = new AtomicLong(initialScramble(19650218L));
        initByArray64(initKey);
    }

    private long initialScramble(long seed) {
        return (seed ^ multiplier) & mask;
    }
    private static long seedUnique() {
        for (;;) {
            long current = seedUnique.get();
            long next = current * 181783497276652981L;
            if (seedUnique.compareAndSet(current, next))
                return next;
        }
    }
    private void initGenRand64(long seed){
        mt[0] = seed;
        for (mti=1; mti<NN; mti++){
            mt[mti] =  (6364136223846793005L * (mt[mti-1] ^ (mt[mti-1] >> 62)) + mti);
        }
    }

    private void initByArray64(long initKey[]){
        int keyLength = initKey.length;
        int i, j;
        int k;
        initGenRand64(19650218L);
        i=1; j=0;
        k = NN > keyLength ? NN : keyLength;
        for (; k > 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >> 62)) * 3935559000370003845L)) + initKey[j] + j;
            i++; j++;
            if (i>=NN) {
                mt[0] = mt[NN-1]; i=1;
            }
            if (j>=keyLength)
                j=0;
        }
        for (k = NN-1; k > 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >> 62)) * 2862933555777941757L)) - i;
            i++;
            if (i>=NN) {
                mt[0] = mt[NN-1]; i=1;
            }
        }
        mt[0] = 1L << 63;
    }
    public static RandomUtils getSingleton() {
        if (randomUtils == null) {
            synchronized (RandomUtils.class) {
                if (randomUtils == null) {
                    randomUtils = new RandomUtils();
                }
            }
        }
        return randomUtils;
    }
}
