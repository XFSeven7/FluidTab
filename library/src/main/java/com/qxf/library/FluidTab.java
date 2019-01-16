package com.qxf.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class FluidTab extends BaseFluidTab {

	private static final String TAG = "FluidTab";

	/**
	 * 绘制曲线的path
	 */
	Path path;

	/**
	 * 绘制透明背景
	 */
	private Paint alphaPaint;

	private Paint pathPaint;

	/**
	 * 背景颜色
	 */
	private int backgroundColor = Color.parseColor("#F9D4BB");

	/**
	 * 绘制辅助线或点的专用paint，正式使用该类的时候，不应该使用该画笔
	 */
	private Paint helpPoint;

	/**
	 * 绘制文字的画笔
	 */
	private Paint textPaint;

	/**
	 * 文字颜色
	 */
	private int textColor = Color.parseColor("#664E4C");


	/**
	 * 动画控制
	 */
	private ValueAnimator valueAnimator;

	/**
	 * 动画过程记录变量
	 */
	private float changeValue = 1f;

	/**
	 * 动画时间
	 */
	private long duration = 700;

	/**
	 * 含有的标签页的个数
	 */
	private int size = 4;

	/**
	 * item要被显示的内容
	 */
	private Tab[] tabs;

	/**
	 * 当前被选中的item
	 */
	private int currentItem = 0;

	/**
	 * 上一个被选中的item
	 */
	private int lastItem = 0;

	/**
	 * 绘制每个tab的paint
	 */
	private Paint tabPaint;

	/**
	 * tab的颜色
	 */
	private int tabColor = Color.parseColor("#664E4C");

	/**
	 * 每个tab的宽度
	 */
	private int tabWidth;

	/**
	 * item的圆半径
	 */
	int radius;

	/**
	 * tab宽度的一半
	 */
	int baseX;
	/**
	 * view的基本线，指的是穿过item的线
	 */
	int baseY;

	/**
	 * 记录变换程度
	 */
	private int[] stage = {1, 6};

	/**
	 * 是否是首次使用该view
	 */
	private boolean isFirstIn = true;

	/**
	 * 动画的插值器
	 */
	private FluidInterpolator fluidInterpolator;

	/**
	 * tab底部被拉伸的区域
	 */
	private RectF upRect;

	/**
	 * 动画结束的时间的百分比
	 */
	private float lastTimeRate;

	/**
	 * fragment控制器
	 */
	private FragmentManager fragmentManager;

	/**
	 * fragment放置的容器ID
	 */
	private int id;

	/**
	 * 点击监听
	 */
	private onItemClickListener onItemClickListener;

	public FluidTab(Context context, AttributeSet attrs) {
		super(context, attrs);

		initTool();
		initThreshold();

		fluidInterpolator = new FluidInterpolator();

		valueAnimator = new ValueAnimator();
		valueAnimator.setFloatValues(0f, 1);
		valueAnimator.setInterpolator(fluidInterpolator);
		valueAnimator.setDuration(duration);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				stage = fluidInterpolator.getLevel();
				changeValue = (float) animation.getAnimatedValue();
				if (changeValue > 0.5) {
					if (onItemClickListener != null) {
						onItemClickListener.itemClick(currentItem);
					}

				}
				invalidate();
			}
		});
	}

	/**
	 * 默认选中第几个item
	 *
	 * @param i 从0开始计算
	 */
	public void defaultItem(int i) {
		if (i >= 0 && i < tabs.length) {
			currentItem = i;
			lastItem = i;
			invalidate();
		}
	}

	/**
	 * 初始化阈值
	 */
	private void initThreshold() {

		if (tabs != null && tabs.length > 0) {
			tabWidth = screenPoint.x / tabs.length;
		} else {
			return;
		}

		baseX = tabWidth / 2;
		baseY = height / 3;

		radius = (int) (0.8f * height / 3);

		currentItem = 0;
		lastItem = 0;

		height = screenPoint.y / 9;

	}

	/**
	 * 初始化绘制工具
	 */
	private void initTool() {

		path = new Path();

		pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pathPaint.setColor(backgroundColor);

		helpPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
		helpPoint.setStrokeCap(Paint.Cap.ROUND);
		helpPoint.setColor(Color.RED);
		helpPoint.setStrokeWidth(20);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(textColor);
		textPaint.setFakeBoldText(true);

		tabPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tabPaint.setColor(tabColor);

		alphaPaint = new Paint();
		alphaPaint.setAlpha(0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

			// 手指下按
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				int tempLastItem = currentItem;
				int tempCurrentItem = touchItem(x);

				if (tempCurrentItem != tempLastItem) {
					lastItem = tempLastItem;
					currentItem = tempCurrentItem;
					valueAnimator.start();
					if (fragmentManager != null) {
						switchFragment(currentItem);
					}
				}

				break;

		}
		return true;
	}

	/**
	 * 根据触摸区域判断当前点击的是哪一个item
	 *
	 * @param x 触摸的X坐标
	 * @return 当前区域，从0开始计算
	 */
	private int touchItem(float x) {
		if (tabs == null || tabs.length <= 0) {
			throw new RuntimeException("tabs不合理，先调用setTabs(Tab[] tabs) tabs == null =>" + (tabs == null));
		}
		int areaWidth = screenPoint.x / tabs.length;
		return (int) (x / areaWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawAlpha(canvas);
		drawColorTie(canvas);
		drawItem(canvas);
	}

	private void drawAlpha(Canvas canvas) {
		Rect rect = new Rect(0, 0, screenPoint.x, height);
		canvas.drawRect(rect, alphaPaint);
	}

	/**
	 * 绘制底部tab区域带
	 *
	 * @param canvas 画布
	 */
	private void drawColorTie(Canvas canvas) {
		RectF rectF = new RectF(0, height / 3, screenPoint.x, height);
		path.addRect(rectF, Path.Direction.CW);
		canvas.drawPath(path, pathPaint);
	}

	/**
	 * 绘制已经选中的Item
	 * 控制消失和出现
	 *
	 * @param canvas the canvas
	 */
	private void drawItem(Canvas canvas) {

		// 出现
		KeyPoint keyPoints = getKeyPoints(true);
		drawCell(keyPoints, tabWidth * currentItem, tabWidth * (1 + currentItem), true, canvas);

		// 消失
		if (isFirstIn) {
			isFirstIn = false;
			KeyPoint keyPoints1 = getKeyPoints(false);
			drawCell(keyPoints1, tabWidth * lastItem, tabWidth * (1 + lastItem), false, canvas);
		} else if (valueAnimator.isRunning()) {
			KeyPoint keyPoints1 = getKeyPoints(false);
			drawCell(keyPoints1, tabWidth * lastItem, tabWidth * (1 + lastItem), false, canvas);
		}

	}

	/**
	 * 绘制单个item
	 * <p>
	 * 由于每一个item都有一个圆形用来承载icon，这个圆形将会被外围的曲线包裹，这里主要就是绘制外围的曲线
	 *
	 * @param keyPoint 关键点（主要是贝塞尔曲线的控制点和圆心两大块）
	 * @param startX   起始点
	 * @param endX     终点
	 * @param check    是否被选中
	 * @param canvas   画布
	 */
	private void drawCell(KeyPoint keyPoint, int startX, int endX, boolean check, Canvas canvas) {

		if (keyPoint == null) {
			return;
		}

		keyPoint = keyPoint.offsetX(startX);

		Point keyLeft3 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_LEFT_3);
		Point keyLeft2 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_LEFT_2);
		Point keyLeft1 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_LEFT_1);
		Point keyRight1 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_RIGHT_1);
		Point keyRight2 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_RIGHT_2);
		Point keyRight3 = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_RIGHT_3);
		Point keyCircle = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_CIRCLE);

		// 绘制path
		path.moveTo(startX, keyLeft3.y);
		path.lineTo(keyLeft3.x, keyLeft3.y);
		path.quadTo(keyLeft2.x, keyLeft2.y, keyLeft1.x, keyLeft1.y);
		path.lineTo(keyRight1.x, keyRight1.y);
		path.quadTo(keyRight2.x, keyRight2.y, keyRight3.x, keyRight3.y);
		path.lineTo(endX, keyRight3.y);

		// 外围贝塞尔曲线
		canvas.drawCircle(keyCircle.x, keyCircle.y, radius, pathPaint);
		canvas.drawPath(path, pathPaint);

		path.reset();

		drawTab(keyPoint, check, canvas);

		// region 绘制辅助线 方便分析
//		if (check) {
//			helpPoint.setColor(Color.RED);
//			canvas.drawPoint(keyLeft1.x, keyLeft1.y, helpPoint);
//			canvas.drawPoint(keyRight1.x, keyRight1.y, helpPoint);
//			helpPoint.setColor(Color.RED);
//			canvas.drawPoint(keyLeft3.x, keyLeft3.y, helpPoint);
//			canvas.drawPoint(keyRight3.x, keyRight3.y, helpPoint);
//			helpPoint.setColor(Color.GREEN);
//			canvas.drawPoint(keyLeft2.x, keyLeft2.y, helpPoint);
//			canvas.drawPoint(keyRight2.x, keyRight2.y, helpPoint);
//		}
		// endregion

	}

	/**
	 * 绘制具体的item的细节，比如icon文字，动画管理等等
	 *
	 * @param keyPoint 关键点
	 * @param check    是否被选中
	 * @param canvas   the canvas
	 */
	private void drawTab(KeyPoint keyPoint, boolean check, Canvas canvas) {

		if (tabs == null) {
			return;
		}

		Point circlePoint = keyPoint.getKeyPoint(KeyPoint.TYPE_KEY_CIRCLE);

		// 当前要被操作的tab页
		Tab tab;
		if (check) {
			tab = tabs[currentItem];
		} else {
			tab = tabs[lastItem];
		}

		// 内圆半径
		int insideCircleRadius = (int) (radius / 1.1f * (1 - changeValue));

		Path path = new Path();

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), tab.getPic());

		Rect src = new Rect();
		src.left = 0;
		src.top = 0;
		src.right = bitmap.getWidth();
		src.bottom = bitmap.getHeight();

		int offsetX = (int) (insideCircleRadius * Math.sin(Math.PI / 4));

		RectF dst = new RectF(
				circlePoint.x - offsetX,
				circlePoint.y - offsetX,
				circlePoint.x + offsetX,
				circlePoint.y + offsetX);

		// 内圆区域
		RectF arcRectF = new RectF(circlePoint.x - insideCircleRadius, circlePoint.y - insideCircleRadius,
				circlePoint.x + insideCircleRadius, circlePoint.y + insideCircleRadius);

		// 绘制内圆
		canvas.drawCircle(circlePoint.x, circlePoint.y, insideCircleRadius, tabPaint);

		if (check) {
			if (stage[0] == FluidInterpolator.Type.STATE_2) {
				upRect = arcRectF;
			} else if (stage[0] == FluidInterpolator.Type.STATE_3) { // 上移 changeValue[0.5~0] 变大一点点

				RectF temp = upRect;

				float v = (circlePoint.y + (temp.top + temp.bottom) / 2) / 2;

				path.moveTo((temp.left + temp.right) / 2 - temp.width() / 2, (temp.top + temp.bottom) / 2);
				path.quadTo(circlePoint.x, v, circlePoint.x - insideCircleRadius, circlePoint.y);

				path.lineTo(circlePoint.x + insideCircleRadius, circlePoint.y);
				path.quadTo(circlePoint.x, v, (temp.left + temp.right) / 2 + temp.width() / 2, (temp.top + temp.bottom) / 2);
				path.lineTo((temp.left + temp.right) / 2 - temp.width() / 2, (temp.top + temp.bottom) / 2);

				canvas.drawCircle((temp.left + temp.right) / 2, (temp.top + temp.bottom) / 2, temp.width() / 2, tabPaint);

				lastTimeRate = 1 - (float) (valueAnimator.getCurrentPlayTime() * 1.0 / valueAnimator.getDuration() * 1.0);

			} else if (stage[0] >= FluidInterpolator.Type.STATE_4) {

				float nowTime = 1 - (float) (valueAnimator.getCurrentPlayTime() * 1.0 / valueAnimator.getDuration() * 1.0);

				float v1 = nowTime - lastTimeRate;

				RectF temp = upRect;

				// 下面那个圆的圆心的Y坐标
				float v2 = (upRect.bottom + upRect.top) / 2;
				// 上下两个圆的圆心距
				float distance = v2 - circlePoint.y;
				// 每次一个增量多少
				float v3 = distance * v1;

				if (v3 < 0) {
					temp.offset(0, v3 * 4);
				}

				float v = (circlePoint.y + (temp.top + temp.bottom) / 2) / 2;

				path.moveTo((temp.left + temp.right) / 2 - temp.width() / 2, (temp.top + temp.bottom) / 2);
				path.quadTo(circlePoint.x, v, circlePoint.x - insideCircleRadius, circlePoint.y);

				path.lineTo(circlePoint.x + insideCircleRadius, circlePoint.y);
				path.quadTo(circlePoint.x, v, (temp.left + temp.right) / 2 + temp.width() / 2, (temp.top + temp.bottom) / 2);
				path.lineTo((temp.left + temp.right) / 2 - temp.width() / 2, (temp.top + temp.bottom) / 2);

				canvas.drawCircle((temp.left + temp.right) / 2, (temp.top + temp.bottom) / 2, temp.width() / 2, tabPaint);

				// 绘制文字
				textPaint.setTextSize(insideCircleRadius / 2);
				canvas.drawText(tab.getText(), circlePoint.x, (float) ((circlePoint.y + insideCircleRadius + height) / 2), textPaint);

				lastTimeRate = nowTime;

			}
		}

		// 双重绘制，加粗字体
		if (!valueAnimator.isRunning()) {
			textPaint.setTextSize(insideCircleRadius / 2);
			canvas.drawText(tab.getText(), circlePoint.x, (float) ((circlePoint.y + insideCircleRadius + height) / 2), textPaint);
		}

		canvas.drawPath(path, tabPaint);

		canvas.drawBitmap(bitmap, src, dst, null);

	}

	/**
	 * 获取外围曲线的关键点，通过这几个点来控制曲线的形状
	 *
	 * @param check 是否被点击
	 * @return 关键点集合
	 */
	private KeyPoint getKeyPoints(boolean check) {

		// check = true  -> 当前选中的item
		// check = false -> 当前取消的item
		if (!check) {
			changeValue = (1 - changeValue);
		}

		// 圆心的X、Y坐标
		int circleX = baseX;
		int circleY = 0;
		if (check) {
			if (stage[0] == FluidInterpolator.Type.STATE_1) { // 放大
				circleY = (baseY + height) / 2;
			} else if (stage[0] == FluidInterpolator.Type.STATE_2) { // 缩小
				circleY = (baseY + height) / 2;
			} else if (stage[0] == FluidInterpolator.Type.STATE_3) { // 上移 changeValue[0.5~0] 变大一点点
				circleY = (int) ((baseY + height) / 2 - ((baseY + height) / 2 - baseY) * (1 - changeValue * 2) * 5);
			} else if (stage[0] == FluidInterpolator.Type.STATE_4) { // 变大
				circleY = baseY;
			} else if (stage[0] == FluidInterpolator.Type.STATE_5) { // 缩小
				circleY = baseY;
			} else if (stage[0] == FluidInterpolator.Type.STATE_6) { // 变大
				circleY = baseY;
			} else {
				Log.e(TAG, "getKeyPoints: 这里不应该被执行");
			}
		} else {
			if (stage[1] == FluidInterpolator.Type.STATE_6) {
				circleY = baseY;
			} else if (stage[1] == FluidInterpolator.Type.STATE_5) {
				circleY = baseY;
			} else if (stage[1] == FluidInterpolator.Type.STATE_4 || stage[1] == FluidInterpolator.Type.STATE_3) {
				circleY = (int) (baseY - ((baseY + height) / 2 - baseY) * (1 - changeValue * 2));
			} else if (stage[1] == FluidInterpolator.Type.STATE_2) {
				circleY = (baseY + height) / 2;
			} else if (stage[1] == FluidInterpolator.Type.STATE_1) {
				circleY = (baseY + height) / 2;
			} else {
				Log.e(TAG, "getKeyPoints: 这里不应该被执行");
			}
		}

		// --------------------------------------
		// ----------- 第一个贝塞尔关键点 -----------
		// --------------------------------------

		// h为基线到第一个关键点的Y坐标的距离
		int h = (int) (radius * (1 - changeValue) / 2);

		// 固定第一个关键点的Y坐标
		if (h >= radius / 5) {
			h = radius / 5;
		}

		int key1_Y = baseY - h;// 适用于圆上顶点与基线想切，上移圆，直到圆心到达基线

		// TODO 当圆点超过第一个关键点的Y坐标的时候，并没有做友好的界面处理，后续需要优化这一块。（以下三行注释勿删，修复完毕再删除；记录思想，以免遗忘）
//		if (circleY <= key1_Y) {
//			canvas.drawLine(0, baseY - h, 1080, baseY - h, paint);
//			valueAnimator.pause();
//		}

		int offset1_X = (int) Math.sqrt(radius * radius - (circleY - key1_Y) * (circleY - key1_Y));

		// 计算左右的对称的两个贝塞尔关键点的X坐标
		// 左边的贝塞尔关键点的X坐标
		int keyLeft1_X = baseX - offset1_X;
		// 右边的贝塞尔关键点的X坐标
		int keyRight1_X = baseX + offset1_X;


		// --------------------------------------
		// ----------- 第三个贝塞尔关键点 -----------
		// --------------------------------------

		// X轴上的偏移量
		int offset3_X = (int) Math.sqrt(radius * radius - radius * changeValue * radius * changeValue) + radius / 2;
		// 计算左右的对称的两个贝塞尔关键点的X坐标
		// 左边的贝塞尔关键点的X坐标
		int keyLeft3_X = baseX - offset3_X;
		// 右边的贝塞尔关键点的X坐标
		int keyRight3_X = baseX + offset3_X;
		// Y坐标，左右两个关键点的Y坐标相同
		int key3_Y = baseY;

		// --------------------------------------
		// ----------- 第二个贝塞尔关键点 -----------
		// --------------------------------------

		// X轴上的偏移量
		if (offset1_X == 0) {
			// 防止除以0的异常
			return new KeyPoint(
					0, 0,
					0, 0,
					0, 0,
					0, 0,
					0, 0,
					0, 0,
					circleX, circleY);
		}
		// 第一个关键点的圆的切线与基线交点的X坐标的偏移量
		int offset2_X = radius * radius / offset1_X;

		// 计算左右的对称的两个贝塞尔关键点的X坐标
		// 左边的贝塞尔关键点的X坐标
		int keyLeft2_X = baseX - offset2_X;
		// 右边的贝塞尔关键点的X坐标
		int keyRight2_X = baseX + offset2_X;

		// 限制范围，限制第二个贝塞尔关键点的X坐标超过第三个贝塞尔曲线的X坐标（为了美观，这里取第一个和第三个关键点的X坐标的中点）
		if (keyLeft2_X <= (keyLeft3_X + keyLeft1_X) / 2) {
			keyLeft2_X = (int) ((keyLeft3_X + keyLeft1_X) / 2);
			keyRight2_X = (int) ((keyRight3_X + keyRight1_X) / 2);
		}

		// Y坐标，左右两个关键点的Y坐标相同
		int key2_Y = baseY;

		return new KeyPoint(
				keyLeft1_X, key1_Y,
				keyLeft2_X, key2_Y,
				keyLeft3_X, key3_Y,
				keyRight1_X, key1_Y,
				keyRight2_X, key2_Y,
				keyRight3_X, key3_Y,
				circleX, circleY
		);

	}

	/**
	 * 设置背景颜色
	 */
	public void setBgColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		pathPaint.setColor(backgroundColor);
		invalidate();
	}

	/**
	 * 设置文字颜色
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
		textPaint.setColor(textColor);
		invalidate();
	}

	/**
	 * 设置tab颜色
	 */
	public void setTabColor(int tabColor) {
		this.tabColor = tabColor;
		tabPaint.setColor(tabColor);
		invalidate();
	}

	public void setOnItemClickListener(FluidTab.onItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface onItemClickListener {
		void itemClick(int currentItem);
	}

	public void involve(FragmentManager fragmentManager, int id) {
		this.fragmentManager = fragmentManager;
		this.id = id;
	}

	/**
	 * 设置底部tab信息
	 */
	public void setTabs(Tab[] tabs) {
		this.tabs = tabs;
		this.size = tabs.length;
		initThreshold();
		if (fragmentManager != null) {
			initFragment();
		}
		invalidate();
	}

	private void initFragment() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		for (Tab tab : tabs) {
			fragmentTransaction.add(id, tab.getFragment());
		}
		fragmentTransaction.commit();
		// 默认选中第0个
		switchFragment(0);
	}

	public void switchFragment(int currentItem) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		for (Tab tab : tabs) {
			fragmentTransaction.hide(tab.getFragment());
		}
		fragmentTransaction.show(tabs[currentItem].getFragment());
		fragmentTransaction.commit();
	}

}
