package mass.Ranger.View;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.webkit.WebView;
import mass.Ranger.Activity.NavigationActivity;
import mass.Ranger.Activity.TrackingActivity;

import java.util.ArrayList;
/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class PathView extends WebView implements SurfaceHolder.Callback{

	RectF onTracePosition_inner = new RectF();
	RectF onTracePosition_outer = new RectF();
	RectF realTimePosition_outer = new RectF();
	RectF realTimePosition_inner = new RectF();

	Path realTimePath = new Path();
	Path realTimeHeading = new Path();
	Path trackingHeading = new Path();
    Path trackingPath = new Path();
	
	Paint outerPointPaint = new Paint();
	Paint mPaintStep= new Paint();
	Paint mPaintTrace = new Paint();
	Paint mPaintHeadingRad = new Paint();
	Paint mPaintTraceRad = new Paint();
	Paint innerPointPaint = new Paint();
	
	float currXonTrace = 0, currYonTrace = 0;
	float currTraceRad = 0, currHeadingRad = 0;
	float HeadingLen = 50;
	
	int width = getWidth();
	int height = getHeight();
	float realTimeX = width/2;
	float realTimeY = height/2;

	public static int stepLength = 6;
	int penWidth = 10;
	int offsetUnit = 30;
	
	public enum Orientation {
		left, right, up, down
	}
	
	Context mContext;
	
	public PathView(Context context) {
		super(context);
		mContext = context;
		initData();
	}
	
	public PathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initData();
	}
	
	private boolean isZoom = false;
	private float oldDist = 0;
	private PointF midPointF = new PointF();
	
	private float spacing(MotionEvent event) {
         float x = event.getX(0) - event.getX(1);
         float y = event.getY(0) - event.getY(1);
         return FloatMath.sqrt(x * x + y * y);
    }
 
    private void midPoint(PointF point, MotionEvent event) {
         float x = event.getX(0) + event.getX(1);
         float y = event.getY(0) + event.getY(1);
         point.set(x / 2, y / 2);
    }

	// draw a path from a file
    public void renderTrackingPath(ArrayList<Float> link)
    {
		init();
        float trackingX = width / 2;
        float trackingY = height / 2;
        for (Float rad : link) {
            trackingPath.moveTo(trackingX, trackingY);
            trackingX += Math.cos(rad) * stepLength;
            trackingY -= Math.sin(rad) * stepLength;
            trackingPath.lineTo(trackingX, trackingY);
        }
        postInvalidate();
    }

	public void init() {
		onTracePosition_inner = new RectF();
		onTracePosition_outer = new RectF();
		realTimePosition_outer = new RectF();
		realTimePosition_inner = new RectF();
		trackingPath.reset();
		realTimePath.reset();
		realTimeHeading.reset();
		trackingHeading.reset();
		realTimeX = width / 2;
		realTimeY = height / 2;
		currXonTrace = 0;
		currYonTrace = 0;
		currTraceRad = 0;
		currHeadingRad = 0;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		/*
		switch (event.getAction() & MotionEvent.ACTION_MASK){
		
			case MotionEvent.ACTION_DOWN:
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
				isZoom = false;
				break;
				
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				midPoint(midPointF, event);
				isZoom = true;
				break;
				
			case MotionEvent.ACTION_MOVE:
				if (isZoom) {
					
					float newDist = spacing(event);
					
					if (newDist - 5 > oldDist){
						float scale = newDist / oldDist;
						Matrix scaleMatrix = new Matrix();
						RectF rectF = new RectF();
						realTimePath.computeBounds(rectF, true);
						scaleMatrix.setScale(scale, scale, rectF.centerX(), rectF.centerY());
						realTimePath.transform(scaleMatrix);
						trackingPath.transform(scaleMatrix);
						realTimeHeading.transform(scaleMatrix);
						trackingHeading.transform(scaleMatrix);
					}

					if (newDist + 5 < oldDist){
						float scale = newDist / oldDist;
						Matrix scaleMatrix = new Matrix();
						RectF rectF = new RectF();
						realTimePath.computeBounds(rectF, true);
						scaleMatrix.setScale(scale, scale, rectF.centerX(), rectF.centerY());
						realTimePath.transform(scaleMatrix);
						trackingPath.transform(scaleMatrix);
						realTimeHeading.transform(scaleMatrix);
						trackingHeading.transform(scaleMatrix);
					}

					PathMeasure pm = new PathMeasure(realTimePath, false);
				    float aCoordinates[] = {0f, 0f};
				    float dist = pm.getLength() * 10;
				    pm.getPosTan(dist * 0.5f, aCoordinates, null);
				    float particleX = aCoordinates[0];
				    float particleY = aCoordinates[1];
				    realTimeX = particleX;
				    realTimeY = particleY;
					Log.i("dist", String.valueOf(dist));
					Log.i("trans", particleX + " " + particleY);
					pm.getPosTan(dist * 0f, aCoordinates, null);
					Log.i("trans", aCoordinates[0] + " " + aCoordinates[1]);
				    updatePinPoints();
					oldDist = newDist;
					postInvalidate();
				}
				break;
		}*/
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		width = w;
		height = h;
		if (width == oldw && height == oldh)
			return;
		realTimeX = width / 2;
		realTimeY = height / 2;
	}
	
	public void initData(){
		
		outerPointPaint.setDither(true);
		outerPointPaint.setColor(Color.YELLOW);
		outerPointPaint.setStrokeJoin(Paint.Join.ROUND);
        outerPointPaint.setStrokeCap(Paint.Cap.ROUND);
		outerPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outerPointPaint.setStrokeWidth(penWidth * 2);
        outerPointPaint.setAlpha(0x80);
        
        innerPointPaint.setDither(true);
        innerPointPaint.setColor(Color.WHITE);
        innerPointPaint.setStrokeJoin(Paint.Join.ROUND);
        innerPointPaint.setStrokeCap(Paint.Cap.ROUND);
        innerPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        innerPointPaint.setStrokeWidth(penWidth * 2);
        innerPointPaint.setAlpha(0x80);
        
        mPaintStep.setDither(true);
		mPaintStep.setColor(Color.GREEN);
		mPaintStep.setStrokeJoin(Paint.Join.ROUND);
        mPaintStep.setStrokeCap(Paint.Cap.ROUND);
		mPaintStep.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintStep.setStrokeWidth(penWidth);
        mPaintStep.setAlpha(0x80);
        
        mPaintTrace.setDither(true);
		mPaintTrace.setColor(Color.WHITE);
		mPaintTrace.setStrokeJoin(Paint.Join.ROUND);
        mPaintTrace.setStrokeCap(Paint.Cap.ROUND);
		mPaintTrace.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTrace.setStrokeWidth(penWidth);
        mPaintTrace.setAlpha(0x80);
        
        mPaintHeadingRad.setDither(true);
        mPaintHeadingRad.setColor(Color.GREEN);
        mPaintHeadingRad.setStrokeJoin(Paint.Join.ROUND);
        mPaintHeadingRad.setStrokeCap(Paint.Cap.ROUND);
        mPaintHeadingRad.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintHeadingRad.setStrokeWidth(penWidth);
        mPaintHeadingRad.setAlpha(0x80);
        
        mPaintTraceRad.setDither(true);
        mPaintTraceRad.setColor(Color.CYAN);
        mPaintTraceRad.setStrokeJoin(Paint.Join.ROUND);
        mPaintTraceRad.setStrokeCap(Paint.Cap.ROUND);
        mPaintTraceRad.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTraceRad.setStrokeWidth(penWidth);
        mPaintTraceRad.setAlpha(0x80);
    }


	private float RadDis(float rad1, float rad2){
		float radDis = Math.abs(rad1 - rad2);
		radDis %= Math.PI * 2;
		
		if(radDis > Math.PI ){
			radDis = (float)(Math.PI*2 - radDis);
		}   
		return Math.abs(radDis);
	}

	int offsetX = 0;
	int offsetY = 0;
	
	private void updateHeadings(){
		
		float coX = width/2 + currXonTrace + offsetY;
		float coY = height/2 + currYonTrace + offsetX;
		//Log.i("offset", offsetX + " " + offsetY);
		realTimeHeading.reset();
		realTimeHeading.moveTo(coX, coY);
		realTimeHeading.lineTo(
				(float) (coX + HeadingLen * Math.cos(currHeadingRad)),
				(float) (coY - HeadingLen * Math.sin(currHeadingRad)));
		
		trackingHeading.reset();
		trackingHeading.moveTo(coX, coY);
		trackingHeading.lineTo(
				(float) (coX + HeadingLen * Math.cos(currTraceRad)),
				(float) (coY - HeadingLen * Math.sin(currTraceRad)));
		
		// paint color
		if(RadDis(currHeadingRad, currTraceRad) > Math.PI/2){
			mPaintHeadingRad.setColor(Color.RED);
		} else {
			mPaintHeadingRad.setColor(Color.GREEN);
		}
		postInvalidate();
	}
	
	public void addTraceRad(float traceRad){
		currTraceRad = traceRad;
		updateHeadings();
	}
	
	public void addHeadingRad(float traceRad){
		currHeadingRad = traceRad;
		updateHeadings();
	}


	float radius = 3;

	public void addOneStep(int stepcnt, float XonTrace, float YonTrace, float radius)
	{
		currXonTrace = XonTrace;
		currYonTrace = YonTrace;
		Log.i("stepTrace-Radius", String.valueOf(radius));
		if(this.radius <= radius)
			this.radius = radius;
		updatePinPoints();
		updateHeadings();
		postInvalidate();
	}
	
	public void addOneStep(float rad)
	{
		realTimePath.moveTo(realTimeX, realTimeY);
		realTimeX += Math.cos(rad) * stepLength;
		realTimeY -= Math.sin(rad) * stepLength;
		realTimePath.lineTo(realTimeX, realTimeY);
		Log.i("real-time-xy", String.valueOf(realTimeX) + " " + String.valueOf(realTimeY));
		if (realTimeX >= width - offsetUnit)
			overflow(Orientation.down);
		if (realTimeX <= offsetUnit)
			overflow(Orientation.up);
		if (realTimeY >= height - offsetUnit)
			overflow(Orientation.right);
		if (realTimeY <= offsetUnit)
			overflow(Orientation.left);

		updatePinPoints();
		updateHeadings();
		postInvalidate();
	}
	
	public void updatePinPoints()
	{
		realTimePosition_outer.left =  realTimeX - penWidth;
		realTimePosition_outer.right = realTimeX + penWidth;
		realTimePosition_outer.top = realTimeY - penWidth;
		realTimePosition_outer.bottom = realTimeY + penWidth;
		realTimePosition_inner.left =  realTimeX - penWidth / 2;
		realTimePosition_inner.right = realTimeX + penWidth / 2;
		realTimePosition_inner.top = realTimeY - penWidth / 2;
		realTimePosition_inner.bottom = realTimeY + penWidth / 2;

		onTracePosition_inner.left =  width/2 + currXonTrace - 3 + offsetY;
		onTracePosition_inner.right = width/2 + currXonTrace + 3 + offsetY;
		onTracePosition_inner.top = height/2 + currYonTrace - 3 + offsetX;
		onTracePosition_inner.bottom = height/2 + currYonTrace + 3 + offsetX;
		onTracePosition_outer.left =  width/2 + currXonTrace - this.radius + offsetY;
		onTracePosition_outer.right = width/2 + currXonTrace + this.radius + offsetY;
		onTracePosition_outer.top = height/2 + currYonTrace - this.radius + offsetX;
		onTracePosition_outer.bottom = height/2 + currYonTrace + this.radius + offsetX;
	}


	public void overflow(Orientation ori)
	{
		switch (ori) {
			case left:
				offsetX += offsetUnit;
				realTimePath.offset(0, offsetUnit);
				trackingPath.offset(0, offsetUnit);
				realTimeY += offsetUnit;
				break;
				
			case right:
				offsetX -= offsetUnit;
				realTimePath.offset(0, -offsetUnit);
				trackingPath.offset(0, -offsetUnit);
				realTimeY -= offsetUnit;
				break;
				
			case up:
				offsetY += offsetUnit;
				realTimePath.offset(offsetUnit, 0);
				trackingPath.offset(offsetUnit, 0);
				realTimeX += offsetUnit;
				break;
				
			case down:
				offsetY -= offsetUnit;
				realTimePath.offset(-offsetUnit, 0);
				trackingPath.offset(-offsetUnit, 0);
				realTimeX -= offsetUnit;
				break;
	
			default:
				break;
		}

	}
	
	protected void onDraw( Canvas canvas )
	{
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.parseColor("#40FFFFFF"));
		canvas.drawRect(0, 0, getWidth()-1, getHeight()-1, paint);
		
		canvas.drawPath(realTimePath, mPaintStep);
        canvas.drawPath(trackingPath, mPaintTrace);
		if (NavigationActivity.serviceOn) {
			//canvas.drawPath(realTimeHeading, mPaintHeadingRad);
			//canvas.drawPath(trackingHeading, mPaintTraceRad);
			canvas.drawOval(onTracePosition_inner, innerPointPaint);
			canvas.drawOval(onTracePosition_outer, outerPointPaint);
		}
		if (TrackingActivity.serviceOn) {
			canvas.drawOval(realTimePosition_outer, outerPointPaint);
			canvas.drawOval(realTimePosition_inner, innerPointPaint);
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
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
