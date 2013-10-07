package edu.cmu.carannotationv2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	private static final int arraySize = 5;
	private Paint currentPaint;
	private Paint formerPaint;
	private boolean drawRect = false;
	private float rect_left;/* Point center; */
	private float rect_top;
	private float rect_right;
	private float rect_bottom;
	
	
	
	private float fix_x;
	private float fix_y;
	private float sliding_x;
	private float sliding_y;
	
	
	public int[][] rectArray = new int[arraySize][4];
	// private Bitmap orgBitmap;
	private int rect_count;

	/**
	 * @return the rect_left
	 */
	public float getRect_left() {
		return rect_left;
	}

	/**
	 * @param rect_left
	 *            the rect_left to set
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
	 * @param rect_top
	 *            the rect_top to set
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
	 * @param rect_right
	 *            the rect_right to set
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
	 * @param rect_bottom
	 *            the rect_bottom to set
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
	 * @param drawRect
	 *            the drawRect to set
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
	 * @param rect_count
	 *            the rect_count to set
	 */
	public void setRect_count(int rect_count) {
		this.rect_count = rect_count;
	}

	private void setPaintColor_inDIV(Paint p, int color){
		p.setDither(true);
		p.setColor(color);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeWidth(4);
	}
	public DrawImageView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub

		super(context, attrs);
		currentPaint = new Paint();
		formerPaint=new Paint();
		setPaintColor_inDIV(currentPaint, Color.YELLOW);
		setPaintColor_inDIV(formerPaint, Color.BLUE);


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

		/*
		float temp_top=this.rect_top;
		float temp_bottom=this.rect_bottom;
		float temp_right=this.rect_right;
		float temp_left=this.rect_left;
		
		this.rect_top=Math.min(temp_top, temp_bottom);
		this.rect_left=Math.min(temp_left, temp_right);
		this.rect_bottom=Math.max(temp_bottom, temp_top);
		this.rect_right=Math.max(temp_left, temp_right);*/
		canvas.drawRect(rect_left, rect_top, rect_right, rect_bottom,
				currentPaint);
		for (int i = 0; i < rect_count; i++) {

			canvas.drawRect(rectArray[i][1], rectArray[i][0], rectArray[i][3],
					rectArray[i][2], formerPaint);
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
		rect_left = 0;
		rect_right = 0;
		rect_top = 0;
		rect_bottom = 0;
	}

	public void clearRecords() {
		for (int i = 0; i < rect_count; i++) {
			for (int j = 0; j < 4; j++) {

				rectArray[i][j] = 0;

			}

		}

		rect_count = 0;
	}

	public void addRect() {
		// TODO Auto-generated method stub
		this.rectArray[rect_count][0] = (int) rect_top;
		this.rectArray[rect_count][1] = (int) rect_left;
		this.rectArray[rect_count][2] = (int) rect_bottom;
		this.rectArray[rect_count][3] = (int) rect_right;
		rect_count = rect_count + 1;
	}

//	public RectInfo getLastRect() {
//		//return  new RectInfo(rectArray[rect_count-1][0],rectArray[rect_count-1][1],rectArray[rect_count-1][2],rectArray[rect_count-1][3]);
//		return new RectInfo((int)rect_top,(int) rect_left,(int) rect_bottom,(int) rect_right);
//	}
	public Rect getLastRect(){
		return new Rect((int)rect_left, (int)rect_top, (int)rect_right, (int)rect_bottom);
	}
//to adjust the rectangle
	public void adjustCortex() {
         // top_left -> right_bottom
		if ((fix_x<=sliding_x)&&(fix_y<=sliding_y)) {
			rect_left=fix_x;
			rect_top=fix_y;
			rect_right=sliding_x;
			rect_bottom=sliding_y;
		}
		// bottom_left ->top_right
		if ((fix_x<sliding_x)&&(fix_y>sliding_y)) {
			rect_left=fix_x;
			rect_right=sliding_x;
			rect_top=sliding_y;
			rect_bottom=fix_y;
		}
		//top_right  -> bottom_left
		 if ((fix_x>sliding_x)&&(fix_y<sliding_y)) {
			
			 rect_top=fix_y;
			 rect_bottom=sliding_y;
			 rect_left=sliding_x;
			 rect_right=fix_x;
		}
		 //bottom_right->topleft
		 if ((fix_x>sliding_x)&&(fix_y>sliding_y)) {
			rect_bottom=fix_y;
			rect_right=fix_x;
			rect_left=sliding_x;
			rect_top=sliding_y;
		}
		 
//		this.rect_top=Math.min(temp_top, temp_bottom);
//		this.rect_left=Math.min(temp_left, temp_right);
//		this.rect_bottom=Math.max(temp_bottom, temp_top);
//		this.rect_right=Math.max(temp_left, temp_right);
//		//drawing from bottom_left to upper_right 
//		if (this.rect_bottom<this.rect_top && this.rect_right>this.rect_left) {
//			this.rect_bottom=temp_top;
//			this.rect_top=temp_bottom;
//		}
		
	}

	public void setFix_x(float x) {
		fix_x=x;
		sliding_x=x;
	}

	public void setFix_y(float y) {
		fix_y=y;
		sliding_y=y;
	}

	public void setSliding_x(float x) {
		sliding_x=x;
	}

	public void setSliding_y(float y) {
		sliding_y=y;
	}

	public Object getRectSize() {
		// TODO Auto-generated method stub
		return null;
	}
}
