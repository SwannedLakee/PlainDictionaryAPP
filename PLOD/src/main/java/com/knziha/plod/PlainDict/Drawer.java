package com.knziha.plod.PlainDict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.knziha.plod.PlainDict.R;
import com.knziha.filepicker.model.DialogConfigs;
import com.knziha.filepicker.model.DialogProperties;
import com.knziha.filepicker.model.DialogSelectionListener;
import com.knziha.filepicker.view.FilePickerDialog;
import com.knziha.plod.dictionarymodels.mdict;
import com.knziha.plod.dictsmanager.files.mFile;
import com.knziha.plod.widgets.CheckedTextViewmy;
import com.knziha.plod.settings.SettingsActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.GlobalOptions;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;



/**
 * @author KnIfER
 *
 */
public class Drawer extends Fragment implements
		OnClickListener, OnDismissListener, OnCheckedChangeListener, OnLongClickListener {
     AlertDialog d;
    
     String[] hints;
	 private ListView mDrawerList;
	 View mDrawerListView;
	 private View mFragmentContainerView;
	 MyAdapter myAdapter;
	 boolean exitAll=false;
	 
	public EditText etAdditional;

	SwitchCompat sw1,sw2,sw3,sw4,sw5;

	View HeaderView;

	ViewGroup FooterView;
	private String filepickernow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	public Drawer() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = inflater.inflate(R.layout.navigation_drawer_fc, container,false);
		//mDrawerListView.setOnClickListener(this);
		FooterView = mDrawerListView.findViewById(R.id.footer);
		FooterView.findViewById(R.id.menu_item_setting).setOnClickListener(this);
		mDrawerListView.findViewById(R.id.menu_item_exit).setOnClickListener(this);
		mDrawerListView.findViewById(R.id.menu_item_exit).setOnLongClickListener(this);
		mDrawerList = (ListView) mDrawerListView.findViewById(R.id.left_drawer);

		String[] items = getResources().getStringArray(R.array.drawer_items);
		
        myAdapter = new MyAdapter(Arrays.asList(items));
        
        mDrawerList.setAdapter(myAdapter);
        HeaderView = inflater.inflate(R.layout.drawer_fc_header, null);
        mDrawerList.addHeaderView(HeaderView);
        //etAdditional = (EditText)mDrawerList.findViewById(R.id.etAdditional);
		//CMN.show("onCreateView");
        mDrawerListView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
        	int oldWidth;
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
					int oldRight, int oldBottom) {
				int drawerWidth=right-left;
				int width = (drawerWidth - sw1.getWidth()*5)/6;
				MarginLayoutParams lp = (MarginLayoutParams) sw1.getLayoutParams();
				lp.leftMargin = width;
				if(drawerWidth!=oldWidth)sw1.setLayoutParams(sw1.getLayoutParams());
				lp = (MarginLayoutParams) sw2.getLayoutParams();
				lp.leftMargin = width;
				if(drawerWidth!=oldWidth)sw2.setLayoutParams(sw2.getLayoutParams());
				lp = (MarginLayoutParams) sw3.getLayoutParams();
				lp.leftMargin = width;
				if(drawerWidth!=oldWidth)sw3.setLayoutParams(sw3.getLayoutParams());
				lp = (MarginLayoutParams) sw4.getLayoutParams();
				lp.leftMargin = width;
				if(drawerWidth!=oldWidth)sw4.setLayoutParams(sw4.getLayoutParams());
				lp = (MarginLayoutParams) sw5.getLayoutParams();
				lp.leftMargin = width;
				if(drawerWidth!=oldWidth)sw5.setLayoutParams(sw5.getLayoutParams());
				oldWidth=drawerWidth;
			}
		});
		return mDrawerListView;
	}



    class MyAdapter extends ArrayAdapter<String> {
        
		public MyAdapter(List<String> mdicts) {
			super(getActivity(),R.layout.listview_item0, R.id.text, mdicts);
        }
		boolean show_hints = true;
		public void notifyDataSetChangedX() {
			show_hints = true;//a.opt.isDrawer_Showhints();
			super.notifyDataSetChanged();
		}
        @Override
        public boolean areAllItemsEnabled() {
          return false;
        }
        @Override
        public int getCount() {
          return super.getCount()-1;
        }
        @Override
        public boolean isEnabled(int position) {
    		return !getItem(position).equals("d"); // 如果-开头，则该项不可选
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
    	  position+=1;
          if(getItem(position).equals("d")){//是标签项
        	  return (convertView!=null && convertView.getTag()==null)?convertView:LayoutInflater.from(getContext()).inflate(R.layout.listview_sep, null);
          }
    	  viewHolder vh = null;
    	  if(convertView!=null)
    		  vh=(viewHolder)convertView.getTag();
          if(vh==null) {
        	  	vh=new viewHolder();
        	  	convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item0, null);
        	  	convertView.setTag(vh);
        	  	vh.title = (TextView) convertView.findViewById(R.id.text);
        	  	vh.subtitle = (TextView) convertView.findViewById(R.id.subtext);
        	  	vh.subtitle.setTextColor(ContextCompat.getColor(a, R.color.colorHeaderBlue));
          }
          if( vh.title.getTextColors().getDefaultColor()!=a.AppBlack) {
        	  if(a.AppBlack==Color.WHITE)
        	  	    convertView.getBackground().setColorFilter(GlobalOptions.NEGATIVE);
	        	else
	        		convertView.getBackground().setColorFilter(null);
              vh.title.setTextColor(a.AppBlack);
          }
          
  		  vh.title.setText(getItem(position));
          vh.subtitle.setText(null);
  		  if(show_hints) {
  			  getExtraHints();
  			  if(!hints[position].equals("d"))
  				  vh.subtitle.setText(hints[position]);
  		  }
  		  convertView.setTag(R.id.position,position);
	  	  convertView.setOnClickListener(Drawer.this);

          return convertView;
        }
      }
    static class viewHolder{
    	private TextView title;
    	private TextView subtitle;
    }

    
	@Override
	public View getView() {
		return super.getView();
	}

	public void getExtraHints() {
		if(hints==null)
			hints = getResources().getStringArray(R.array.drawer_hints);
	}

	PDICMainActivity a;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		a = ((PDICMainActivity)getActivity());
		myAdapter.show_hints = true;//a.opt.isDrawer_Showhints();
		a.drawerFragment = this;
		if(myAdapter.show_hints)
			getExtraHints();
		//mDrawerListView.setBackgroundColor(a.AppWhite);
		//HeaderView.setBackgroundColor(a.AppWhite);
		//FooterView.setBackgroundColor(a.AppWhite);
		

        sw1 = (SwitchCompat)HeaderView.findViewById(R.id.sw1);
		sw1.setOnCheckedChangeListener(this);
        sw1.setChecked(a.opt.isFullScreen());
		sw1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});

        sw2 = (SwitchCompat)HeaderView.findViewById(R.id.sw2);
        sw2.setOnCheckedChangeListener(this);
        sw2.setChecked(!a.opt.isContentBow());
        
        sw3 = (SwitchCompat)HeaderView.findViewById(R.id.sw3);
        sw3.setOnCheckedChangeListener(this);
        sw3.setChecked(!a.opt.isViewPagerEnabled());
        
		sw4 = ((SwitchCompat) HeaderView.findViewById(R.id.sw4));
		sw4.setChecked(a.opt.getInDarkMode());
		sw4.setOnCheckedChangeListener(this);

		sw5 = ((SwitchCompat) HeaderView.findViewById(R.id.sw5));
		sw5.setChecked(a.opt.get_use_volumeBtn());
		sw5.setOnCheckedChangeListener(this);
		
		if(a.opt.getInDarkMode()) {
			mDrawerListView.setBackgroundColor(Color.BLACK);
			HeaderView.setBackgroundColor(a.AppWhite);
			FooterView.setBackgroundColor(a.AppWhite);
		}
		//test groups
		//View v = new View(a);v.setTag(R.id.position,7);onClick(v);
		//View v = new View(a);v.setTag(R.id.position,9);onClick(v);
	}

	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	private final String infoStr = "感谢使用平典，安卓平台的检索查词软件。\r\n\r\n相关软件：Bluedict(启发) 、MdictPC(起源) \r\n\r\n[开源组件] \r\n\r\n[帮助与问题反馈(贴吧)]";
	private final String infoStr_en = "Thanks for choosing/tesing/playing Plain Dictionary for Android。\r\n\r\nRelated Softwares：Bluedict(Inspired by) 、MdictPC(Originates in) \r\n\r\n[OpenSource Compenents] \r\n\r\n[Feedback1(Tieba)]\r\n\r\n[Feedback2(Github)]";

	boolean isDirty;
	
	@Override
	public void onClick(View v) {
		if(!a.systemIntialized) return;
		int id = v.getId();
		switch(id) {
			case R.id.menu_item_setting:
			PackageInfo packageInfo;
	        
				//a.mDrawerLayout.closeDrawer(Gravity.LEFT);
				final View dv = a.inflater.inflate(R.layout.dialog_about,null);
				
				final SpannableStringBuilder ssb = new SpannableStringBuilder(infoStr);
				final String languageName = Locale.getDefault().getLanguage();

		        if(!languageName.equals("zh")){
		        	ssb.clear(); ssb.append(infoStr_en);
		        }
		        
				int start = ssb.toString().indexOf("欢迎打");
				final TextView tv = ((TextView)dv.findViewById(R.id.resultN));
				tv.setPadding(0, 0, 0, 50);
				if(start!=-1) {
					int len=5;
			        if(!languageName.equals("zh")){
			        	ssb.clear(); ssb.append(infoStr_en);
			        	start = ssb.toString().indexOf("Please do");
			        	len=34;
			        }
					ssb.setSpan(new ClickableSpan() {
						@Override
						public void onClick(View widget) {
							ssb.clear(); ssb.append("捐赠:\n\n	🐏1\n\n	\n	🐏5\n\n	\n	🐏10\n\n	\n	🐏自定义\n\n\n");
							if(!languageName.equals("zh")){
								ssb.clear(); ssb.append("Donate:\n\n	🐏1\n\n	\n	🐏5\n\n	\n	🐏10\n\n	\n	🐏Customise\n\n\n");
							}
							int idx = ssb.toString().indexOf("🐏");
							int iiddxx=0;
							while(idx>0) {
								final int cc = iiddxx;
								ssb.setSpan(new ClickableSpan() {
									@Override
									public void onClick(View widget) {
										launchDonate(cc);
									}},idx,ssb.toString().indexOf("\n",idx),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								idx =  ssb.toString().indexOf("🐏",idx+3);
								iiddxx++;
							}
								
							tv.setText(ssb);
						}
						String[] qrcodes =  new String[]{"HTTPS://QR.ALIPAY.COM/FKX02170S9R0BFEPOVHKBA",
								"HTTPS://QR.ALIPAY.COM/FKX04640O8PJTGIRHFKE73",
								"HTTPS://QR.ALIPAY.COM/FKX07825BSNCJQYUYBVAE5",
								"HTTPS://QR.ALIPAY.COM/FKX01270UNJTIC0LONPFE1"};
						private void launchDonate(int idx) {
							a.show(R.string.donate);
							if(hasInstalledAlipayClient(getActivity())) {
								Intent i = new Intent(Intent.ACTION_VIEW);
								
								String url = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode="+qrcodes[idx];
								i.setData(Uri.parse(url));
								startActivity(i);
							}else {
								a.show(R.string.donatefail);
							}						
						}}, start, start+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				int startss = ssb.toString().indexOf("[");
				int endss = ssb.toString().indexOf("]",startss);
				
				ssb.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						ssb.clear();
						ssb
						   .append("KnIfER/mdict-java (GPL)").append("\n\n")
						   .append("Xiaoqiang Wang/python mdict-analysis (bitbucket2017)").append("\n\n")
						   .append("fengdh/mdict-js (Github2018)").append("\n\n")
						   .append("AndreiD/TSnackBar (Github2019)").append("\n\n")
						   .append("jess-anders/two-way-gridview (Github2019)").append("\n\n")
						   .append("IDFDeveloper/android-resize-view (Github2019)").append("\n\n")
						   .append("Angads25/android-filepicker (apache2)").append("\n\n")
						   .append("com.mobeta.android.dslv(Dragsort ListView)(Github2018)").append("\n\n")
						   .append("danoz73/RecyclerViewFastScroller (apache2)").append("\n\n")
						   .append("org.jvcompress.lzo(mini lzo)(GPL)").append("\n\n")
						   .append("org.apache.commons.lang (apache2)").append("\n\n")
						   .append("Powered by Android and Eclispe").append("\n\n")
						   ;
						tv.setText(ssb.toString());
						tv.setTextIsSelectable(true);
					}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				

				startss = ssb.toString().indexOf("[",endss);
				endss = ssb.toString().indexOf("]",startss);
				
				ssb.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						Uri uri = Uri.parse("https://tieba.baidu.com/f?kw=%E5%B9%B3%E5%85%B8app");
		    			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    			startActivity(intent);
					}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				
				startss = ssb.toString().indexOf("[",endss);
				endss = ssb.toString().indexOf("]",startss);
				if(endss>startss && startss>0)
				ssb.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						Uri uri = Uri.parse("https://tieba.baidu.com/f?kw=%E5%B9%B3%E5%85%B8app");
		    			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    			startActivity(intent);
					}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				
				tv.setText(ssb);
				tv.setMovementMethod(LinkMovementMethod.getInstance());
				AlertDialog.Builder builder2 = new AlertDialog.Builder(a);
				builder2.setView(dv);
				final AlertDialog d = builder2.create();
				d.setCanceledOnTouchOutside(true);
				//d.setCanceledOnTouchOutside(false);
				d.setOnDismissListener(new AlertDialog.OnDismissListener(){
					@Override
					public void onDismiss(DialogInterface dialog) {
					}
				});
				dv.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						d.dismiss();
					}});
				d.show();
				android.view.WindowManager.LayoutParams lp = d.getWindow().getAttributes();  //获取对话框当前的参数值
				lp.height = -2;
				d.getWindow().setAttributes(lp);
				
				return;
			case R.id.menu_item_exit://退出
				//a.mDrawerLayout.closeDrawer(Gravity.LEFT);
				final View dv1 = a.inflater.inflate(R.layout.dialog_about,null);
				
				final SpannableStringBuilder ssb1 = new SpannableStringBuilder(getResources().getString(R.string.warn_exit1));
				int start1 = ssb1.toString().indexOf("[");
				final TextView tv1 = ((TextView)dv1.findViewById(R.id.resultN));
				((TextView)dv1.findViewById(R.id.title)).setText(R.string.warn_exit0);
				tv1.setPadding(0, 0, 0, 50);
				ssb1.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						if(a.deleteHistory()) 
							a.show(R.string.clearsucc);
						else
							a.show(R.string.clearfail);
					}					
					}, start1,  ssb1.toString().indexOf("]",start1)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start1 = ssb1.toString().indexOf("(");
				final int stf =start1;
				exitAll=false;
				ssb1.setSpan(new ClickableSpan() {
					char[] acc=new char[1];
					@Override
					public void onClick(View widget) {
						ssb1.getChars(stf, stf+1, acc, 0);
						if(acc[0]=='(') {
							ssb1.replace(stf, stf+1, "[", 0, 1);
							int xx = ssb1.toString().indexOf(")",stf);
							ssb1.replace(xx, xx+1, "]", 0, 1);
							final String languageName = Locale.getDefault().getLanguage();
							ssb1.insert(stf+1, languageName.equals("zh")?"将 ":"Will ");
							exitAll=true;
						}else {
							ssb1.replace(stf, stf+1, "(", 0, 1);
							int xx = ssb1.toString().indexOf("]",stf);
							ssb1.replace(xx, xx+1, ")", 0, 1);
							ssb1.delete(stf+1, ssb1.toString().indexOf(" ",stf+1)+1);
						}
						tv1.setText(ssb1);
					}					
					}, start1,  ssb1.toString().indexOf(")",start1)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tv1.setText(ssb1);
				tv1.setMovementMethod(LinkMovementMethod.getInstance());
				AlertDialog.Builder builder21 = new AlertDialog.Builder(a);
				builder21.setView(dv1);
				final AlertDialog d1 = builder21.create();
				d1.setCanceledOnTouchOutside(true);
				d1.setOnDismissListener(new AlertDialog.OnDismissListener(){
					@Override
					public void onDismiss(DialogInterface dialog) {
					}
				});
				dv1.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//a.finish();
						//d1.dismiss();
						if(exitAll) {
							//int xx=1/0;
							 /*
					        // 1. 通过Context获取ActivityManager
					        ActivityManager activityManager = (ActivityManager) a.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

					        // 2. 通过ActivityManager获取任务栈
					        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
					        
					        // 3. 逐个关闭Activity
					        for (ActivityManager.AppTask appTask : appTaskList) {
					            appTask.finishAndRemoveTask();
					        }
					        */
							ActivityManager manager = (ActivityManager) a.getSystemService(Context.ACTIVITY_SERVICE); 
							manager.restartPackage(a.getPackageName());
							manager.killBackgroundProcesses(a.getPackageName()); 
							android.os.Process.killProcess(android.os.Process.myPid());
					        //android.os.Process.killProcess(android.os.Process.myPid());
							//System.exit(0);
						}
					}});
				d1.show();
				
				return;
		}
		int position = (int) v.getTag(R.id.position);
		LayoutParams attr;
		int BKHistroryVagranter;
		switch(position) {
			case 1://模糊搜索
				a.switchToSearchModeDelta(100);
				a.mDrawerLayout.closeDrawer(Gravity.LEFT);
				a.etSearch.requestFocus();
				((InputMethodManager)a.getSystemService( Context.INPUT_METHOD_SERVICE )).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			break;
			case 2://全文搜索
				a.switchToSearchModeDelta(-100);
				a.mDrawerLayout.closeDrawer(Gravity.LEFT);
				a.etSearch.requestFocus();
				((InputMethodManager)a.getSystemService( Context.INPUT_METHOD_SERVICE )).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			break;
			case 4://书签历史
				final String[] items = new String[20];
				final int[] pos = new int[20];
				BKHistroryVagranter = a.opt.getInt("bkHVgrt",0);// must 0<..<20
				BKHistroryVagranter+=20;
				int cc=0;
				for(int i=0;i<20;i++) {
					int VagranterI = (BKHistroryVagranter-i)%20;
					items[cc] = a.opt.getString("bkh"+VagranterI);
					if(items[cc]!=null) {
						int deli = items[cc].indexOf("/?Pos=");
						pos[cc] = Integer.valueOf(items[cc].substring(deli+"/?Pos=".length()));
						items[cc] = items[cc].substring(0, deli);
					}else {
						items[cc]="N.A.";
						pos[cc] = -2;
					}
					cc++;
				}
				final HashMap<String, mdict> mdictInternal = new HashMap<>();
				for(mdict mdTmp:a.md) {
					mdictInternal.put(mdTmp.getPath(), mdTmp);
				}
				
		        AlertDialog.Builder builder = new AlertDialog.Builder(a);
		        builder.setTitle(R.string.bookmarkH);
		        builder.setSingleChoiceItems(new String[] {}, 0,
		                new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, final int position) {
		                    	d.getListView().postDelayed(new Runnable() {
									@Override
									public void run() {
				                    	String id = items[position];
										mdict mdTmp = mdictInternal.get(id);
										if(mdTmp!=null) {
											if(!a.md.contains(mdTmp)) {
												a.md.add(mdTmp);
												a.switchToDictIdx(a.md.size()-1);
											}else {
												a.switchToDictIdx(a.md.indexOf(mdTmp));
											}
											if(a.pickDictDialog!=null) a.pickDictDialog.isDirty=true;
											int toPos = pos[position];
											a.bWantsSelection=false;
											a.bNeedReAddCon=false;
											a.adaptermy.onItemClick(toPos);
											a.lv.setSelection(toPos);
											d.hide();
										}}
		                    		
		                    	}, 0);
		                    }
		                });
		        d=builder.create();
		        d.show();
		        
		        //attr = d.getWindow().getAttributes();
        		//attr.dimAmount=0;
        		//d.getWindow().setAttributes(attr);
    	    	d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        		if(a.AppBlack==Color.WHITE) {
        	    	//d.setTitle(Html.fromHtml("<span style='color:#ffffff;'>"+getResources().getString(R.string.loadconfig)+"</span>"));
        	        View tv = d.getWindow().findViewById(Resources.getSystem().getIdentifier("alertTitle","id", "android"));
        			if(TextView.class.isInstance(tv))((TextView) tv).setTextColor(Color.WHITE);
        	    	//d.getWindow().setBackgroundDrawableResource(R.drawable.popup_shadow_d);
        			d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));//(R.drawable.popup_shadow_d);
        	    }
        	    	
		        d.setOnDismissListener(Drawer.this);
		        d.getListView().setAdapter(new ArrayAdapter<String>(a,
			            R.layout.singlechoice, android.R.id.text1, items) {
				    @Override
				    public View getView(int position,  View convertView,
				             ViewGroup parent) {
				    	View ret =  super.getView(position, convertView, parent);
				    	CheckedTextViewmy tv;
				        if(ret.getTag()==null)
				        	ret.setTag(tv = ret.findViewById(android.R.id.text1));
				        else
				        	tv = (CheckedTextViewmy)ret.getTag();
				        
				    	String id = items[position];
				    	mdict mdTmp = null;
				    	if(pos[position]==-2) {//无数据
				    		tv.setText("N.A.");
				        	tv.setSubText(null);
				    		return ret;
				    	}
				    	
				    	if(a.AppBlack==Color.WHITE)
				    		tv.setTextColor(Color.WHITE);
				    	
				    	
				    	if(mdictInternal.containsKey(id))
				    		mdTmp = mdictInternal.get(id);
						else {
							try {
								mdictInternal.put(id, mdTmp = new mdict(id,a));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

				        if(mdTmp!=null) {
				        	tv.setSubText(mdTmp._Dictionary_fName);
				        	tv.setText(mdTmp.getEntryAt(pos[position]));
				        }else {//获取词典失败
				        	tv.setSubText(new File(id).getName());
				        	tv.setText("failed to fetch: "+id);
				        }
				        return ret;
				    }
				});
			break;
			case 5://书签
				//String lastBookMark = a.opt.getString("bkmk");
				BKHistroryVagranter = a.opt.getInt("bkHVgrt",0);// must 0<..<20
				String lastBookMark = a.opt.getString("bkh"+BKHistroryVagranter);
				//CMN.show(lastBookMark);
				if(lastBookMark!=null) {
					String[] l = lastBookMark.split("\\/\\?Pos=");
					int pos1 = Integer.valueOf(l[1]);
					lastBookMark = l[0];
					String fn = new File(lastBookMark).getName();
					if(fn.lastIndexOf(".")!=-1)
						fn = fn.substring(0,fn.lastIndexOf("."));
					int c=0;
					boolean suc=false;
					int oldPos = a.adapter_idx;
					for(mdict mdTmp:a.md) {
						if(mdTmp._Dictionary_fName.equals(fn)) {
							a.switchToDictIdx(c);
							a.adaptermy.onItemClick(pos1);
							a.lv.setSelection(pos1);
							suc=true;
							break;
						}
						c++;
					}
					if(!suc)
					try {
						a.md.add(new mdict(lastBookMark,a));
						a.switchToDictIdx(a.md.size()-1);
						a.adaptermy.onItemClick(pos1);
						a.lv.setSelection(pos1);
						suc=true;
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(suc) {
						//a.mDrawerLayout.closeDrawer(mDrawerListView);
						if(a.pickDictDialog!=null) a.pickDictDialog.isDirty=true;
					}
				}else
					a.show(R.string.nothingR);
			break;
			case 7://追加词典
				DialogProperties properties = new DialogProperties();
				properties.selection_mode = DialogConfigs.SINGLE_MULTI_MODE;
				properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File("/");
                properties.error_dir = new File(Environment.getExternalStorageDirectory().getPath());
                properties.offset = new File(filepickernow!=null?filepickernow:a.opt.lastMdlibPath);
                properties.opt_dir=new File(a.opt.pathTo()+"favorite_dirs/");
                properties.opt_dir.mkdirs();
                properties.extensions = new HashSet<>();
                properties.extensions.add(".mdx");
                properties.title_id = R.string.addd;
                properties.isDark = a.AppWhite==Color.BLACK;
				FilePickerDialog dialog = new FilePickerDialog(a, properties);
				dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void
                    onSelectedFilePaths(String[] files,String now) { //files is the array of the paths of files selected by the Application User.
                    	filepickernow = now;
                    	if(files.length>0) {
                    		//File def = new File(a.opt.pathToMain()+"default.txt");      //!!!原配
                    		final File def = new File(a.getExternalFilesDir(null),"default.txt");      //!!!原配
                            File rec = new File(a.opt.pathToMain()+"CONFIG/mdlibs.txt");
                            try {
                            	BufferedWriter output = new BufferedWriter(new FileWriter(rec,true));
                            	BufferedWriter output2 = new BufferedWriter(new FileWriter(def,true));
	            				HashSet<String> mdictInternal = new HashSet<>();
	            				HashSet<String> renameRec = new HashSet<>();
	            				HashMap<String,String> renameList = new HashMap<>();
	            				for(mdict mdTmp:a.md) {
	            					mdictInternal.add(mdTmp.getPath());
	            				}
	            				int count=0;
	            				for(String fnI:files) {
	            					File fI = new File(fnI);
	            					if(fI.isDirectory())
	            						continue;
	            					if(a.checker.containsKey(fI.getName())) {//大小写敏感
	            						String removedAPath = a.checker.remove(fI.getName());
	            						renameList.put(removedAPath, fnI);
	            						renameRec.add(fnI);
	            					}
	            				}
	            				for(String fnI:files) {
        							//CMN.show(a.checker.containsKey(new File(fnI).getName())+"");
	            					File fI = new File(fnI);
	            					if(fI.isDirectory())
	            						continue;
	            					if(!mdictInternal.contains(new File(fnI).getAbsolutePath())) {//当前配置的disk cache 和全部词典的记录。
	            						try {
	            							String raw=fnI;
	            							fnI = mFile.tryDeScion(new File(fnI), a.opt.lastMdlibPath);
											a.md.add(new mdict(raw, a));
	            							output2.write(fnI);
	            							output2.write("\n");
	            							output2.flush();
											if(!a.mdlibsCon.contains(fnI) && !renameRec.contains(raw)) {
												a.mdlibsCon.add(fnI);
												output.write(fnI);
												output.write("\n");
												output.flush();
											}
											count++;
										} catch (Exception e) {
											e.printStackTrace();
											a.showT("词典 "+new File(fnI).getName()+" 加载失败 @"+fnI+" Load Error！ "+e.getLocalizedMessage());
										}
	            					}
	            				}
	            				a.showT("新加入"+count+"本词典！");
								if(a.pickDictDialog!=null) {
									a.pickDictDialog.adapter().notifyDataSetChanged();
									a.pickDictDialog.isDirty=true;
								}
	            				output.close();
	            				output2.close();
	            				renameRec.clear();
	            				renameRec=null;
						        ArrayList<File> moduleFullScannerArr;
					        	File[] moduleFullScanner = new File(a.opt.pathToMain()+"CONFIG").listFiles(new FileFilter() {
									@Override
									public boolean accept(File pathname) {
										String name = pathname.getName();
										if(name.endsWith(".set")) {
											return true;
										}	
										return false;
									}});
					        	moduleFullScannerArr = new ArrayList<File>(Arrays.asList(moduleFullScanner));
						        moduleFullScannerArr.add(rec);
						        moduleFullScannerArr.add(def);
					            StringBuffer sb= new StringBuffer(); 
						        for(File fI:moduleFullScannerArr) {
						        	InputStreamReader reader = null;
						        	sb.setLength(0);
						            String line = "";
						        	
						            try {
						                reader = new InputStreamReader(new FileInputStream(fI));
						                BufferedReader br = new BufferedReader(reader); 
						                while((line = br.readLine()) != null) {
					                    	String key=line.startsWith("/")?line:a.opt.lastMdlibPath+"/"+line;
					                    	String finder = renameList.get(new File(key).getAbsolutePath());
					                    	if(finder!=null){//当重命名之
					                    		line=mFile.tryDeScion(new File(finder), a.opt.lastMdlibPath);
						                        System.out.println("当重命名之"+line);
						                    }
						                    sb.append(line).append("\n");
						                } 
						                br.close();
						                reader.close();

						                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fI));
						                BufferedWriter bw = new BufferedWriter(writer);
						                bw.write(sb.toString());
						                bw.flush();
						                bw.close();
						                writer.close();
						            } catch (IOException e) {
						                e.printStackTrace();
						            }
								}
						        renameList.clear();
						        renameList=null;
						        
                    		} catch (IOException e1) {
                    			e1.printStackTrace();
                    		}
                        }
                    }

					@Override
					public void onEnterSlideShow(Window win, int delay) {

					}

					@Override
					public void onExitSlideShow() {

					}

					@Override
					public Activity getDialogActivity() {
						return null;
					}
				});
                dialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

                a.d = dialog;
                //.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

            break;
			case 8://主目录
				DialogProperties properties1 = new DialogProperties();
                properties1.selection_mode = DialogConfigs.SINGLE_MODE;
                properties1.selection_type = DialogConfigs.DIR_SELECT;
                properties1.root = new File("/");
                properties1.error_dir = Environment.getExternalStorageDirectory();
                properties1.offset = new File(
                		a.opt.lastMdlibPath
                		);
                //CMN.show(a.opt.lastMdlibPath+":"+Environment.getExternalStorageDirectory().getAbsolutePath());
                properties1.opt_dir=new File(a.opt.pathTo().append("favorite_dirs/").toString());
                properties1.opt_dir.mkdirs();
                properties1.title_id=R.string.pmfolder;
                //properties1.extensions = new String[] {"mdx"};
				FilePickerDialog dialog1 = new FilePickerDialog(a, properties1);
                dialog1.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void
                    onSelectedFilePaths(String[] files, String n) { //files is the array of the paths of files selected by the Application User.
                    	if(files.length>0) {
                    		a.opt.setLastMdlibPath(new File(files[0]).getAbsolutePath());
                    		a.show(R.string.relaunch);
                    		a.mDrawerLayout.closeDrawer(Gravity.LEFT);
                        }
                    }

					@Override
					public void onEnterSlideShow(Window win, int delay) {

					}

					@Override
					public void onExitSlideShow() {

					}

					@Override
					public Activity getDialogActivity() {
						return null;
					}
				});
                dialog1.show();
			break;
			case 9://词典管理中心
				a.findViewById(R.id.browser_widget2).performLongClick();
			break;
			case 10://切换生词本
				a.findViewById(R.id.browser_widget5).performLongClick();
			break;
			case 12://设置
	            Intent intent = new Intent();
				((AgentApplication)a.getApplication()).opt=a.opt;
	            intent.setClass(a, SettingsActivity.class);
	            a.startActivityForResult(intent,111);
			break;
		}
	}
	
	 /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     * 
     * @param fragmentId
     *            The android:id of this fragment in its activity's layout.
     * @param drawerLayout
     *            The DrawerLayout containing this fragment's UI.
     */
    //Users of this fragment must call this method to set up the navigation

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    	
    	mFragmentContainerView = a.findViewById(fragmentId);
    	//mDrawerLayout = drawerLayout;

    	// set a custom shadow that overlays the main content when the drawer
    	// opens
    	//mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);
    	// set up the drawer's list view with items and click listener

    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		d = null;
	}
	

    public boolean hasInstalledAlipayClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo("com.eg.android.AlipayGphone", 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
			case R.id.sw1:
				if(isChecked) {
					a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}else {
					a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				a.opt.setFullScreen(isChecked);
				isDirty=true;
			break;
			case R.id.sw2:
				a.opt.setContentBow(!isChecked);
				a.setContentBow(!isChecked);
				isDirty=true;
			break;
			case R.id.sw3:
				a.opt.setViewPagerEnabled(!isChecked);
				a.viewPager.setNoScroll(isChecked);
				isDirty=true;
			break;
			case R.id.sw4:
				a.opt.setInDarkMode(isChecked);
				a.AppBlack=isChecked?Color.WHITE:Color.BLACK;
				a.AppWhite=isChecked?Color.BLACK:Color.WHITE;
				mDrawerListView.setBackgroundColor(isChecked?Color.BLACK:0xffe2e2e2);
				HeaderView.setBackgroundColor(a.AppWhite);
				FooterView.setBackgroundColor(a.AppWhite);
				a.adaptermy.notifyDataSetChanged();
				a.adaptermy2.notifyDataSetChanged();
				a.adaptermy3.notifyDataSetChanged();
				a.adaptermy4.notifyDataSetChanged();
				if(a.isFragInitiated)a.pickDictDialog.adapter().notifyDataSetChanged();
				myAdapter.notifyDataSetChanged();
				//a.refreshUIColors();
				a.animateUIColorChanges();
				isDirty=true;
			break;
			case R.id.sw5:
				a.opt.set_use_volumeBtn(isChecked);
				isDirty=true;
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Intent i = new Intent(getActivity(), CuteFileManager.class);
		startActivity(i);
		return false;
	}


}
