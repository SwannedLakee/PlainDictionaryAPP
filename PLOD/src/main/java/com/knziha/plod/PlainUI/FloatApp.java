package com.knziha.plod.PlainUI;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.appcompat.app.GlobalOptions;

import com.knziha.plod.plaindict.AgentApplication;
import com.knziha.plod.plaindict.CMN;
import com.knziha.plod.plaindict.PDICMainActivity;
import com.knziha.plod.plaindict.R;
import com.knziha.plod.widgets.ViewUtils;
import com.knziha.plod.widgets.WindowLayout;

public class FloatApp implements View.OnTouchListener, View.OnClickListener {
	public final WindowManager wMan;
	public final AgentApplication app;
	public WindowManager.LayoutParams lpLand;
	public WindowManager.LayoutParams lpPort;
	public WindowManager.LayoutParams lp;
	public View floatingView;
	public FloatBtn floatBtn;
	public WindowLayout view;
	public ViewGroup contentView;
	public ViewGroup appContentView;
	public PDICMainActivity a;
	public boolean landScape;
	public DisplayMetrics dm = new DisplayMetrics();
	private int statusBarHeight;
	private int padding;
	private ViewGroup titleBar;
	
	public FloatApp(PDICMainActivity a) {
		this.a = a;
		Context context = a.getApplicationContext();
		this.app = (AgentApplication) a.getApplication();
		this.wMan = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}
	
	public void floatWindow() {
		if (view != null) {
			if (view.getParent()!=null) {
				wMan.removeView(view);
			}
		} else {
			view = (WindowLayout) a.getLayoutInflater().inflate(R.layout.multiwindow_root, null);
			view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					CMN.Log("onLayoutChange::");
					Display display = ((WindowManager) a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					int angle = display.getRotation();
					boolean land = angle==Surface.ROTATION_90||angle==Surface.ROTATION_270;
					if (land!=landScape) {
						display.getMetrics(dm);
						statusBarHeight = CMN.getStatusBarHeight(a.getApplicationContext());
						landScape = land;
						calcLayout();
						wMan.removeView(view);
						wMan.addView(view, lp);
					}
				}
			});
			view.floatApp = this;
			ViewGroup views = (ViewGroup) view.getChildAt(0);
			padding = (int) (GlobalOptions.density*8);
			//views.getChildAt(0).setOnTouchListener(this);
			titleBar = (ViewGroup) views.getChildAt(0);
			for (int i = 0; i < titleBar.getChildCount(); i++) {
				View child = titleBar.getChildAt(i);
				if(child.getId()!=0) child.setOnClickListener(this);
				child.setOnTouchListener(this);
			}
			contentView = ((ViewGroup)views.getChildAt(1));
		}
		floatingView = view;
		//view.setOnTouchListener(this);
		View v = a.UIData.root;
		v.setFitsSystemWindows(false);
		v.setPadding(0,0,0,0);
		
		if (appContentView==null) {
			appContentView = (ViewGroup) v.getParent();
		}
		ViewUtils.removeView(v);
		contentView.addView(v);
		
		Display display = ((WindowManager) a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int angle = display.getRotation();
		display.getMetrics(dm);
		statusBarHeight = CMN.getStatusBarHeight(a.getApplicationContext());
		landScape = angle==Surface.ROTATION_90||angle==Surface.ROTATION_270;
		calcLayout();
		
		try {
			wMan.addView(view, lp);
			a.mDialogType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
					? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
					: WindowManager.LayoutParams.TYPE_PHONE;
			app.floatApp = this;
		} catch (Exception e) {
			CMN.Log(e);
			toggle(true);
		}
	}
	
	public void toggle(boolean close) {
		if (isFloating()) {
			wMan.removeView(floatingView);
			ViewUtils.addViewToParent(a.UIData.root, appContentView);
			a.UIData.root.setFitsSystemWindows(true);
			a.UIData.root.setPadding(0,CMN.getStatusBarHeight(a),0,0);
			a.mDialogType = WindowManager.LayoutParams.TYPE_APPLICATION;
			a.moveTaskToFront();
			app.floatApp = null;
			floatingView = null;
			a.onSizeChanged();
		} else if (!close) {
			floatWindow();
			a.onSizeChanged();
			a.moveTaskToBack(true);
		}
	}
	
	public void close() {
		if (isFloating()) {
			wMan.removeView(floatingView);
			app.floatApp = null;
		}
	}
	
	float orgX;
	float orgY;
	boolean moved;
	int x;
	int y;
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		int e=ev.getActionMasked();
		if (e==MotionEvent.ACTION_OUTSIDE) {
			a.showT("ACTION_OUTSIDE");
			enableKeyBoard(false);
			return false;
		}
		else if (e==MotionEvent.ACTION_DOWN) {
			if (!view.resizing) {
				orgX = ev.getRawX();
				orgY = ev.getRawY();
				x = lp.x;
				y = lp.y;
				moved = false;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					view.suppressLayout(true);
				}
			}
		}
		else if (e==MotionEvent.ACTION_MOVE) {
			if (!moved && Math.max(Math.abs(ev.getRawX()-orgX), Math.abs(ev.getRawY()-orgY))>GlobalOptions.density*5) {
				moved = true;
			}
			if (moved) {
				lp.x = (int) (x + ev.getRawX() - orgX);
				lp.y = (int) (y + ev.getRawY() - orgY);
				updateLayout();
			}
		}
		else if (e==MotionEvent.ACTION_UP) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				view.suppressLayout(false);
			}
			if (moved) {
				moved = false;
				ViewUtils.preventDefaultTouchEvent(v, 0, 0);
				return true;
			}
		}
		return moved;
	}
	
	public final boolean isAppFloating() {
		return view!=null && view.getParent()!=null;
	}
	
	public final boolean isFloating() {
		return floatingView!=null && floatingView.getParent()!=null;
	}
	
	public final void enableKeyBoard(boolean enable) {
		if (isFloating()) {
			WindowManager.LayoutParams lp = (WindowManager.LayoutParams) floatingView.getLayoutParams();
			final int mask = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			if (enable ^ (lp.flags&mask)==0) {
				if (enable) {
					lp.flags &= ~mask;
				} else {
					lp.flags |= mask;
				}
				wMan.updateViewLayout(floatingView, lp);
			}
		}
	}
	
	public final void updateView(RectF frameOffsets) {
		lp.x = (int) frameOffsets.left;
		lp.y = (int) (frameOffsets.top - CMN.statusBarHeight);
		lp.width = (int) frameOffsets.width();
		lp.height = (int) frameOffsets.height();
		wMan.updateViewLayout(view, lp);
	}
	
	public void expand(boolean collapse) {
		try {
			if (isFloating()) {
				if (collapse) {
					if (isAppFloating()) { // 最小化
						wMan.removeView(floatingView);
						getFloatBtn().reInitBtn(a, 0);
						floatingView = floatBtn.view;
					}
				} else if (!isAppFloating()) { // 复原
					wMan.removeView(floatingView);
					floatWindow();
				}
			}
		} catch (Exception e) {
			CMN.debug(e);
		}
	}
	
	public FloatBtn getFloatBtn() {
		if (floatBtn==null) {
			floatBtn = a.floatBtn = new FloatBtn(a, app);
		}
		return floatBtn;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.min:
				expand(true);
			break;
			case R.id.max:
				toggle(true);
			break;
			case R.id.close:
				a.showExitDialog(false);
			break;
		}
	}
	
	/** 初始化坐标布局 */
	private void calcLayout() {
		lp = landScape?lpLand:lpPort;
		if (lp==null) {
			DisplayMetrics dm = a.dm;
			((WindowManager)a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
			//CMN.debug("calcLayout::", dm.widthPixels, dm.heightPixels);
			int width = dm.widthPixels;
			if (landScape) {
				if (width/2>dm.heightPixels-dm.density*100) {
					width/=2;
				}
				else if (width>dm.heightPixels) {
					width=dm.heightPixels;
				}
			}
			width = (int) (width*5/6.f);
			int height = Math.min((int) (width*5/3.f), dm.heightPixels-CMN.getStatusBarHeight(a));
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
					width, height
					, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
					? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
					: WindowManager.LayoutParams.TYPE_PHONE
					, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
					| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
					| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
					, PixelFormat.RGBA_8888);
			lp.gravity = Gravity.START | Gravity.TOP;
			lp.x = (dm.widthPixels-width)/2;
			lp.y = (dm.heightPixels-height)/2;
			this.lp = lp;
			if (landScape) {
				lpLand = lp;
			} else {
				lpPort = lp;
			}
		}
	}
	
	private void updateLayout() {
		if (lp.x+padding<0) lp.x=-padding;
		else if(lp.x+titleBar.getHeight()*1.5f+padding>dm.widthPixels) lp.x=(int) (dm.widthPixels-titleBar.getHeight()*1.5f-padding);
		if (lp.y+4*padding<statusBarHeight) lp.y=statusBarHeight-4*padding;
		else if(lp.y+titleBar.getHeight()+4*padding>dm.heightPixels) lp.y=(int) (dm.heightPixels-titleBar.getHeight()-4*padding);
		wMan.updateViewLayout(view, lp);
	}
}
