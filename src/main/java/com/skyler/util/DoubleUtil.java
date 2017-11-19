package com.skyler.util;

import java.math.BigDecimal;

public class DoubleUtil {

	/**
	 * 人民币/美元 返回4位
	 * 
	 * @param value
	 * @return
	 */
	public static double cny(double cnyValue) {
		return round(cnyValue, 4);
	}

	/**
	 * 比特币返回8位
	 * 
	 * @param value
	 * @return
	 */
	public static double btc(double btcValue) {
		return round(btcValue, 8);
	}

	/**
	 * 对double数据进行取精度. 返回最大的（最接近正无穷大）double 值，该值小于等于参数，并等于某个整数。
	 * 
	 * @param value
	 *            double数据.
	 * @param scale
	 *            精度位数(保留的小数位数).
	 * @return 精度计算后的数据.
	 */
	public static double round(double value, int scale) {
		int n = (int) Math.pow(10, scale);
		double result = (double) divide(Math.floor(multiply(value, n)), n, scale);
		return result;
	}

	/**
	 * double 相加
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double add(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}

	/**
	 * double 相减
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double subtract(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}

    /**
     * double 相减
     *
     * @param b1
     * @param d1
     * @return
     */
    public static BigDecimal subtract(BigDecimal b1, double d1) {
        BigDecimal bd2 = new BigDecimal(Double.toString(d1));
        return b1.subtract(bd2);
    }

	/**
	 * double 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double multiply(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.multiply(bd2).doubleValue();
	}

    /**
     * double 乘法
     *
     * @param d1
     * @param b1
     * @return
     */
    public static double multiply(double d1, BigDecimal b1) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        return bd1.multiply(b1).doubleValue();
    }

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static double divide(double d1, double d2, int scale) {
		// 当然在此之前，你要判断分母是否为0，
		// 为0你可以根据实际需求做相应的处理

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * double 除法 向正无穷取值
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static double divideUp(double d1, double d2, int scale) {
		// 当然在此之前，你要判断分母是否为0，
		// 为0你可以根据实际需求做相应的处理

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_CEILING).doubleValue();
	}

	/**
	 * 小数向上进位
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double roundUp(double value, int scale) {
		int n = (int) Math.pow(10, scale);
		double result = (double) divide(Math.ceil(multiply(value, n)), n, scale);
		return result;
	}

	public static double floor(double value, int scale) {
		int n = (int) Math.pow(10, scale);
		return divide(Math.floor(multiply(value, n)), n, scale);
	}

	public static double ceil(double value, int scale) {
		int n = (int) Math.pow(10, scale);
		return divide(Math.ceil(multiply(value, n)), n, scale);
	}

	public static void main(String[] args) {
		// System.out.println(DoubleUtil.round(-114.8612155555555553, 4));
		// System.out.println(DoubleUtil.round(114.8612955555555553, 4));
		// System.out.println(DoubleUtil.down(144.8612355555555553, 4));
		// System.out.println(DoubleUtil.roundUp(1114.861203, 4));
		// System.out.println(DoubleUtil.roundUp(1114.861223231, 6));
	}
}
