package com.qxf.library;

import android.view.animation.LinearInterpolator;

// TODO 该类还有优化空间，比如【变化过程】的数据设定为动态，而不是现在的死水状态
class FluidInterpolator extends LinearInterpolator {

	private float[] rateTime;

	/**
	 * 变化过程
	 * 六个过程
	 * 1 变大
	 * 2 变小
	 * 3 变大一点点
	 * 4 变大
	 * 5 变小
	 * 6 变大
	 */
	private float[] changeValue = {1f, 0.0f, 0.5f, 0.4f, 0.0f, 0.1f, 0};

	/**
	 * 记录二元一次方程系数
	 */
	private float[][] coe;

	private int[] level = {-1, -1};

	FluidInterpolator() {

		rateTime = new float[6];
		rateTime[0] = 0 / 30.0f;
		rateTime[1] = 7 / 30.0f;
		rateTime[2] = 12 / 30.0f;
		rateTime[3] = 17 / 30.0f;
		rateTime[4] = 25 / 30.0f;
		rateTime[5] = 30 / 30.0f;

		float[] function1 = getFunction(0, 1f, rateTime[0], changeValue[1]);
		float[] function2 = getFunction(rateTime[0], changeValue[1], rateTime[1], changeValue[2]);
		float[] function3 = getFunction(rateTime[1], changeValue[2], rateTime[2], changeValue[3]);
		float[] function4 = getFunction(rateTime[2], changeValue[3], rateTime[3], changeValue[4]);
		float[] function5 = getFunction(rateTime[3], changeValue[4], rateTime[4], changeValue[5]);
		float[] function6 = getFunction(rateTime[4], changeValue[5], rateTime[5], changeValue[6]);

		coe = new float[][]{function1, function2, function3, function4, function5, function6};

	}

	@Override
	public float getInterpolation(float input) {

		// TODO 下面代码倒是没有什么问题，但是我看着不舒服（结构），有时间就修改一下

		float furtion;
		if (isArea(input, 0, rateTime[0])) {
			arr2Value(Type.STATE_1);
			furtion = getFurtion(coe[0], input);
		} else if (isArea(input, rateTime[0], rateTime[1])) {
			arr2Value(Type.STATE_2);
			furtion = getFurtion(coe[1], input);
		} else if (isArea(input, rateTime[1], rateTime[2])) {
			arr2Value(Type.STATE_3);
			furtion = getFurtion(coe[2], input);
		} else if (isArea(input, rateTime[2], rateTime[3])) {
			arr2Value(Type.STATE_4);
			furtion = getFurtion(coe[3], input);
		} else if (isArea(input, rateTime[3], rateTime[4])) {
			arr2Value(Type.STATE_5);
			furtion = getFurtion(coe[4], input);
		} else if (isArea(input, rateTime[4], rateTime[5])) {
			arr2Value(Type.STATE_6);
			furtion = getFurtion(coe[5], input);
		} else {
			throw new RuntimeException("这里不应该被执行的，被执行代表有错：区域判定错误，input 不属于给定的任何一个区间，input = " + input);
		}

		return furtion;
	}

	private void arr2Value(int l1) {
		level[0] = l1;
		level[1] = changeValue.length - l1;
	}

	/**
	 * 返回当前变化阶段
	 * 一共分为5个阶段
	 * <p>
	 * 阶段1：0.0～1.0 放大
	 * 阶段2：1.0～0.5 缩小
	 * 阶段3：0.5～1.1 放大 上移
	 * 阶段4：1.1～0.9 缩小
	 * 阶段5：0.9～1.0 放大
	 *
	 * @return 当前阶段
	 */
	int[] getLevel() {
		return level;
	}

	/**
	 * 将x带入函数中，计算y值大小
	 *
	 * @param coe 二元一次函数的系数，参考{@link #getFunction(float, float, float, float)}
	 * @param x   x值
	 * @return 得到的结果
	 */
	private float getFurtion(float[] coe, float x) {
		return coe[0] * x + coe[1];
	}

	/**
	 * 求二元一次方程的系数解，y = kx + b
	 *
	 * @param x1 第一个坐标的X坐标
	 * @param y1 第一个坐标的Y坐标
	 * @param x2 第二个坐标的X坐标
	 * @param y2 第二个坐标的Y坐标
	 * @return 以数组的形式返回两个数据，k = [0], b = [1]
	 */
	private float[] getFunction(float x1, float y1, float x2, float y2) {
		float k = (y2 - y1) / (x2 - x1);
		float b = y1 - k * x1;
		return new float[]{k, b};
	}

	/**
	 * 判定某个值是否在给定的两个数之间
	 *
	 * @param value 待判定的值
	 * @param num1  数字1
	 * @param num2  数字2
	 * @return 在这个区域则为true，反之为false
	 */
	private boolean isArea(float value, float num1, float num2) {

		if (value == num1 || value == num2) {
			return true;
		}

		float min;
		float max;

		if (num1 > num2) {
			min = num2;
			max = num1;
		} else {
			min = num1;
			max = num2;
		}

		return value > min && value < max;

	}

	/**
	 * 变化过程
	 * 六个过程
	 * 1 变大
	 * 2 变小
	 * 3 变大一点点
	 * 4 变大
	 * 5 变小
	 * 6 变大
	 */
	public class Type {
		static final int STATE_1 = 1;
		static final int STATE_2 = 2;
		static final int STATE_3 = 3;
		static final int STATE_4 = 4;
		static final int STATE_5 = 5;
		static final int STATE_6 = 6;
	}

}
