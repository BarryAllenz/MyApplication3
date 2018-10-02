package com.example.administrator.myapplication.view;

import com.example.administrator.myapplication.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;


/**
 * ˢ�¿���view
 * 
 * @author Hz
 * 
 */
public class RefreshableView extends LinearLayout {

	private static final String TAG = "LILITH";
	private Scroller scroller;
	private View refreshView;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -220;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;

	private RefreshListener refreshListener;

	private String downTextString;
	private String releaseTextString;

	private Long refreshTime = null;
	private int lastX;
	private int lastY;
	// �������
	private boolean isDragging = false;
	// �Ƿ��ˢ�±��
	private boolean isRefreshEnabled = true;
	// ��ˢ���б��
	private boolean isRefreshing = false;
	private TextView refreshing;
	
	private Context mContext;
	public RefreshableView(Context context) {
		super(context);
		mContext = context;
		
	}
	public RefreshableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
		
	}
	private void init() {
		// TODO Auto-generated method stub
		//��������
		scroller = new Scroller(mContext);
		
		//ˢ����ͼ���˵ĵ�view
		 refreshView = LayoutInflater.from(mContext).inflate(R.layout.refresh_top_item, null);
		//ָʾ��view
		 refreshIndicatorView = (ImageView) refreshView.findViewById(R.id.indicator);
		//ˢ��bar
		bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		refreshing=(TextView) refreshView.findViewById(R.id.refreshing);
		//������ʾtext
		 downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);
		//������ʾʱ��
		 timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);
		
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		downTextString = mContext.getResources().getString(R.string.refresh_down_text);
		releaseTextString = mContext.getResources().getString(R.string.refresh_release_text);	
	}




	/**
	 * ˢ��
	 * @param time
	 */
	private void setRefreshText(String time) {
		// TODO Auto-generated method stub
		//timeTextView.setText(time);
	}


	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int y= (int) event.getRawY();
		
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//��¼��y����
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE");
			//y�ƶ�����
			int m = y - lastY;
			if(((m < 6) && (m > -1)) || (!isDragging )){
				 doMovement(m);
			}
			//��¼�´˿�y����
			this.lastY = y;
			break;
			
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "ACTION_UP");
			
			fling();
			
			break;
		}
		return true;
	}


	/**
	 * up�¼�����
	 */
	private void fling() {
		// TODO Auto-generated method stub
		LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		Log.i(TAG, "fling()" + lp.topMargin);
		if(lp.topMargin > 0){//�����˴�����ˢ���¼�
			refresh();	
		}else{
			returnInitState();
		}
	}
	

	
	private void returnInitState() {
		// TODO Auto-generated method stub
		 LayoutParams lp = (LayoutParams)this.refreshView.getLayoutParams();
		 int i = lp.topMargin;
		 scroller.startScroll(0, i, 0, refreshTargetTop);
		 invalidate();
	}
	private void refresh() {
		// TODO Auto-generated method stub
		 LayoutParams lp = (LayoutParams)this.refreshView.getLayoutParams();
		 int i = lp.topMargin;
		 refreshIndicatorView.setVisibility(View.GONE);
		 bar.setVisibility(View.VISIBLE);
		 refreshing.setVisibility(View.VISIBLE);
		 timeTextView.setVisibility(View.GONE);
		 downTextView.setVisibility(View.GONE);
		 scroller.startScroll(0, i, 0, 0-i);
		 invalidate();
		 if(refreshListener !=null){
			 refreshListener.onRefresh(this);
			 isRefreshing = true;
		 }
	}
	
	/**
	 * 
	 */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(scroller.computeScrollOffset()){
			int i = this.scroller.getCurrY();
		      LayoutParams lp = (LayoutParams)this.refreshView.getLayoutParams();
		      int k = Math.max(i, refreshTargetTop);
		      lp.topMargin = k;
		      this.refreshView.setLayoutParams(lp);
		      this.refreshView.invalidate();
		      invalidate();
		}
	}
	/**
	 * ����move�¼�����
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		if(moveY > 0){
			//��ȡview���ϱ߾�
			float f1 =lp.topMargin;
			float f2 = moveY * 0.3F;
			int i = (int)(f1+f2);
			//�޸��ϱ߾�
			lp.topMargin = i;
			//�޸ĺ�ˢ��
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();
		}
		timeTextView.setVisibility(View.VISIBLE);
		if(refreshTime!= null){
			setRefreshTime(refreshTime);
		}
		downTextView.setVisibility(View.VISIBLE);
		
		refreshIndicatorView.setVisibility(View.VISIBLE);
		bar.setVisibility(View.GONE);
		refreshing.setVisibility(View.GONE);
		if(lp.topMargin >  0){
			downTextView.setText(R.string.refresh_release_text);
			refreshIndicatorView.setImageResource(R.drawable.refresh_arrow_up);
		}else{
			downTextView.setText(R.string.refresh_down_text);
			refreshIndicatorView.setImageResource(R.drawable.refresh_arrow_down);
		}
			
	}

	public void setRefreshEnabled(boolean b) {
		this.isRefreshEnabled = b;
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * ˢ��ʱ��
	 * @param refreshTime2
	 */
	private void setRefreshTime(Long time) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ����ˢ���¼�
	 */
	public void finishRefresh(){
		Log.i(TAG, "ִ����=====finishRefresh");
		 LayoutParams lp= (LayoutParams)this.refreshView.getLayoutParams();
		    int i = lp.topMargin;
		    refreshIndicatorView.setVisibility(View.VISIBLE);
		    timeTextView.setVisibility(View.VISIBLE);
		    scroller.startScroll(0, i, 0, refreshTargetTop);
		    invalidate();
		    isRefreshing = false;  
	}

	
	/*�÷���һ���ontouchEvent һ����
	 * (non-Javadoc)
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y= (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			//y�ƶ�����
			int m = y - lastY;

			//��¼�´˿�y����
			this.lastY = y;
		     if(m > 6 &&  canScroll()){
		    	 return true;
		     }
			break;
		case MotionEvent.ACTION_UP:
			
			break;
			
	case MotionEvent.ACTION_CANCEL:
			
			break;
		}
		return false;
	}
	private boolean canScroll() {
		// TODO Auto-generated method stub
		View childView;
		if(getChildCount()>1){
			childView = this.getChildAt(1);
			if(childView instanceof ListView){
				int top =((ListView)childView).getChildAt(0).getTop(); 
				int pad =((ListView)childView).getListPaddingTop(); 
				if((Math.abs(top-pad)) < 3&&
						((ListView) childView).getFirstVisiblePosition() == 0){
					return true;
				}else{
					return false;
				}
			}else if(childView instanceof ScrollView){
				if(((ScrollView)childView).getScrollY() == 0){
					return true;
				}else{
					return false;
				}
			}
			
		}
		return false;
	}
	/**
	 * ˢ�¼����ӿ�
	 * @author Nono
	 *
	 */
	public interface RefreshListener{
		public void onRefresh(RefreshableView view);
	}


}
