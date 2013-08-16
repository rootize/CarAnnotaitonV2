package edu.cmu.carannotationv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	private Paint currentPaint;
	public boolean drawPoint = false;
	public float left;/* Point center; */
	public float top;
	public float right;
	public float bottom;

	// private Bitmap orgBitmap;

	public DrawImageView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub

		super(context, attrs);
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(Color.BLACK);
		currentPaint.setStyle(Paint.Style.STROKE);
		currentPaint.setStrokeCap(Paint.Cap.ROUND);
		currentPaint.setStrokeJoin(Paint.Join.ROUND);
		currentPaint.setStrokeWidth(2);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (drawPoint) {
			// canvas.drawCircle(center.x, center.y, c_radius, currentPaint);
			canvas.drawRect(left, top, right, bottom, currentPaint);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		// orgBitmap=Bitmap.createBitmap(bm);
		super.setImageBitmap(bm);
	}

	public void clearrect() {
		left = 0;
		right = 0;
		top = 0;
		bottom = 0;
	}
}
