package org.hearingthevoice.innerlife.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SegmentedProgressBar extends View
{
	public static final int EMPTY = 0;
	public static final int GREY = 1;
	public static final int BLUE = 2;
	public static final int PURPLE = 3;

	private Context context;

	private Paint borderPaint;
	private Paint debugFillPaint;
	
	private Paint emptyPaint;
	private Paint greyPaint;
	private Paint bluePaint;
	private Paint purplePaint;

	private List<Integer> segmentMap;
	private int cornerRadius;

	public SegmentedProgressBar(Context context)
	{
		super(context);
		this.context = context;
		init(null, 0);
	}

	public SegmentedProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init(attrs, 0);
	}

	public SegmentedProgressBar(Context context, AttributeSet attrs, int style)
	{
		super(context, attrs, style);
		this.context = context;
		init(attrs, style);
	}

	private void init(AttributeSet attrs, int style)
	{
		// set default values
		segmentMap = new ArrayList<Integer>(28);
		for (int i = 0; i < 28; i++)
		{
			if (i % 3 == 0 && i % 5 == 0) segmentMap.add(PURPLE);
			else if (i % 3 == 0) segmentMap.add(BLUE);
			else if (i % 5 == 0) segmentMap.add(GREY);
			else segmentMap.add(EMPTY);
		}
		cornerRadius = 10;

		borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(1);
		
		emptyPaint = new Paint();
		emptyPaint.setShader(new LinearGradient(0, 0, 0, 40, Color.LTGRAY, Color.argb(255, 242, 242, 242), Shader.TileMode.CLAMP));
		
		greyPaint = new Paint();
		greyPaint.setShader(new LinearGradient(0, 0, 0, 40, 
				new int[]
				{
					Color.argb(255, 166, 166, 166),
					Color.argb(255, 204, 204, 204),
					Color.argb(255, 255, 255, 255)
				}, new float[]{0, 0.8f, 1}, Shader.TileMode.CLAMP));
		
		bluePaint = new Paint();
		bluePaint.setShader(new LinearGradient(0, 0, 0, 40, 
				new int[]
				{
					Color.argb(255, 36, 74, 179),
					Color.argb(255, 61, 108, 217),
					Color.argb(255, 87, 143, 255)
				}, new float[]{0, 0.8f, 1}, Shader.TileMode.CLAMP));
		
		purplePaint = new Paint();
		purplePaint.setShader(new LinearGradient(0, 0, 0, 40, 
				new int[]
				{
					Color.argb(255, 143, 37, 179),
					Color.argb(255, 178, 63, 217),
					Color.argb(255, 214, 89, 255)
				}, new float[]{0, 0.8f, 1}, Shader.TileMode.CLAMP));

		debugFillPaint = new Paint();
		debugFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		debugFillPaint.setColor(0xDDDDDDFF);
		debugFillPaint.setStyle(Style.FILL);
	}

	public List<Integer> getSegmentMap()
	{
		return segmentMap;
	}

	public void setSegmentMap(List<Integer> segmentMap)
	{
		this.segmentMap = segmentMap;
		invalidate();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		int x = getPaddingLeft();
		int y = getPaddingTop();
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
//		int height = getHeight() - getPaddingTop() - getPaddingBottom();
		int height = 40;

		float sw = width / (float) segmentMap.size();

		Path clipPath = new Path();
		clipPath.addRoundRect(new RectF(x, y, width, height), cornerRadius, cornerRadius, Path.Direction.CW);

		canvas.clipPath(clipPath);

		canvas.drawRect(0, 0, getWidth(), getHeight(), debugFillPaint);

		for (int i = 0; i < segmentMap.size(); i++)
		{
			if (segmentMap.get(i) == GREY)
			{
				canvas.drawRect(x + i * sw, 0, x + i * sw + sw, y + height, greyPaint);
			}
			else if (segmentMap.get(i) == BLUE)
			{
				canvas.drawRect(x + i * sw, 0, x + i * sw + sw, y + height, bluePaint);
			}
			else if (segmentMap.get(i) == PURPLE)
			{
				canvas.drawRect(x + i * sw, 0, x + i * sw + sw, y + height, purplePaint);
			}
			else
			{
				canvas.drawRect(x + i * sw, 0, x + i * sw + sw, y + height, emptyPaint);
			}
		}

	}
}
