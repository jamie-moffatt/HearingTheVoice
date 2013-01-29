package org.hearingthevoice.innerlife.ui.view;

import org.hearingthevoice.innerlife.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class XPBar extends View
{
	private int max;
	private int textColor;
	private Paint textPaint;
	private int sectionSpacing;

	public XPBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
		// assign properties defined in xml attributes
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.XPBar, 0, 0);
		try
		{
			max = a.getInt(R.styleable.XPBar_max, 100);
			textColor = a.getInt(R.styleable.XPBar_textColor, Color.BLACK);
		}
		finally
		{
			a.recycle();
		}
	}

	public int getMaxt()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
		invalidate();
		requestLayout();
	}

	// create paint objects only once rather than every time onDraw() is called
	// to improve performance
	private void init()
	{
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//textPaint.setColor(textColor);
		textPaint.setStrokeWidth(2);
		textPaint.setStyle(Paint.Style.STROKE);
		Log.wtf("DRAW","Paint");
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		sectionSpacing = w/max;
		Log.wtf("DRAW",""+max);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		RectF tmpRect = new RectF();
		tmpRect.top = 0;
		tmpRect.bottom = height;
		tmpRect.left = 0;
		tmpRect.right = width;
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		
		for (int i = 0; i < max; i++)
		{
			canvas.drawRect(i*sectionSpacing + 10, 1, (i*sectionSpacing)+sectionSpacing + 10, height, textPaint);
		}
//		canvas.drawOval(tmpRect, new Paint());
		Log.wtf("DRAW","OnDraw");
		super.onDraw(canvas);
	}
}
