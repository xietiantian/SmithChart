package com.example.smith;

import transmission.Circle;
import transmission.Convert;
import transmission.SmithChart;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

//改编自http://blog.csdn.net/guolin_blog/article/details/11100327
public class ZoomImageView extends View {
	/**
	 * 初始化状态常量
	 */
	public static final int STATUS_INIT = 1;
	/**
	 * 没有动作状态常量
	 */
	public static final int STATUS_NO_MOVE = 0;
	/**
	 * 图片放大状态常量
	 */
	public static final int STATUS_ZOOM_OUT = 2;

	/**
	 * 图片缩小状态常量
	 */
	public static final int STATUS_ZOOM_IN = 3;

	/**
	 * 图片拖动状态常量
	 */
	public static final int STATUS_MOVE = 4;

	/**
	 * point拖动状态常量
	 */
	public static final int STATUS_MOVE_POINT = 5;
	/**
	 * 用seekbar设置point位置的状态常量
	 */
	public static final int STATUS_SET_POINT = 6;
	/**
	 * point长宽
	 */
	public static final int POINT_SIZE = 20;

	/**
	 * 用于对图片进行移动和缩放变换的矩阵
	 */
	private Matrix matrix = new Matrix();

	/**
	 * 待展示的Bitmap对象
	 */
	private Bitmap sourceBitmap;
	/**
	 * 待展示的point Bitmap对象
	 */
	private Bitmap pointBitmap;

	/**
	 * 各种 Paint 代表了Canvas上的画笔、画刷、颜料等等
	 */
	private Paint paint_r = new Paint();
	private Paint paint_x = new Paint();
	private Paint paint_vswr = new Paint();
	private Paint paint_ref = new Paint();

	/**
	 * 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
	 */
	private int currentStatus;

	/**
	 * ZoomImageView控件的宽度
	 */
	private int width;

	/**
	 * ZoomImageView控件的高度
	 */
	private int height;

	/**
	 * 记录两指同时放在屏幕上时，中心点的横坐标值
	 */
	private float centerPointX;

	/**
	 * 记录两指同时放在屏幕上时，中心点的纵坐标值
	 */
	private float centerPointY;

	/**
	 * 记录两指同时放在屏幕上时，中心点的横坐标值
	 */
	private float zoomCenterX;

	/**
	 * 记录两指同时放在屏幕上时，中心点的纵坐标值
	 */
	private float zoomCenterY;

	/**
	 * 记录当前图片的宽度，图片被缩放时，这个值会一起变动
	 */
	private float currentBitmapWidth;

	/**
	 * 记录当前图片的高度，图片被缩放时，这个值会一起变动
	 */
	private float currentBitmapHeight;

	/**
	 * 记录上次手指移动时的横坐标
	 */
	private float lastXMove = -1;

	/**
	 * 记录上次手指移动时的纵坐标
	 */
	private float lastYMove = -1;

	/**
	 * 记录手指在横坐标方向上的移动距离
	 */
	private float movedDistanceX;

	/**
	 * 记录手指在纵坐标方向上的移动距离
	 */
	private float movedDistanceY;

	/**
	 * 记录图片在矩阵上的横向偏移值
	 */
	private float totalTranslateX;

	/**
	 * 记录图片在矩阵上的纵向偏移值
	 */
	private float totalTranslateY;
	/**
	 * 记录point横向偏移值
	 */
	private float totalRatio;
	/**
	 * 记录手指移动的距离所造成的缩放比例(原是手指间距之比，现修改成相对上一帧的放大倍数)
	 */
	private float scaledRatio;

	/**
	 * 记录图片初始化时的缩放比例
	 */
	private float initRatio;

	/**
	 * 记录上次两指之间的距离
	 */
	private double lastFingerDis;

	/**
	 * point的位置x
	 */
	private float pointX;

	/**
	 * point位置y
	 */
	private float pointY;
	/**
	 * point在圆图上的位置a
	 */
	private double pointA;

	/**
	 * point在圆图上的位置b
	 */
	private double pointB;

	/**
	 * smith图对象
	 */
	private SmithChart Schart = new SmithChart();

	/**
	 * ZoomImageView构造函数，将当前操作状态设为STATUS_INIT。
	 * 
	 * @param context
	 * @param attrs
	 */
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentStatus = STATUS_INIT;
	}

	public double getPointA() {
		return pointA;
	}

	public double getPointB() {
		return pointB;
	}
	
	public double getStatus() {
		return currentStatus;
	}

	public void fromSeekBar(double aa, double bb) {
		if (Convert.checkab(aa, bb)) {
			pointA = aa;
			pointB = bb;
			pointX = Convert.a2x(aa, totalRatio, totalTranslateX)-POINT_SIZE/2;
			pointY = Convert.b2y(bb, totalRatio, totalTranslateY)-POINT_SIZE/2;
			currentStatus = STATUS_SET_POINT;
			invalidate();
		}
	}

	/**
	 * 将待展示的图片设置进来。
	 * 
	 * @param bitmap
	 *            待展示的Bitmap对象
	 */
	public void setImageBitmap(Bitmap bitmap, Bitmap pointbitmap) {
		sourceBitmap = bitmap;
		pointBitmap = pointbitmap;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			// 分别获取到ZoomImageView的宽度和高度
			width = getWidth();
			height = getHeight();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getPointerCount() == 2) {
				// 当有两个手指按在屏幕上时，计算两指之间的距离
				lastFingerDis = distanceBetweenFingers(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				// 只有单指按在屏幕上移动时，为拖动状态
				float xMove = event.getX();
				float yMove = event.getY();
				if (lastXMove == -1 && lastYMove == -1) {
					lastXMove = xMove;
					lastYMove = yMove;
				}
				movedDistanceX = xMove - lastXMove;
				movedDistanceY = yMove - lastYMove;
				if (Math.abs(xMove - pointX) < 50
						&& Math.abs(yMove - pointY) < 50) {// 触点在point附近
					currentStatus = STATUS_MOVE_POINT;
//					zHandler.sendEmptyMessage(2);
					// 边界检查 还要改！
					if (xMove < 0 || xMove > 1060 || yMove < 0 || yMove > 1060) {
						movedDistanceX = 0;
					}
				} else {// 触点不在point附近
					// System.out.println(""+event.getRawX()+" "+event.getRawY()+"x="+xMove+",y="+yMove+"");
					currentStatus = STATUS_MOVE;
					// 进行边界检查，不允许将图片拖出边界
					if (totalTranslateX + movedDistanceX > 0) {
						movedDistanceX = 0;
					} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
						movedDistanceX = 0;
					}
					if (totalTranslateY + movedDistanceY > 0) {
						movedDistanceY = 0;
					} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
						movedDistanceY = 0;
					}
				}
				// 调用onDraw()方法绘制图片
				invalidate();
				lastXMove = xMove;
				lastYMove = yMove;
			} else if (event.getPointerCount() == 2) {
				// 有两个手指按在屏幕上移动时，为缩放状态
				centerPointBetweenFingers(event);
				double fingerDis = distanceBetweenFingers(event);
				if (fingerDis > lastFingerDis) {
					currentStatus = STATUS_ZOOM_OUT;
				} else {
					currentStatus = STATUS_ZOOM_IN;
				}
				// 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
				float lastRatio = totalRatio;
				if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
						|| (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
					scaledRatio = (float) (fingerDis / lastFingerDis);
					totalRatio = totalRatio * scaledRatio;
					if (totalRatio > 4 * initRatio) {
						totalRatio = 4 * initRatio;
						scaledRatio = totalRatio / lastRatio;
					} else if (totalRatio < initRatio) {
						totalRatio = initRatio;
						scaledRatio = totalRatio / lastRatio;
					}
					// 调用onDraw()方法绘制图片
					invalidate();
					lastFingerDis = fingerDis;
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (event.getPointerCount() == 2) {
				// 手指离开屏幕时将临时值还原
				lastXMove = -1;
				lastYMove = -1;
			}
			break;
		case MotionEvent.ACTION_UP:
			// 手指离开屏幕时将临时值还原
			lastXMove = -1;
			lastYMove = -1;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (currentStatus) {
		case STATUS_ZOOM_OUT:
		case STATUS_ZOOM_IN:
			zoom(canvas);
			break;
		case STATUS_MOVE:
			move(canvas);
			break;
		case STATUS_MOVE_POINT:
			movePoint(canvas);
			break;
		case STATUS_SET_POINT:
			setPoint(canvas);
			break;
		case STATUS_INIT:
			initBitmap(canvas);
			break;
		default:
			canvas.drawBitmap(sourceBitmap, matrix, null);
			drawCircles(canvas);
			canvas.drawBitmap(pointBitmap, pointX,pointY, null);
			break;
		}
	}

	/**
	 * 对图片进行缩放处理。
	 * 
	 * @param canvas
	 */
	private void zoom(Canvas canvas) {
		matrix.reset();
		// 将图片按总缩放比例进行缩放
		matrix.postScale(totalRatio, totalRatio);
		float scaledWidth = sourceBitmap.getWidth() * totalRatio;
		float scaledHeight = sourceBitmap.getHeight() * totalRatio;
		float translateX = 0f;
		float translateY = 0f;
		// 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
		if (currentBitmapWidth < width) {
			translateX = (width - scaledWidth) / 2f;
			zoomCenterX = width / 2;
		} else {
			translateX = totalTranslateX * scaledRatio + centerPointX
					* (1 - scaledRatio);
			// 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
			if (translateX > 0) {
				translateX = 0;
				zoomCenterX = 0;
			} else if (width - translateX > scaledWidth) {
				translateX = width - scaledWidth;
				zoomCenterX = width;
			} else {
				zoomCenterX = centerPointX;
			}
		}
		// 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
		if (currentBitmapHeight < height) {
			translateY = (height - scaledHeight) / 2f;
			zoomCenterY = height / 2;
		} else {
			translateY = totalTranslateY * scaledRatio + centerPointY
					* (1 - scaledRatio);
			// 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
			if (translateY > 0) {
				translateY = 0;
				zoomCenterY = 0;
			} else if (height - translateY > scaledHeight) {
				translateY = height - scaledHeight;
				zoomCenterY = height;
			} else {
				zoomCenterY = centerPointY;
			}
		}
		// 缩放后对图片进行偏移，以保证缩放后中心点位置不变
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		currentBitmapWidth = scaledWidth;
		currentBitmapHeight = scaledHeight;
		canvas.drawBitmap(sourceBitmap, matrix, null);

		drawCircles(canvas);

		pointX = pointX + POINT_SIZE / 2;
		pointY = pointY + POINT_SIZE / 2;
		pointX = pointX * scaledRatio + zoomCenterX * (1 - scaledRatio);
		pointY = pointY * scaledRatio + zoomCenterY * (1 - scaledRatio);
		pointX = pointX - POINT_SIZE / 2;
		pointY = pointY - POINT_SIZE / 2;
		canvas.drawBitmap(pointBitmap, pointX, pointY, null);
		currentStatus=STATUS_NO_MOVE;
	}

	/**
	 * 对图片进行平移处理
	 * 
	 * @param canvas
	 */
	private void move(Canvas canvas) {
		matrix.reset();
		// 根据手指移动的距离计算出总偏移值
		float translateX = totalTranslateX + movedDistanceX;
		float translateY = totalTranslateY + movedDistanceY;
		// 先按照已有的缩放比例对图片进行缩放
		matrix.postScale(totalRatio, totalRatio);
		// 再根据移动距离进行偏移
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		canvas.drawBitmap(sourceBitmap, matrix, null);

		drawCircles(canvas);

		pointX = pointX + movedDistanceX;
		pointY = pointY + movedDistanceY;
		canvas.drawBitmap(pointBitmap, pointX, pointY, null);
		currentStatus=STATUS_NO_MOVE;
	}

	/**
	 * 对point进行平移处理
	 * 
	 * @param canvas
	 */
	private void movePoint(Canvas canvas) {
		matrix.reset();
		// 先按照已有的缩放比例对图片进行缩放
		matrix.postScale(totalRatio, totalRatio);
		// 再根据移动距离进行偏移
		matrix.postTranslate(totalTranslateX, totalTranslateY);
		canvas.drawBitmap(sourceBitmap, matrix, null);
		// 根据手指移动的距离计算出总偏移值
		float tempx = pointX + movedDistanceX;
		float tempy = pointY + movedDistanceY;
		// 调用convert类函数防止出界 待修改！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		double tempa = Convert.x2a(pointX, POINT_SIZE, totalRatio,
				totalTranslateX);
		double tempb = Convert.y2b(pointY, POINT_SIZE, totalRatio,
				totalTranslateY);
		if (Convert.checkab(tempa, tempb)) {
			pointX = tempx;
			pointY = tempy;
			pointA = tempa;
			pointB = tempb;
		} else {
			movedDistanceX = 0;
			movedDistanceY = 0;
		}
		Schart.setab(pointA, pointB);
		drawCircles(canvas);

		canvas.drawBitmap(pointBitmap, pointX, pointY, null);
		currentStatus=STATUS_NO_MOVE;
	}
	
	private void setPoint(Canvas canvas){
		matrix.reset();
		// 先按照已有的缩放比例对图片进行缩放
		matrix.postScale(totalRatio, totalRatio);
		// 再根据移动距离进行偏移
		matrix.postTranslate(totalTranslateX, totalTranslateY);
		canvas.drawBitmap(sourceBitmap, matrix, null);
		
		Schart.setab(pointA, pointB);
		drawCircles(canvas);
		
		canvas.drawBitmap(pointBitmap, pointX, pointY, null);
		currentStatus=STATUS_NO_MOVE;
	}

	/**
	 * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
	 * 
	 * @param canvas
	 */
	private void initBitmap(Canvas canvas) {
		if (sourceBitmap != null && pointBitmap != null) {
			matrix.reset();
			int bitmapWidth = sourceBitmap.getWidth();
			int bitmapHeight = sourceBitmap.getHeight();
			pointX = (int) ((width - POINT_SIZE) / 2);
			pointY = (int) ((height - POINT_SIZE) / 2);
			if (bitmapWidth > width || bitmapHeight > height) {
				if (bitmapWidth - width > bitmapHeight - height) {
					// 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = width / (bitmapWidth * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateY = (height - (bitmapHeight * ratio)) / 2f;
					// 在纵坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(0, translateY);
					totalTranslateY = translateY;
					totalRatio = initRatio = ratio;
				} else {
					// 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = height / (bitmapHeight * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateX = (width - (bitmapWidth * ratio)) / 2f;
					// 在横坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(translateX, 0);
					totalTranslateX = translateX;
					totalRatio = initRatio = ratio;
				}
				currentBitmapWidth = bitmapWidth * initRatio;
				currentBitmapHeight = bitmapHeight * initRatio;
			} else {
				// 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
				float translateX = (width - sourceBitmap.getWidth()) / 2f;
				float translateY = (height - sourceBitmap.getHeight()) / 2f;
				matrix.postTranslate(translateX, translateY);
				totalTranslateX = translateX;
				totalTranslateY = translateY;
				totalRatio = initRatio = 1f;
				currentBitmapWidth = bitmapWidth;
				currentBitmapHeight = bitmapHeight;
			}
			pointA = Convert.x2a(pointX, POINT_SIZE, totalRatio,
					totalTranslateX);
			pointB = Convert.y2b(pointY, POINT_SIZE, totalRatio,
					totalTranslateY);

			canvas.drawBitmap(sourceBitmap, matrix, null);

			// 设置不同的画笔
			paint_r.setColor(Color.GREEN);
			paint_r.setAntiAlias(true);
			paint_r.setStyle(Paint.Style.STROKE);
			paint_r.setStrokeWidth(10);

			paint_x.setColor(Color.BLUE);
			paint_x.setAntiAlias(true);
			paint_x.setStyle(Paint.Style.STROKE);
			paint_x.setStrokeWidth(10);

			paint_vswr.setColor(Color.YELLOW);
			paint_vswr.setAntiAlias(true);
			paint_vswr.setStyle(Paint.Style.STROKE);
			paint_vswr.setStrokeWidth(10);

			paint_ref.setARGB(255, 160, 32, 240);// purple?
			paint_ref.setAntiAlias(true);
			paint_ref.setStyle(Paint.Style.STROKE);
			paint_ref.setStrokeWidth(10);

			Schart.setab(pointA, pointB);
			drawCircles(canvas);

			canvas.drawBitmap(pointBitmap, pointX, pointY, null);
			
			currentStatus=STATUS_NO_MOVE;
		}
	}

	public void drawCircles(Canvas canvas) {
		// 画圆

		if (Schart.getFlag() != Schart.NotIn) {
			// 画等反射系数圆
			Circle tempR = Convert.convertCircle(Schart.ReflectCircleCalc(),
					totalRatio, totalTranslateX, totalTranslateY);
			if (tempR != null) {
				canvas.drawCircle((float) tempR.getX(), (float) tempR.getY(),
						(float) tempR.getRadius(), paint_ref);
			}
			// 画等驻波比圆
			tempR = Convert.convertCircle(Schart.VSWRCircleCalc(), totalRatio,
					totalTranslateX, totalTranslateY);
			if (tempR != null) {
				canvas.drawCircle((float) tempR.getX(), (float) tempR.getY(),
						(float) tempR.getRadius(), paint_vswr);
			}

			if (Schart.getFlag() != Schart.OC) {
				// 画等电阻圆
				tempR = Convert.convertCircle(Schart.rCircleCalc(), totalRatio,
						totalTranslateX, totalTranslateY);
				if (tempR != null) {
					canvas.drawCircle((float) tempR.getX(),
							(float) tempR.getY(), (float) tempR.getRadius(),
							paint_r);
				}

				if (Schart.getFlag() == Schart.Normal) {
					// 画等电抗圆
					tempR = Convert.convertCircle(Schart.xCircleCalc(),
							totalRatio, totalTranslateX, totalTranslateY);
					if (tempR != null) {
						canvas.drawCircle((float) tempR.getX(),
								(float) tempR.getY(),
								(float) tempR.getRadius(), paint_x);
					}
				} else {
					canvas.drawLine(0, pointY + POINT_SIZE / 2, width, pointY
							+ POINT_SIZE / 2, paint_x);
				}
			}
		}
	}

	/**
	 * 计算两个手指之间的距离。
	 * 
	 * @param event
	 * @return 两个手指之间的距离
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 计算两个手指之间中心点的坐标。
	 * 
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}

}