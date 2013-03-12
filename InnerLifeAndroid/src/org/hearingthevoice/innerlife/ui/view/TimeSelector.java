package org.hearingthevoice.innerlife.ui.view;

import org.hearingthevoice.innerlife.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TimeSelector extends View
{
	private Context context;

	private OnTimeChangedListener timeChangedCallback;

	private TextPaint textPaint;
	private Paint backgroundPaint;
	private Paint darkBackgroundPaint;
	private Paint borderPaint;
	private Paint dividerPaint;
	private Paint timeSliceAMFillPaint;
	private Paint timeSlicePMFillPaint;
	private Paint timeSliceSelectedFillPaint;

	// default range for AM notifications is 09:00 - 12:00
	// default range for PM notifications is 16:00 - 19:00
	private int amStartTime = 9;
	private int pmStartTime = 16;

	// {a, b, c}
	private Point[] amBoundingTriangle = new Point[3];
	private Point[] pmBoundingTriangle = new Point[3];

	private int centreX;
	private int centreY;

	private boolean touching;
	private float currentX;
	private float currentY;
	private float theta;

	private static final int NO_COLLISION = 0;
	private static final int AM_TRIANGLE_COLLISION = 1;
	private static final int PM_TRIANGLE_COLLISION = 2;
	private int hit = NO_COLLISION;

	private static final boolean DEBUG = false;

	public TimeSelector(Context context)
	{
		super(context);
		this.context = context;
		init(null, 0);
	}

	public TimeSelector(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init(attrs, 0);
	}

	public TimeSelector(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeSelector,
				defStyle, 0);

		a.recycle();

		// Set up a default TextPaint object
		textPaint = new TextPaint();
		textPaint.setTextSize(12);
		textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextAlign(Paint.Align.LEFT);

		backgroundPaint = new Paint();
		backgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setColor(Color.LTGRAY);
		
		darkBackgroundPaint = new Paint();
		darkBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		darkBackgroundPaint.setColor(Color.parseColor("#aaaaaa"));

		borderPaint = new Paint();
		borderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(5);
		borderPaint.setStrokeCap(Cap.ROUND);

		dividerPaint = new Paint();
		dividerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		dividerPaint.setColor(Color.BLACK);
		dividerPaint.setStyle(Style.STROKE);
		dividerPaint.setStrokeWidth(1);

		timeSliceAMFillPaint = new Paint();
		timeSliceAMFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		timeSliceAMFillPaint.setColor(Color.rgb(114, 134, 209));
		timeSliceAMFillPaint.setStyle(Style.FILL);
		
		timeSlicePMFillPaint = new Paint();
		timeSlicePMFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		timeSlicePMFillPaint.setColor(Color.rgb(32, 45, 112));
		timeSlicePMFillPaint.setStyle(Style.FILL);

		timeSliceSelectedFillPaint = new Paint();
		timeSliceSelectedFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		timeSliceSelectedFillPaint.setColor(Color.rgb(255, 64, 64));
		timeSliceSelectedFillPaint.setStyle(Style.FILL);
	}

	public int getAMTime()
	{
		return this.amStartTime;
	}

	private void decrementAMTime()
	{
		if (this.amStartTime > 0) this.amStartTime--;
		invalidate();
	}

	private void incrementAMTime()
	{
		if (this.amStartTime < 9) this.amStartTime++;
		invalidate();
	}

	public int getPMTime()
	{
		return this.pmStartTime;
	}

	private void decrementPMTime()
	{
		if (this.pmStartTime > 12) this.pmStartTime--;
		invalidate();
	}

	private void incrementPMTime()
	{
		if (this.pmStartTime < 21) this.pmStartTime++;
		invalidate();
	}

	private float cross2D(Point u, Point v)
	{
		return u.y * v.x - u.x * v.y;
	}

	private Point subtract(Point u, Point v)
	{
		return new Point(u.x - v.x, u.y - v.y);
	}

	private boolean pointInTriangle(Point p, Point a, Point b, Point c)
	{
		if (cross2D(subtract(p, a), subtract(b, a)) < 0.0f) return false;
		if (cross2D(subtract(p, b), subtract(c, b)) < 0.0f) return false;
		if (cross2D(subtract(p, c), subtract(a, c)) < 0.0f) return false;
		return true;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();

		int contentWidth = getWidth() - paddingLeft - paddingRight;
		int contentHeight = getHeight() - paddingTop - paddingBottom;

		centreX = paddingLeft + contentWidth / 2;
		centreY = paddingTop + contentHeight / 2;
		int radius = Math.min(contentWidth, contentHeight) / 2;

		RectF rect = new RectF(centreX - radius, centreY - radius, centreX + radius,
				centreY + radius);
		
		canvas.drawCircle(centreX, centreY, radius, backgroundPaint);
		
		canvas.drawArc(rect, 90, 180, false, darkBackgroundPaint);

		/*
		 * DRAW AM ARC
		 */

		Path path = new Path();
		path.moveTo(centreX, centreY);
		path.arcTo(rect, 270.0f + (amStartTime * 15), 45.0f);
		path.close();

		if (hit == AM_TRIANGLE_COLLISION) canvas.drawPath(path, timeSliceSelectedFillPaint);
		else canvas.drawPath(path, timeSliceAMFillPaint);

		/*
		 * END DRAW AM ARC
		 */

		if (DEBUG)
		{
			Point a, b, c;
			a = new Point((int) centreX, (int) centreY);
			b = new Point(
					(int) (centreX + radius * (float) Math
							.sin(((amStartTime + 3) * 15 * Math.PI) / 180.0)),
					(int) (centreY - radius * (float) Math
							.cos(((amStartTime + 3) * 15 * Math.PI) / 180.0)));
			c = new Point(
					(int) (centreX + radius * (float) Math
							.sin((amStartTime * 15 * Math.PI) / 180.0)),
					(int) (centreY - radius * (float) Math
							.cos((amStartTime * 15 * Math.PI) / 180.0)));

			path = new Path();
			path.moveTo(a.x, a.y);
			path.lineTo(b.x, b.y);
			path.lineTo(c.x, c.y);
			path.close();
			canvas.drawPath(path, dividerPaint);
		}

		amBoundingTriangle[0] = new Point(0, 0);
		amBoundingTriangle[2] = new Point(
				(int) (radius * (float) Math.sin((amStartTime * 15 * Math.PI) / 180.0)),
				(int) (radius * (float) Math.cos((amStartTime * 15 * Math.PI) / 180.0)));
		amBoundingTriangle[1] = new Point(
				(int) (radius * (float) Math.sin(((amStartTime + 3) * 15 * Math.PI) / 180.0)),
				(int) (radius * (float) Math.cos(((amStartTime + 3) * 15 * Math.PI) / 180.0)));

		/*
		 * DRAW PM ARC
		 */

		path = new Path();
		path.moveTo(centreX, centreY);
		path.arcTo(rect, 270.0f + (pmStartTime * 15), 45.0f);
		path.close();

		if (hit == PM_TRIANGLE_COLLISION) canvas.drawPath(path, timeSliceSelectedFillPaint);
		else canvas.drawPath(path, timeSlicePMFillPaint);

		/*
		 * END DRAW PM ARC
		 */

		if (DEBUG)
		{
			Point a, b, c;
			a = new Point((int) centreX, (int) centreY);
			b = new Point(
					(int) (centreX + radius * (float) Math
							.sin(((pmStartTime + 3) * 15 * Math.PI) / 180.0)),
					(int) (centreY - radius * (float) Math
							.cos(((pmStartTime + 3) * 15 * Math.PI) / 180.0)));
			c = new Point(
					(int) (centreX + radius * (float) Math
							.sin((pmStartTime * 15 * Math.PI) / 180.0)),
					(int) (centreY - radius * (float) Math
							.cos((pmStartTime * 15 * Math.PI) / 180.0)));

			path = new Path();
			path.moveTo(a.x, a.y);
			path.lineTo(b.x, b.y);
			path.lineTo(c.x, c.y);
			path.close();
			canvas.drawPath(path, dividerPaint);
		}

		pmBoundingTriangle[0] = new Point(0, 0);
		pmBoundingTriangle[2] = new Point(
				(int) (radius * (float) Math.sin((pmStartTime * 15 * Math.PI) / 180.0)),
				(int) (radius * (float) Math.cos((pmStartTime * 15 * Math.PI) / 180.0)));
		pmBoundingTriangle[1] = new Point(
				(int) (radius * (float) Math.sin(((pmStartTime + 3) * 15 * Math.PI) / 180.0)),
				(int) (radius * (float) Math.cos(((pmStartTime + 3) * 15 * Math.PI) / 180.0)));

		canvas.drawCircle(centreX, centreY, radius, borderPaint);

		int hourLabel = 0;
		
		for (int i = 0; i < 90; i += 15)
		{
			canvas.drawLine(centreX + radius * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - radius * (float) Math.cos((i * Math.PI) / 180.0),
					centreX + (radius * 0.95f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - (radius * 0.95f) * (float) Math.cos((i * Math.PI) / 180.0),
					borderPaint);
			canvas.drawText(""+(hourLabel++), centreX + (radius * 0.85f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - (radius * 0.85f) * (float) Math.cos((i * Math.PI) / 180.0), textPaint);
		}
		for (int i = 90; i >= 0; i -= 15)
		{
			canvas.drawLine(centreX + radius * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + radius * (float) Math.cos((i * Math.PI) / 180.0),
					centreX + (radius * 0.95f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + (radius * 0.95f) * (float) Math.cos((i * Math.PI) / 180.0),
					borderPaint);
			canvas.drawText(""+(hourLabel++), centreX + (radius * 0.85f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + (radius * 0.85f) * (float) Math.cos((i * Math.PI) / 180.0), textPaint);
		}
		for (int i = 15; i < 90; i += 15)
		{
			canvas.drawLine(centreX - radius * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + radius * (float) Math.cos((i * Math.PI) / 180.0),
					centreX - (radius * 0.95f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + (radius * 0.95f) * (float) Math.cos((i * Math.PI) / 180.0),
					borderPaint);
			canvas.drawText(""+(hourLabel++), centreX - (radius * 0.85f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY + (radius * 0.85f) * (float) Math.cos((i * Math.PI) / 180.0), textPaint);
		}
		for (int i = 90; i >= 15; i -= 15)
		{
			canvas.drawLine(centreX - radius * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - radius * (float) Math.cos((i * Math.PI) / 180.0),
					centreX - (radius * 0.95f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - (radius * 0.95f) * (float) Math.cos((i * Math.PI) / 180.0),
					borderPaint);
			canvas.drawText(""+(hourLabel++), centreX - (radius * 0.85f) * (float) Math.sin((i * Math.PI) / 180.0),
					centreY - (radius * 0.85f) * (float) Math.cos((i * Math.PI) / 180.0), textPaint);
		}

		if (DEBUG)
		{
			if (touching)
			{
				canvas.drawLine(centreX, centreY, currentX, currentY, dividerPaint);
				canvas.drawText("" + (Math.toDegrees(theta)), centreX + 5, centreY + 5, textPaint);
			}
		}

		canvas.drawLine(centreX, centreY - radius, centreX, centreY + radius, dividerPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		currentX = event.getX();
		currentY = event.getY();

		float u = currentX - centreX;
		float v = currentY - centreY;

		boolean isLeft = currentX < centreX;

		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			touching = true;

			theta = (float) Math.acos(-v / Math.sqrt(u * u + v * v));

			if (isLeft) theta = (float) (2 * Math.PI - theta);

			Point p = new Point((int) (currentX - centreX), (int) (centreY - currentY));
			Point a, b, c;

			a = amBoundingTriangle[0];
			b = amBoundingTriangle[1];
			c = amBoundingTriangle[2];

			if (pointInTriangle(p, a, b, c))
			{
				hit = AM_TRIANGLE_COLLISION;
			}

			a = pmBoundingTriangle[0];
			b = pmBoundingTriangle[1];
			c = pmBoundingTriangle[2];

			if (pointInTriangle(p, a, b, c))
			{
				hit = PM_TRIANGLE_COLLISION;
			}

			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if (hit == AM_TRIANGLE_COLLISION)
			{
				theta = (float) Math.acos(-v / Math.sqrt(u * u + v * v));
				if (!isLeft)
				{
					if (Math.toDegrees(theta) > amStartTime * 15)
					{
						incrementAMTime();
						Log.d("T", "increment AM");
						if (timeChangedCallback != null)
							timeChangedCallback.onTimeChanged(amStartTime, pmStartTime);
					}
					if (Math.toDegrees(theta) < amStartTime * 15)
					{
						decrementAMTime();
						Log.d("T", "decrement AM");
						if (timeChangedCallback != null)
							timeChangedCallback.onTimeChanged(amStartTime, pmStartTime);
					}
				}
				invalidate();
			}
			if (hit == PM_TRIANGLE_COLLISION)
			{
				theta = (float) Math.acos(-v / Math.sqrt(u * u + v * v));
				if (isLeft)
				{
					theta = (float) (2 * Math.PI - theta);
					if (Math.toDegrees(theta) > pmStartTime * 15)
					{
						incrementPMTime();
						if (timeChangedCallback != null)
							timeChangedCallback.onTimeChanged(amStartTime, pmStartTime);
					}
					if (Math.toDegrees(theta) < pmStartTime * 15)
					{
						decrementPMTime();
						if (timeChangedCallback != null)
							timeChangedCallback.onTimeChanged(amStartTime, pmStartTime);
					}
				}
				invalidate();
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			touching = false;
			hit = NO_COLLISION;
			invalidate();
		}

		return true;
	}

	public interface OnTimeChangedListener
	{
		public void onTimeChanged(int amTime, int pmTime);
	}

	public void setOnTimeChangedListener(OnTimeChangedListener l)
	{
		this.timeChangedCallback = l;
	}
}