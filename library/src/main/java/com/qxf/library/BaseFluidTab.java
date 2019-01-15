package com.qxf.library;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

abstract class BaseFluidTab extends View {

	/**
	 * 屏幕分辨率
	 */
	protected Point screenPoint;

	/**
	 * view高度
	 */
	protected int height;

	public BaseFluidTab(Context context, AttributeSet attrs) {
		super(context, attrs);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = wm.getDefaultDisplay();
		screenPoint = new Point();
		defaultDisplay.getSize(screenPoint);

		height = screenPoint.y / 9;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	protected int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = screenPoint.x;
		if (specMode == MeasureSpec.AT_MOST) {// wrap_content
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {// match_parent
			result = specSize;
		}
		return result;
	}

	protected int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = height;
		if (specMode == MeasureSpec.AT_MOST) {
			result = height;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		return result;
	}

}
