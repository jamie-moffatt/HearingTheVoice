package org.hearingthevoice.innerlife.ui.view;

import org.hearingthevoice.innerlife.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class XPBar extends View
{
	private int max;
	private int textColor;
	private Paint textPaint;
	private double sectionSpacing;

	public XPBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
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
		init();
	}

	public int getMax()
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
		textPaint = new Paint();
		Log.wtf("Paint", String.format("%x", textPaint.getColor()));
//		textPaint.setAntiAlias(true);
		textPaint.setColor(textColor);
		textPaint.setStrokeWidth(2);
		textPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		sectionSpacing = w / max;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		for (int i = 0; i < max; i++)
		{
			canvas.drawRect((float)(i * sectionSpacing)+0.5f, (float)1, (float)((i * sectionSpacing) + sectionSpacing)+0.5f, (float)height,
					textPaint);
		}

		super.onDraw(canvas);
	}
}
