package com.joe.bulletscreenview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AsyncBulletViewUtils {

	/**
	 * Show an AsyncBulletView with custom title
	 * 
	 * @param context
	 * @param title
	 */
	public static void showAsyncBulletView(Context context, String title) {

		WindowManager windowManager = (WindowManager) context
				.getApplicationContext().getSystemService(
						Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		params.x = 0;
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		// Random Y position
		params.y = (int) ((dm.heightPixels * Math.random()) / (dm.ydpi / 160))
				* (int) Math.pow(-1, (int) (10 * Math.random()));
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = 100;

		AsyncBulletView asyncBulletView = new AsyncBulletView(context, title);
		asyncBulletView.setLayoutParams(params);

		windowManager.addView(asyncBulletView, params);
	}
}
