package com.joe.bulletscreenview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Async Bullet View base on SurfaceView class,
 * every instance has own draw tread, it 
 * will not block UI, you can show several
 * instance in the meantime.
 * @author zouhao
 *
 */
public class AsyncBulletView extends SurfaceView implements Callback{

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
	
	
	public AsyncBulletView(Context context, String title) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title;
		init();
	}

	public AsyncBulletView(Context context, AttributeSet attrs, String title) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title;
		init();
	}
	
	
	/**
	 *  Initialize paint and parameters
	 */
	private void init() {
		initPaints();
		initParameters();
		getHolder().addCallback(this);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
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
			// Rect height and width are base on the text size
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
		
		//Clear early cache
		bgPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR)); 
		canvas.drawPaint(bgPaint);
		bgPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER)); 
		
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
	
	/**
	 * The thread respond for drawing
	 * @author zouhao
	 *
	 */
	private class DrawThread extends Thread {
		
		private SurfaceHolder holder;

		public DrawThread(SurfaceHolder holder) {
			super();
			this.holder = holder;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while (xPos > -totalWidth) {
					Canvas canvas = holder.lockCanvas();
					
					drawContent(canvas);
					holder.unlockCanvasAndPost(canvas);
					xPos -= 3;
					sleep(3);
				}
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				onRollFinished();
			}
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		DrawThread drawThread = new DrawThread(holder);
		drawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
}
