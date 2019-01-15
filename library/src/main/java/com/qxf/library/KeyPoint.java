package com.qxf.library;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.util.Arrays;

class KeyPoint {

	private Point[] keyPoints;

	static final int TYPE_KEY_LEFT_1 = 0;
	static final int TYPE_KEY_LEFT_2 = 1;
	static final int TYPE_KEY_LEFT_3 = 2;
	static final int TYPE_KEY_RIGHT_1 = 3;
	static final int TYPE_KEY_RIGHT_2 = 4;
	static final int TYPE_KEY_RIGHT_3 = 5;
	static final int TYPE_KEY_CIRCLE = 6;// 贝塞尔圆


	public KeyPoint(Point[] keyPoints) {
		this.keyPoints = keyPoints;
	}

	KeyPoint(
			int left1X, int left1Y,
			int left2X, int left2Y,
			int left3X, int left3Y,
			int right1X, int right1Y,
			int right2X, int right2Y,
			int right3X, int right3Y,
			int circleX, int circleY
	) {

		keyPoints = new Point[7];

		keyPoints[0] = new Point(left1X, left1Y);
		keyPoints[1] = new Point(left2X, left2Y);
		keyPoints[2] = new Point(left3X, left3Y);
		keyPoints[3] = new Point(right1X, right1Y);
		keyPoints[4] = new Point(right2X, right2Y);
		keyPoints[5] = new Point(right3X, right3Y);
		keyPoints[6] = new Point(circleX, circleY);

	}

	KeyPoint offsetX(int offsetX) {
		for (Point keyPoint : keyPoints) {
			keyPoint.x += offsetX;
		}
		return this;
	}

	Point getKeyPoint(int type) {
		if (type > keyPoints.length - 1) {
			throw new ArrayIndexOutOfBoundsException("数组越界，length = " + keyPoints.length + " index = " + type);
		}
		return keyPoints[type];
	}

	@NonNull
	@Override
	public String toString() {
		return "KeyPoint{" +
				"keyPoints=" + Arrays.toString(keyPoints) +
				'}';
	}
}
