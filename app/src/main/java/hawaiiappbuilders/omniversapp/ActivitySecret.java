package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivitySecret extends BaseActivity {
    public static final String TAG = ActivitySecret.class.getSimpleName();
    DrawingView dv;
    private Paint mPaint;

    /*
     *
     * You are creating a view class then extends View.
     * You override the onDraw().
     * You add the path of where finger touches and moves.
     * You override the onTouch() of this purpose.
     * In your onDraw() you draw the paths using the paint of your choice.
     * You should call invalidate() to refresh the view.
     *
     * */
    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.RED);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_secret);
        // mDetector = new GestureDetector(this, new MyGestureListener());
       /* layoutFingerStart = findViewById(R.id.layoutFingerStart);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // pass the events to the gesture detector
                // a return value of true means the detector is handling it
                // a return value of false means the detector didn't
                // recognize the event
                float x = event.getX();
                float y = event.getY();

                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerId = event.getPointerId(index);

                *//**
         * You're only tracking the up and down events.
         * Track the ACTION_MOVE event too.
         * Beware that it will track continuously,
         * even if the person's finger isn't apparently moving.
         * Your code should go something like this:
         *
         * ACTION_DOWN: Store position.
         *
         * ACTION_MOVE: If position is different from stored position
         * then draw a line from stored position to current position,
         * and update stored position to current.
         *
         * ACTION_UP: Stop.
         *
         * In the ACTION_MOVE bit, it might be a good idea to check
         * if the position is at least 2 or 3 pixels away
         * from the stored position.
         * If you're going to store all of the plot points,
         * so you can do something with the data later,
         * then maybe increase that to 10 pixels
         * so you don't end up with hundreds of points for a simple line.
         *//*
         *//* switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("onTouch", "action down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("onTouch", "moving at (" + x + ", " + y + ")");
                        showToastMessage("moving at (" + x + ", " + y + ")");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("onTouch", "action up");
                        break;
                }
                return true;
                // return mDetector.onTouchEvent(event);*//*
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        if(mVelocityTracker == null) {
                            // Retrieve a new VelocityTracker object to watch the
                            // velocity of a motion.
                            mVelocityTracker = VelocityTracker.obtain();
                        }
                        else {
                            // Reset the velocity tracker back to its initial state.
                            mVelocityTracker.clear();
                        }
                        // Add a user's movement to the tracker.
                        mVelocityTracker.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mVelocityTracker.addMovement(event);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        mVelocityTracker.computeCurrentVelocity(1000);
                        // Log velocity of pixels per second
                        // Best practice to use VelocityTrackerCompat where possible.
                        Log.d("", "X velocity: " + mVelocityTracker.getXVelocity(pointerId));
                        Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity(pointerId));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Return a VelocityTracker object back to be re-used by others.
                        mVelocityTracker.recycle();
                        break;
                }
                return true;
            }
        };

*/
       /* GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        // gestures.addOnGesturePerformedListener(this);
        gestures.setOnTouchListener(touchListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gestures.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
                @Override
                public boolean onCapturedPointer (View view, MotionEvent motionEvent) {
                    // Get the coordinates required by your app
                    float horizontalOffset = motionEvent.getX();
                    // Use the coordinates to update your view and return true if the event was
                    // successfully processed
                    return true;
                }
            });
        }
        gestures.setGestureVisible(false);*/

        dv = new DrawingView(mContext);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    /*@Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

    }

    public static class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("Gesture", "onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i("Gesture", "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("Gesture", "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("Gesture", "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("Gesture", "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("Gesture", "onFling");
            return false;
        }
    }*/
}
