package com.zxc.tigerunlock;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * zaker�Զ���Ч��ҳ��
 * 
 * @author Administrator
 * 
 */
public class PullDoorView extends RelativeLayout {

	private Context mContext;

	private Scroller mScroller;

	private int mScreenWidth = 0;

	private int mScreenHeigh = 0;

	private int mLastDownY = 0;

	private int mCurryY;

	private int mDelY;
	private SoundPool soundPool;// ����һ��SoundPool,SoundPool���ֻ������1M���ڴ�ռ�
	private int musicId;// ����һ��������load������������suondID
	private boolean mCloseFlag = false;
	private static Handler mainHandler = null; // ����Activityͨ�ŵ�Handler����
	private ImageView mImgView;

	public PullDoorView(Context context) {
		super(context);
		mContext = context;
		setupView();
	}

	public PullDoorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupView();
	}

	private void setupView() {

		// ���Interpolator��������ñ�� ������ѡ������е���Ч����Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);

		// ��ȡ��Ļ�ֱ���
		WindowManager wm = (WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeigh = dm.heightPixels;
		mScreenWidth = dm.widthPixels;

		// ������һ��Ҫ���ó�͸������,��Ȼ��Ӱ���㿴���ײ㲼��
		// this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		this.setBackgroundColor(R.color.transparency);
		mImgView = new ImageView(mContext);
		mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// ���������Ļ
		mImgView.setImageResource(R.drawable.bg); // Ĭ�ϱ���
		addView(mImgView);
		// ��������
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// ��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
		musicId = soundPool.load(mContext, R.raw.music, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
	}

	// �����ƶ��ű���
	public void setBgImage(int id) {
		mImgView.setImageResource(id);
	}

	// �����ƶ��ű���
	public void setBgImage(Drawable drawable) {
		mImgView.setImageDrawable(drawable);
	}

	// �ƶ��ŵĶ���
	public void startBounceAnim(int startY, int dy, int duration) {
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastDownY = (int) event.getY();
			System.err.println("ACTION_DOWN=" + mLastDownY);
			return true;
		case MotionEvent.ACTION_MOVE:
			mCurryY = (int) event.getY();
			System.err.println("ACTION_MOVE=" + mCurryY);
			mDelY = mCurryY - mLastDownY;
			// ֻ׼�ϻ���Ч
			if (mDelY < 0) {
				scrollTo(0, -mDelY);
			}
			System.err.println("-------------  " + mDelY);

			break;
		case MotionEvent.ACTION_UP:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			if (mDelY < 0) {
				if (Math.abs(mDelY) > mScreenHeigh / 2.5) {
					// ���ϻ������������Ļ�ߵ�ʱ�� ����������ʧ����
					startBounceAnim(this.getScrollY(), mScreenHeigh, 300);
					mCloseFlag = true;
				} else {
					// ���ϻ���δ���������Ļ�ߵ�ʱ�� �������µ�������
					startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);

				}
			}

			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			Log.i("scroller", "getCurrX()= " + mScroller.getCurrX() + "     getCurrY()=" + mScroller.getCurrY() + "  getFinalY() =  " + mScroller.getFinalY());
			// ��Ҫ���Ǹ��½���
			postInvalidate();
		} else {
			if (mCloseFlag) {
				soundPool.play(musicId, 1, 1, 0, 0, 1);
				mainHandler.obtainMessage(MainActivity.MSG_LOCK_SUCESS).sendToTarget();
				this.setVisibility(View.GONE);
			}
		}
	}

	public static void setMainHandler(Handler handler) {
		mainHandler = handler;// activity���ڵ�Handler����
	}
}
