package com.skyler.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author: zhangyu
 * @date: 2017/8/1
 */
@Slf4j
public class ValidateCodeUtils {
    public static String directRandomCode() {
        //定义矩阵char数组
        final char[] chars = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        //随机码
        final StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            final Random random = new Random();
            final int a = random.nextInt(9);
            final String code = String.valueOf(chars[a]);
            sbf.append(code);
        }
        log.info("验证码:{}", sbf.toString());
        return sbf.toString();
    }

    public static int getRandomInt() {
        return (int)((Math.random() * 9 + 1) * 100000);
    }

}
