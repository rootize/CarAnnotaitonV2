package edu.cmu.carannotationv2;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	
	private static final int arraySize=5;
	private Paint currentPaint;
	private boolean drawRect = false;
	private float rect_left;/* Point center; */
	private float rect_top;
	private float rect_right;
	private float rect_bottom;
    public int[][] rectArray=new int[arraySize][4];
	// private Bitmap orgBitmap;
	private int rect_count;

	
	
	
	
	/**
	 * @return the rect_left
	 */
	public float getRect_left() {
		return rect_left;
	}

	/**
	 * @param rect_left the rect_left to set
	 */
	public void setRect_left(float rect_left) {
		this.rect_left = rect_left;
	}

	/**
	 * @return the rect_top
	 */
	public float getRect_top() {
		return rect_top;
	}

	/**
	 * @param rect_top the rect_top to set
	 */
	public void setRect_top(float rect_top) {
		this.rect_top = rect_top;
	}

	/**
	 * @return the rect_right
	 */
	public float getRect_right() {
		return rect_right;
	}

	/**
	 * @param rect_right the rect_right to set
	 */
	public void setRect_right(float rect_right) {
		this.rect_right = rect_right;
	}

	/**
	 * @return the rect_bottom
	 */
	public float getRect_bottom() {
		return rect_bottom;
	}

	/**
	 * @param rect_bottom the rect_bottom to set
	 */
	public void setRect_bottom(float rect_bottom) {
		this.rect_bottom = rect_bottom;
	}

	/**
	 * @return the drawRect
	 */
	public boolean isDrawRect() {
		return drawRect;
	}

	/**
	 * @param drawRect the drawRect to set
	 */
	public void setDrawRect(boolean drawRect) {
		this.drawRect = drawRect;
	}

	
	
	/**
	 * @return the rect_count
	 */
	public int getRect_count() {
		return rect_count;
	}

	/**
	 * @param rect_count the rect_count to set
	 */
	public void setRect_count(int rect_count) {
		this.rect_count = rect_count;
	}

	public DrawImageView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub

		super(context, attrs);
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(Color.YELLOW);
		currentPaint.setStyle(Paint.Style.STROKE);
		currentPaint.setStrokeCap(Paint.Cap.ROUND);
		currentPaint.setStrokeJoin(Paint.Join.ROUND);
		currentPaint.setStrokeWidth(4);
        
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

		//if (drawRect) {
			// canvas.drawCircle(center.x, center.y, c_radius, currentPaint);
		canvas.drawRect(rect_left, rect_top, rect_right, rect_bottom, currentPaint);
			for (int i = 0; i < rect_count; i++) {
				
				canvas.drawRect(rectArray[i][1], rectArray[i][0], rectArray[i][3], rectArray[i][2], currentPaint);
			}
		//}
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
		rect_left = 0;
		rect_right = 0;
		rect_top = 0;
		rect_bottom = 0;
	}
	public void clearRecords(){
		for (int i = 0; i < rect_count; i++) {
			for (int j = 0; j < 4; j++) {
				
				rectArray[i][j]=0;
				
			}
			
		}
		
		rect_count=0;
	}
}
