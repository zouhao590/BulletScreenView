package com.joe.bulletscreenview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Bullet View base on common View class,
 * Draw on main thread, maybe block UI
 *  if you show too many instance in the meantime.
 * @author zouhao
 *
 */
public class BulletView extends View{
	
	// Input parameter
	private String title;
	private Bitmap icon;
	
	// Private temp parameters
	private Context context;
	private Paint textPaint, bgPaint;
	private float totalWidth;
	private float totalHeight;
	private int xPos;
	private int yPos;

	public BulletView(Context context, String title) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title == null ? "No title!" : title;
		init();
	}

	public BulletView(Context context, AttributeSet attrs, String title) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title == null ? "No title!" : title;
		init();
	}
	
	
	/**
	 *  Initialize paint and parameters
	 */
	private void init() {
		initPaints();
		initParameters();
	}
	
	/**
	 * Initialize paints
	 */
	private void initPaints() {
		
		textPaint = new Paint();
		textPaint.setTextSize(60);
		textPaint.setColor(Color.WHITE);
		textPaint.setStrokeWidth(2);
		textPaint.setStyle(Style.FILL);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setAntiAlias(true);
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.argb(255, 128, 128, 128));
		bgPaint.setStyle(Style.FILL);
		bgPaint.setAlpha(150);
		bgPaint.setAntiAlias(true);
	}
	
	/**
	 * Initialize parameters
	 */
	private void initParameters() {
		
		if (textPaint != null && title != null) {
			//	Rect height and width are base on the text size
			totalHeight = textPaint.getTextSize() * 1.5f;
			float textWidth = textPaint.measureText(title);
			totalWidth = textWidth + totalHeight; //Width + circleSize
		}
		
		WindowManager windowManager = (WindowManager)context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        
		xPos = dm.widthPixels; //Start from right
		yPos = 0;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		drawContent(canvas);
		xPos -= 3;
		if (xPos > -totalWidth) {
			postInvalidateDelayed(10);
		}else {
			onRollFinished();
		}
	}
	
	/**
	 * Draw content
	 * @param canvas
	 */
	private void drawContent(Canvas canvas) {
		drawBackground(canvas);
		drawTitle(canvas);
	}
	
	/**
	 * Draw title
	 * @param canvas
	 */
	private void drawTitle(Canvas canvas) {
		if (canvas == null || title == null) {
			return;
		}
		
		float x = xPos + totalWidth / 2;
		FontMetricsInt fontMetrics = textPaint.getFontMetricsInt(); 
		float baseline = yPos + (totalHeight - fontMetrics.bottom 
				+ fontMetrics.top) / 2 - fontMetrics.top;
		
		canvas.drawText(title, x , baseline, textPaint);
		
	}
	
	/**
	 * Draw background
	 * @param canvas
	 */
	private void drawBackground(Canvas canvas) {
		if (canvas == null) {
			return;
		}
		
		//Left circle
		RectF rectLeft = new RectF();
		rectLeft.left = xPos;
	    rectLeft.top = yPos; 
	    rectLeft.right = xPos + totalHeight;
	    rectLeft.bottom = yPos + totalHeight;
		canvas.drawArc(rectLeft, 90, 180, true, bgPaint);
		
		//Center rect
		RectF rectCenter = new RectF();
		rectCenter.left = rectLeft.right - totalHeight / 2;
		rectCenter.top = yPos; 
		rectCenter.right = rectCenter.left + (totalWidth - totalHeight);//TotalWidth - circleSize
		rectCenter.bottom = yPos + totalHeight;
		canvas.drawRect(rectCenter, bgPaint);
		
		//Right circle
		RectF rectRight = new RectF();
		rectRight.left = rectCenter.right - totalHeight / 2;
		rectRight.top = yPos;
		rectRight.right = rectRight.left + totalHeight;
		rectRight.bottom = yPos + totalHeight;
		canvas.drawArc(rectRight, 270, 180, true, bgPaint);
	}
	
	/**
	 *  Remove self when draw finished
	 */
	private void onRollFinished() {
		WindowManager windowManager = (WindowManager)context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(this);
	}
	
}
