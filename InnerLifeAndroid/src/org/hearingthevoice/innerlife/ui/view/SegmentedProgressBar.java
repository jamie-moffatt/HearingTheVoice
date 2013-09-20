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
	private Paint shadowPaint;

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
			if (i < 8) segmentMap.add(BLUE);
			else if (i == 9) segmentMap.add(PURPLE);
			else if (i % 4 == 0) segmentMap.add(GREY);
			else segmentMap.add(EMPTY);
		}
		cornerRadius = 10;

		borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);

		emptyPaint = new Paint();
		emptyPaint.setShader(new LinearGradient(0, 0, 0, 40, Color.LTGRAY, Color.argb(255, 242, 242, 242),
				Shader.TileMode.CLAMP));

		greyPaint = new Paint();
		greyPaint.setColor(Color.GRAY);

		bluePaint = new Paint();
		bluePaint.setColor(Color.argb(255, 36, 74, 179));
		bluePaint.setStrokeWidth(1);
		bluePaint.setStyle(Style.FILL);

		purplePaint = new Paint();
		purplePaint.setColor(Color.argb(255, 143, 37, 179));

		debugFillPaint = new Paint();
		debugFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		debugFillPaint.setColor(Color.BLACK);
		debugFillPaint.setStyle(Style.FILL);

		shadowPaint = new Paint();
		shadowPaint.setColor(0x44000000);
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
		// int height = getHeight() - getPaddingTop() - getPaddingBottom();
		int height = 40;

		float sw = width / (float) segmentMap.size();

		List<RectF> clips = new ArrayList<RectF>();

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
				clips.add(new RectF(x + i * sw, 0, x + i * sw + sw, y + height));
			}
		}

		for (int i = 0; i * 40 < width; i++)
		{
			Path p = new Path();
			p.moveTo(x + (i * 40), y + height);
			p.lineTo(x + (i * 40) + 20, y + height);
			p.lineTo(x + (i * 40) + 40, y);
			p.lineTo(x + (i * 40) + 20, y);
			p.close();

			canvas.drawPath(p, shadowPaint);
		}
		
		for (RectF r : clips)
			canvas.drawRect(r, emptyPaint);
		
		for (int i = 0; i < segmentMap.size(); i++)
		{
			canvas.drawRect(x + i * sw + 1, 1, x + i * sw + sw, y + height - 1, borderPaint);
		}

	}
}
