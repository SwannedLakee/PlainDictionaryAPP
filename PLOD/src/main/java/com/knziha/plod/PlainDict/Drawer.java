package com.knziha.plod.plaindict;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.GlobalOptions;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.knziha.filepicker.model.DialogConfigs;
import com.knziha.filepicker.model.DialogProperties;
import com.knziha.filepicker.model.DialogSelectionListener;
import com.knziha.filepicker.view.FilePickerDialog;
import com.knziha.plod.dictionary.Utils.SU;
import com.knziha.plod.dictionary.mdict;
import com.knziha.plod.dictionarymanager.files.ReusableBufferedReader;
import com.knziha.plod.dictionarymanager.files.ReusableBufferedWriter;
import com.knziha.plod.dictionarymanager.files.mFile;
import com.knziha.plod.dictionarymodels.BookPresenter;
import com.knziha.plod.settings.ServerPreference;
import com.knziha.plod.widgets.AdvancedNestScrollListview;
import com.knziha.plod.widgets.CheckedTextViewmy;
import com.knziha.plod.widgets.FlowTextView;
import com.knziha.plod.widgets.SwitchCompatBeautiful;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.appcompat.app.GlobalOptions.realWidth;
import static com.knziha.plod.plaindict.PDICMainAppOptions.PLAIN_TARGET_FLOAT_SEARCH;


/** @author KnIfER */
public class Drawer extends Fragment implements
		OnClickListener, OnDismissListener, OnCheckedChangeListener, OnLongClickListener {
	private PDICMainActivity a;
	private boolean bIsFirstLayout=true;
	AlertDialog d;

	String[] hints;
	private ListView mDrawerList;
	View mDrawerListLayout;
	MyAdapter myAdapter;

	public EditText etAdditional;

	SwitchCompat sw1,sw2,sw3,sw4,sw5;

	View HeaderView;
	View HeaderView2;
	View pasteBin;

	ViewGroup FooterView;
	private File filepickernow;
	public  ArrayList<String> mClipboard;
	FileOutputStream PasteFileTarget;
	ClipboardManager.OnPrimaryClipChangedListener ClipListener;
	private ListView ClipboardList;
	private CharSequence mPreviousCBContent;
	private ViewGroup swRow;
	private boolean toPDF;
	HashMap<String, BookPresenter> mdictInternal = new HashMap<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//if(true) return new View(inflater.getContext());
		if(mDrawerListLayout ==null){
			mDrawerListLayout = inflater.inflate(R.layout.activity_main_navi_drawer, container,false);
			FooterView = mDrawerListLayout.findViewById(R.id.footer);
			FooterView.findViewById(R.id.menu_item_setting).setOnClickListener(this);
			mDrawerListLayout.findViewById(R.id.menu_item_exit).setOnClickListener(this);
			mDrawerListLayout.findViewById(R.id.menu_item_exit).setOnLongClickListener(this);
			mDrawerList = mDrawerListLayout.findViewById(R.id.left_drawer);
			((AdvancedNestScrollListview)mDrawerList).setNestedScrollingEnabled(true);
			
			myAdapter = new MyAdapter(new int[]{
				R.string.fuzzyret1
				, R.string.fullret
				, 0
				, R.string.bookmarkH
				, R.string.lastmarks
				, 0
				, R.string.settings
				, 0
				, R.string.addd
				, R.string.pick_main
				, R.string.manager
				, R.string.switch_favor
			});
			
			mDrawerList.setAdapter(myAdapter);
			
			HeaderView = inflater.inflate(R.layout.activity_main_navi_drawer_header, null);
			
			mDrawerList.addHeaderView(HeaderView);

			mDrawerListLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {
				int oldWidth;
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
										   int oldRight, int oldBottom) {
					right=right-left;
					//todo opt
					if(GlobalOptions.isLarge) {
						right = Math.min(right, Math.max(realWidth, (int)getResources().getDimension(R.dimen.idealdpdp)));
						v.getLayoutParams().width = right;
					}
					
					if(swRow!=null && (right!=oldWidth || bIsFirstLayout)) {
						//if(bIsFirstLayout) SwitchCompatBeautiful.bForbidRquestLayout = true;
						int width = (right - sw1.getWidth() * 5) / 6;
						View vI;
						for (int i = 0; i < swRow.getChildCount(); i++) {
							vI=swRow.getChildAt(i);
							MarginLayoutParams lp = (MarginLayoutParams) vI.getLayoutParams();
							lp.leftMargin = width;
							vI.setLayoutParams(lp);
						}
						oldWidth = right;
						if(bIsFirstLayout) {
							SwitchCompatBeautiful.bForbidRquestLayout = false;
							bIsFirstLayout = false;
						}
					}
				}
			});
		}
		return mDrawerListLayout;
	}

	public void setCheckedForce(SwitchCompat sw5, boolean b) {
		if(sw5.isChecked()==b){
			onCheckedChanged(sw5, b);
		}else{
			sw5.toggle();
		}
	}

	class MyAdapter extends BaseAdapter {
		int[] items;
		public MyAdapter(int[] items) {
			this.items = items;
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
			return items.length;
		}
		@Override
		public boolean isEnabled(int position) {
			return items[position]!=0;
		}
		
		@Override
		public int getViewTypeCount() {
			return 3;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (items[position]==0) {
				return 1;
			}
			if (items[position]==R.string.settings) {
				return 2;
			}
			return 0;
		}
		
		@Override
		public String getItem(int position) {
			return a.mResource.getString(items[position]);
		}
		
		@Override
		public long getItemId(int position) {
			return items[position];
		}
		
		@NonNull
		@Override
		public View getView(int position, View convertView, @NonNull ViewGroup parent) {
			int id=items[position];
			if(id==0){
				/* divider border */
				return convertView!=null?convertView:LayoutInflater.from(getContext()).inflate(R.layout.listview_sep, parent, false);
			}
			PDICMainActivity.ViewHolder vh;
			if(convertView!=null){
				vh=(PDICMainActivity.ViewHolder)convertView.getTag();
			} else {
				vh=new PDICMainActivity.ViewHolder(getContext(),id==R.string.settings?R.layout.drawer_settings:R.layout.drawer_item0, parent);
				vh.itemView.setBackgroundResource(R.drawable.listviewselector1);
				vh.itemView.setOnClickListener(Drawer.this);
				if (vh.subtitle!=null) {
					vh.subtitle.trim=false;
					vh.subtitle.setTextColor(ContextCompat.getColor(a, R.color.colorHeaderBlue));
				}
			}
			vh.position=position;
			vh.itemView.setOnLongClickListener((id==R.string.addd||id==R.string.pick_main)?Drawer.this:null);
			if( vh.title.getTextColors().getDefaultColor()!=a.AppBlack) {
				PDICMainActivity.decorateBackground(vh.itemView);
				vh.title.setTextColor(a.AppBlack);
			}

			vh.title.setText(id);
			vh.itemView.setId(id);
			if (vh.subtitle!=null) {
				String hint = null;
				if(show_hints) {
					if(hints==null) hints = getResources().getStringArray(R.array.drawer_hints);
					hint=hints[position];
				}
				vh.subtitle.setText(hint);
			}
//			vh.subtitle.getLayoutParams().height=hint==null? (int) (GlobalOptions.density * 8) :-2;
//			vh.subtitle.requestLayout();
			//vh.subtitle.setVisibility(hint==null?View.GONE:View.VISIBLE);

			return vh.itemView;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//CMN.Log("Drawer onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		a = ((PDICMainActivity)getActivity());
		a.drawerFragment = this;
		
		if(HeaderView==null) return;
		
		myAdapter.show_hints = true;
		
		if(PDICMainAppOptions.getShowPasteBin())
			SetupPasteBin();

		sw1 = HeaderView.findViewById(R.id.sw1);
		swRow = (ViewGroup) sw1.getParent();
		sw1.setOnCheckedChangeListener(this);
		sw1.setChecked(PDICMainAppOptions.isFullScreen());
		sw1.setOnClickListener(v -> {
			// TODO Auto-generated method stub

		});

		boolean val = PDICMainAppOptions.getEnableSuperImmersiveScrollMode();
		sw2 = HeaderView.findViewById(R.id.sw2);
		sw2.setChecked(val);
		sw2.setOnCheckedChangeListener(this);

		sw3 = HeaderView.findViewById(R.id.sw3);
		sw3.setOnCheckedChangeListener(this);
		//sw3.setChecked(!a.opt.isViewPagerEnabled());
		sw3.setChecked(a.opt.getServerStarted());

		sw4 = HeaderView.findViewById(R.id.sw4);
		val = a.opt.getInDarkMode();
		sw4.setChecked(!val);
		sw4.setTag(false);
		sw4.setOnCheckedChangeListener(this);
		sw4.setChecked(val);

		sw5 = HeaderView.findViewById(R.id.sw5);
		sw5.setChecked(a.opt.getUseVolumeBtn());
		sw5.setOnCheckedChangeListener(this);

		if(GlobalOptions.isDark) {
			mDrawerListLayout.setBackgroundColor(Color.BLACK);
			HeaderView.setBackgroundColor(a.AppWhite);
			FooterView.setBackgroundColor(a.AppWhite);
		}
		//test groups
		//PDICMainActivity.ViewHolder vh = new PDICMainActivity.ViewHolder(a, R.layout.listview_item0, null);
		//View v = new View(a);v.setTag(vh);onClick(v);
	}

	void SetupPasteBin() {
		ClipboardManager clipboardManager = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboardManager!=null){
			if(pasteBin==null){
				pasteBin = mDrawerListLayout.findViewById(R.id.pastebin);
				pasteBin.setOnClickListener(this);
				mClipboard=new ArrayList<>(12);
				//mClipboard.add("Happy");
			}
			if(PDICMainAppOptions.getShowPasteBin()){
				pasteBin.setVisibility(View.VISIBLE);
				if(ClipListener==null){
					ClipListener = () -> {
						if (a.opt.getPasteBinEnabled())
							try {
								ClipData pclip = clipboardManager.getPrimaryClip();
								ClipData.Item firstItem = pclip.getItemAt(0);
								CharSequence content = firstItem.getText();
								//CMN.Log("剪贴板监听器:", content);
								//a.showT(  GlobalOptions.chromium+"剪贴板监听器:" + content + System.identityHashCode(pclip));
								
								long timeDelta = System.currentTimeMillis() - a.lastClickTime;
								if ((GlobalOptions.chromium || timeDelta < 256) && content.equals(mPreviousCBContent)){
									return;
								}
								String text = content.toString();
								if(false) {
									try {
										if(PasteFileTarget==null) {
											PasteFileTarget = new FileOutputStream(new File("/sdcard/myFolder/PlainDictPasted.txt"), true);
											PasteFileTarget.write("\n\n".getBytes());
										}
										Pattern p = Pattern.compile("(ht|f)tp(s?)://[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(/?)([a-zA-Z0-9\\-.?,'/\\\\+&amp;%$#_]*)?");
										Matcher m = p.matcher(text);
										if(m.find()) {
											text = m.group(0);
										}
										text = text.trim();
										text = text.replace("\r", "");
										text = text.replace("\n", "");
										PasteFileTarget.write(text.getBytes());
										PasteFileTarget.write("\n".getBytes());
										PasteFileTarget.flush();
										a.showT("已记录！");
									} catch (Exception ignored) { }
									return;
								}
								int i = 0;
								for (; i < mClipboard.size(); i++) {
									if (mClipboard.get(i).equals(text))
										break;
								}
								if (i == mClipboard.size()) {
									mClipboard.add(0, text);
								} else {
									mClipboard.add(0, mClipboard.remove(i));
								}
								boolean focused = a.hasWindowFocus();
								boolean toFloat = a.opt.getPasteTarget() == PLAIN_TARGET_FLOAT_SEARCH;
								if (!toFloat && !focused && a.opt.getPasteBinBringTaskToFront()) {
									ActivityManager manager = (ActivityManager) a.getSystemService(Context.ACTIVITY_SERVICE);
									if (manager != null)
										manager.moveTaskToFront(a.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
								}
								if (toFloat || (focused || a.opt.getPasteBinBringTaskToFront()) && a.opt.getPasteBinUpdateDirect())
									a.JumpToWord(text, focused ? 1 : 2);
								else
									a.textToSetOnFocus = text;
								mPreviousCBContent = content;
								a.lastClickTime = System.currentTimeMillis();
							}
							catch (Exception e) {
								CMN.Log("ClipListener:" + e);
							}
					};
				}
				//CMN.Log("clipboardManager.addPrimaryClipChangedListener");
				clipboardManager.removePrimaryClipChangedListener(ClipListener);
				clipboardManager.addPrimaryClipChangedListener(ClipListener);
			}
			else {
				pasteBin.setVisibility(View.GONE);
				if(ClipListener!=null)
					clipboardManager.removePrimaryClipChangedListener(ClipListener);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(!a.systemIntialized) return;
		int id = v.getId();
		int BKHistroryVagranter;
		switch(id) {
			case R.id.server:
				a.launchSettings(ServerPreference.id, 0);
			return;
			case R.id.menu_item_setting:{
				//a.mDrawerLayout.closeDrawer(GravityCompat.START);
				final View dv = a.getLayoutInflater().inflate(R.layout.dialog_about,null);
				
				String infoStr = getString(R.string.infoStr, BuildConfig.VERSION_NAME+(BuildConfig.isDebug?"工程调试版":""));
				final SpannableStringBuilder ssb = new SpannableStringBuilder(infoStr);
				
				if (mdict.error_input!=null) {
					ssb.append("\n出错信息：").append(mdict.error_input);
				}
				
				final String languageName = Locale.getDefault().getLanguage();
				
				final TextView tv = dv.findViewById(R.id.resultN);
				tv.setPadding(0, 0, 0, 50);
				
				try {
					int startss = ssb.toString().indexOf("[");
					int endss = ssb.toString().indexOf("]",startss);
					
					ssb.setSpan(new ClickableSpan() {
						@Override
						public void onClick(@NonNull View widget) {
							a.launchSettings(6, 1297);
						}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					
					startss = ssb.toString().indexOf("[",endss);
					endss = ssb.toString().indexOf("]",startss);
					
					if(false)
						ssb.setSpan(new ClickableSpan() {
							@Override
							public void onClick(@NonNull View widget) {
								Uri uri = Uri.parse("https://tieba.baidu.com/f?kw=%E5%B9%B3%E5%85%B8app");
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					
					startss = ssb.toString().indexOf("[",endss);
					endss = ssb.toString().indexOf("]",startss);
					if(endss>startss && startss>0)
						
						if(false)
							ssb.setSpan(new ClickableSpan() {
								@Override
								public void onClick(@NonNull View widget) {
									Uri uri = Uri.parse("https://tieba.baidu.com/f?kw=%E5%B9%B3%E5%85%B8app");
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(intent);
								}},startss,endss+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				tv.setText(ssb);
				tv.setMovementMethod(LinkMovementMethod.getInstance());
				AlertDialog.Builder builder2 = new AlertDialog.Builder(a);
				builder2.setView(dv);
				final AlertDialog d = builder2.create();
				d.setCanceledOnTouchOutside(true);
				//d.setCanceledOnTouchOutside(false);
				d.setOnDismissListener(dialog -> {
				});
				dv.findViewById(R.id.cancel).setOnClickListener(v1 -> d.dismiss());
				d.show();
				//android.view.WindowManager.LayoutParams lp = d.getWindow().getAttributes();  //获取对话框当前的参数值
				//lp.height = -2;
				//d.getWindow().setAttributes(lp);
				
			} break;
			//退出
			case R.id.menu_item_exit:
				a.showAppExit(false);
				break;
			case R.id.pastebin:{//剪贴板对话框
				if(ClipboardList ==null){
					ClipboardList = new ListView(a.getBaseContext());
					View pastebin_header = getLayoutInflater().inflate(R.layout.pastebin_header,null);
					decorateHeader(pastebin_header);
					ArrayAdapter<String> adapter = new ArrayAdapter<>(a.getBaseContext(), R.layout.simple_column_litem, android.R.id.text1, mClipboard);
					ClipboardList.addHeaderView(pastebin_header);
					ClipboardList.setAdapter(adapter);
					ClipboardList.setPadding(0,0,0, (int) (5*a.dm.density));
					ClipboardList.setOnItemClickListener((p, view, position, id1) -> {//操作剪贴板
						int hc = ClipboardList.getHeaderViewsCount();
						if(position>= hc && position<hc+mClipboard.size()){
							a.etSearch.setText(mClipboard.get(position-hc));
							a.d.dismiss();
						}
					});
				}
				if(ClipboardList.getParent()!=null)
					((ViewGroup) ClipboardList.getParent()).removeView(ClipboardList);
				androidx.appcompat.app.AlertDialog.Builder dialog_builder = new  androidx.appcompat.app.AlertDialog.Builder(a);
				dialog_builder.setView(ClipboardList);
				androidx.appcompat.app.AlertDialog dTmp = dialog_builder.create();
				Window win = dTmp.getWindow();
				if (win != null) {
					a.fix_full_screen(win.getDecorView());
					win.setDimAmount(0);
				}
				dTmp.show();
				dTmp.setOnDismissListener(dialog -> a.checkFlags());
				a.d=dTmp;
				ClipboardList.addOnLayoutChangeListener(MainActivityUIBase.mListsizeConfiner.setMaxHeight((int) (a.root.getHeight()-a.root.getPaddingTop()-2.8*getResources().getDimension(R.dimen._50_))));
			} break;
			//模糊搜索 全文搜索
			case R.string.fuzzyret1:
			case R.string.fullret:  {
				a.switchToSearchModeDelta((id==R.string.fuzzyret1)?100:-100);
				a.UIData.drawerLayout.closeDrawer(GravityCompat.START);
				a.etSearch.requestFocus();
				((InputMethodManager)a.getSystemService( Context.INPUT_METHOD_SERVICE )).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			} break;
			//书签历史
			case R.string.bookmarkH:  {
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
				
				for(BookPresenter mdTmp:a.md) {
					if(mdTmp!=null)
						mdictInternal.put(mdTmp.getDictionaryName(), mdTmp);
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(a);
				builder.setTitle(R.string.bookmarkH);
				ArrayList<PlaceHolder> placeHolders = a.getLazyCC();
				builder.setSingleChoiceItems(new String[] {}, 0,
						(dialog, position1) -> d.getListView().postDelayed(new Runnable() {
							@Override
							public void run() {
								String id1 = items[position1];
								BookPresenter mdTmp = mdictInternal.get(id1);
								if(mdTmp!=null) {
									String name = mdTmp.getDictionaryName();
									int i = 0;
									for (; i < placeHolders.size(); i++) {
										if(placeHolders.get(i).getName().equals(name))
											break;
									}
									if(i==placeHolders.size()) {
										a.md.add(mdTmp);
										placeHolders.add(new PlaceHolder(mdTmp.getPath()));
										a.switch_To_Dict_Idx(a.md.size()-1, true, false, null);
									}else {
										if(a.md.get(i)==null){
											a.md.set(i, mdTmp);
										}
										a.switch_To_Dict_Idx(i, true, false, null);
									}
									if(a.pickDictDialog!=null) a.pickDictDialog.isDirty=true;
									int toPos = pos[position1];
									a.bWantsSelection=false;
									a.bNeedReAddCon=false;
									//a.bOnePageNav=mdTmp instanceof bookPresenter_pdf; nimp
									a.adaptermy.onItemClick(toPos);
									a.bOnePageNav=false;
									a.lv.setSelection(toPos);
									d.hide();
								}}
							
						}, 0));
				d=builder.create();
				d.show();
				
				d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				d.setOnDismissListener(Drawer.this::onDismiss);
				d.getListView().setAdapter(new ArrayAdapter<String>(a,
						R.layout.singlechoice, android.R.id.text1, items) {
					@NonNull
					@Override
					public View getView(int position, View convertView,
										@NonNull ViewGroup parent) {
						View ret =  super.getView(position, convertView, parent);
						CheckedTextViewmy tv;
						if(ret.getTag()==null)
							ret.setTag(tv = ret.findViewById(android.R.id.text1));
						else
							tv = (CheckedTextViewmy)ret.getTag();
						
						String id = items[position];
						
						if(GlobalOptions.isDark)
							tv.setTextColor(Color.WHITE);
						
						if(pos[position]==-2) {//无数据
							tv.setText("N.A.");
							tv.setSubText(null);
							return ret;
						}
						
						BookPresenter mdTmp  = mdictInternal.get(id);
						
						if(mdTmp==null) {
							try {
								String path=id;
								mdTmp = MainActivityUIBase.new_book(path, a);
								mdictInternal.put(id, mdTmp);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						if(mdTmp!=null) {
							tv.setSubText(mdTmp.getDictionaryName());
							tv.setText(mdTmp.getLexicalEntryAt(pos[position]));
						}else {//获取词典失败
							tv.setSubText(id);
							tv.setText("failed to fetch: "+id);
						}
						return ret;
					}
				});
			} break;
			//书签
			case R.string.lastmarks:  {
				//String lastBookMark = a.opt.getString("bkmk");
				BKHistroryVagranter = a.opt.getInt("bkHVgrt",0);// must 0<..<20
				String lastBookMark = a.opt.getString("bkh"+BKHistroryVagranter);
				//CMN.show(lastBookMark);
				if(lastBookMark!=null) {
					String[] l = lastBookMark.split("/\\?Pos=");
					int pos1 = Integer.valueOf(l[1]);
					lastBookMark = l[0];
					String fn = new File(lastBookMark).getName();
					if(fn.lastIndexOf(".")!=-1)
						fn = fn.substring(0,fn.lastIndexOf("."));
					int c=0;
					boolean suc=false;
					int oldPos = a.adapter_idx;
					for(PlaceHolder phI:a.getLazyCC()) {
						if(phI.getName().equals(fn)) {
							a.switch_To_Dict_Idx(c, true, false, null);
							a.adaptermy.onItemClick(pos1);
							a.lv.setSelection(pos1);
							suc=true;
							break;
						}
						c++;
					}
					if(!suc)
						try {
							a.md.add(MainActivityUIBase.new_book(lastBookMark, a));
							a.switch_To_Dict_Idx(a.md.size()-1, true, false, null);
							a.adaptermy.onItemClick(pos1);
							a.lv.setSelection(pos1);
							suc=true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					if(suc) {
						//a.mDrawerLayout.closeDrawer(mDrawerListView);
						if(a.pickDictDialog!=null) a.pickDictDialog.isDirty=true;
					}
				}else
					a.show(R.string.nothingR);
			} break;
			//追加词典 添加词典 打开
			case R.string.addd:  {
				if(false) { // MLSN
					a.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
							.setType("*/*"), Constants.OpenBookRequset);
				}
				else {
					boolean AutoFixLostRecords = true;
					DialogProperties properties = new DialogProperties();
					properties.selection_mode = DialogConfigs.SINGLE_MULTI_MODE;
					properties.selection_type = DialogConfigs.FILE_SELECT;
					properties.root = new File("/");
					properties.error_dir = new File(Environment.getExternalStorageDirectory().getPath());
					properties.offset = filepickernow!=null?filepickernow:a.opt.lastMdlibPath;
					properties.opt_dir=new File(a.opt.pathToDatabases()+"favorite_dirs/");
					properties.opt_dir.mkdirs();
					properties.extensions = new HashSet<>();
					properties.extensions.add(".mdx");
					properties.extensions.add(".web"); // 在线词典，JSON格式。
					properties.extensions.add(".dsl");
					properties.extensions.add(".dz");
					if(toPDF) {
						properties.extensions.add(".pdf");
						properties.extensions.add(".mdd");
						properties.extensions.add(".txt");
					}
					properties.title_id = R.string.addd;
					properties.isDark = a.AppWhite==Color.BLACK;
					FilePickerDialog dialog = new FilePickerDialog(a, properties);
					dialog.setDialogSelectionListener(new DialogSelectionListener() {
						@Override
						public void
						onSelectedFilePaths(String[] files, File now) {
							//CMN.Log(files);
							filepickernow = now;
							if(files.length>0) {
								final File def = a.opt.getCacheCurrentGroup()?new File(a.getExternalFilesDir(null),"default.txt")
										:a.getStartupFile(a.opt.fileToConfig());      //!!!原配
								File ConfigFile = a.opt.fileToConfig();
								File rec = a.opt.fileToDecords(ConfigFile);
								a.ReadInMdlibs(rec);
								HashMap<String, String> add_book_checker = a.lostFiles;
								
								HashSet<String> mdictInternal = new HashSet<>();
								HashSet<String> renameRec = new HashSet<>();
								HashMap<String,String> renameList = new HashMap<>();
								for(PlaceHolder phI:a.CosyChair) {
									mdictInternal.add(phI.getPath(a.opt).getPath());
								}
								for(BookPresenter mdTmp:a.currentFilter) {
									if(mdTmp!=null)
										mdictInternal.add(mdTmp.getPath());
								}
								for(BookPresenter mdTmp:Drawer.this.mdictInternal.values()) {
									if(mdTmp!=null)
										mdictInternal.add(mdTmp.getPath());
								}
								int newAdapterIdx=-1;
								try {
									BufferedWriter output = new BufferedWriter(new FileWriter(rec,true));
									BufferedWriter output2 = null;
									int countAdd=0;
									int countRename=0;
									String removedAPath;
									boolean bNextPlaceHolder = false;
									for (int i = 0; i < files.length; i++) {
										String fnI = files[i];
										File fI = new File(fnI);
										CMN.Log("AddFiles", fnI, mdictInternal.contains(fI.getPath()));
										if(fI.isDirectory()) continue;
										//checker.put("sound_us.mdd", "/storage/emulated/0/PLOD/mdicts/发音库/sound_us.mdd");
										/* 检查文件名称是否乃记录之中已失效项，有则需重命名。*/
										if(!bNextPlaceHolder && AutoFixLostRecords && (removedAPath=add_book_checker.get(fI.getName()))!=null) {
											renameList.put(removedAPath, fnI);
											renameRec.add(fnI);
										}
										/* 追加不存于当前分组的全部词典至全部记录与缓冲组。 */
										else if(!mdictInternal.contains(fI.getPath())) {
											try {
												if (bNextPlaceHolder) {
													a.AddIndexingBookIdx(-1, a.md.size());
													a.md.add(null);
													bNextPlaceHolder=false;
												} else {
													a.md.add(MainActivityUIBase.new_book(fnI, a));
												}
												if(newAdapterIdx==-1) newAdapterIdx = a.md.size()-1;
												PlaceHolder phI = new PlaceHolder(fnI);
												a.CosyChair.add(phI);
												String raw=fnI;
												fnI = mFile.tryDeScion(fI, a.opt.lastMdlibPath);
												if(output2==null){
													boolean def_exists = def.exists();
													//tofo check
													output2 = new BufferedWriter(new FileWriter(def,true));
													if(!def_exists) {
														String path = CMN.AssetTag + "liba.mdx";
														output2.write(path);
														output2.write("\n");
													}
												}
												output2.write(fnI);
												output2.write("\n");
												output2.flush();
												/* 需追加至全部记录而无须重命名者 */
												if(a.mdlibsCon.add(fnI) && !renameRec.contains(raw)) {
													output.write(fnI);
													output.write("\n");
												}
												countAdd++;
											} catch (Exception e) {
												if(e instanceof IllegalStateException && "Needs Index Building!".equals(e.getMessage())) {
													bNextPlaceHolder=true;
													i--;
												} else {
													CMN.Log(e);
													a.showT("词典 "+new File(fnI).getName()+" 加载失败 @"+fnI+" Load Error！ "+e.getLocalizedMessage());
												}
											}
										}
									}
									CMN.Log(add_book_checker);
									CMN.Log(renameRec.toString());
									if(a.pickDictDialog!=null) {
										a.pickDictDialog.adapter().notifyDataSetChanged();
										a.pickDictDialog.isDirty=true;
									}
									output.close();
									if(output2!=null) {
										output2.close();
									}
									renameRec.clear();
									
									for (ArrayList<PlaceHolder> phII: PDICMainActivity.PlaceHolders) {
										for (PlaceHolder phI:phII){
											String newPath = renameList.get(phI.getPath(a.opt));
											if(newPath!=null){
												PlaceHolder phTmp = new PlaceHolder(newPath);
												phI.pathname = phTmp.pathname;
											}
										}
									}
									
									if(AutoFixLostRecords && renameList.size()>0){
										ArrayList<File> moduleFullScannerArr;
										File[] moduleFullScanner = ConfigFile.listFiles(pathname -> !SU.isNoneSetFileName(pathname.getPath()));
										moduleFullScannerArr = new ArrayList<>(Arrays.asList(moduleFullScanner));
										moduleFullScannerArr.add(rec);
										moduleFullScannerArr.add(def);
										StringBuilder sb = a.MainStringBuilder;
										AgentApplication app = ((AgentApplication) a.getApplication());
										char[] cb = app.get4kCharBuff();
										for(File fI:moduleFullScannerArr) {
											sb.setLength(0);
											String line;
											boolean bNeedRewrite=false;
											try {
												ReusableBufferedReader br = new ReusableBufferedReader(new FileReader(fI), cb, 4096);
												while((line = br.readLine()) != null) {
													String prefix = null;
													if(line.startsWith("[:")){
														int idx = line.indexOf("]",2);
														if(idx>=2) {
															idx+=1;
															prefix = line.substring(0, idx);
															line = line.substring(idx);
														}
													}
													/* from old path to neo path */
													String finder = renameList.get(line.startsWith("/")?line:a.opt.lastMdlibPath+"/"+line);
													CMN.Log(fI.getName(), "{重命名??}", finder, line);
													if(finder!=null){
														line=mFile.tryDeScion(new File(finder), a.opt.lastMdlibPath);
														if(prefix!=null){
															line=prefix+line;
														}
														bNeedRewrite = true;
														countRename++;
														CMN.Log(fI.getName(), "{当重命名之}", finder, line);
													}
													sb.append(line).append("\n");
												}
												br.close();
												cb=br.cb;
												if(bNeedRewrite) {
													ReusableBufferedWriter bw = new ReusableBufferedWriter(new FileWriter(fI), cb, 4096);
													bw.write(sb.toString());
													bw.flush(); bw.close();
													cb=br.cb;
												}
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
										app.set4kCharBuff(cb);
										renameList.clear();
										add_book_checker.clear();
									}
									if(countRename>0)
										a.showT("新加入"+countAdd+"本词典, 重定位"+countRename+"次！");
									else
										a.showT("新加入"+countAdd+"本词典！");
									
									if(newAdapterIdx!=-1) {
										a.switch_To_Dict_Idx(newAdapterIdx, true, true, null);
									}
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
						
						@Override
						public void onDismiss() {
						
						}
					});
					dialog.show();
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					
					a.d = dialog;
				}
				//.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			} break;
			//主目录
			case R.string.pick_main:  {
				DialogProperties properties1 = new DialogProperties();
				properties1.selection_mode = DialogConfigs.SINGLE_MODE;
				properties1.selection_type = DialogConfigs.DIR_SELECT;
				properties1.root = new File("/");
				properties1.error_dir = Environment.getExternalStorageDirectory();
				properties1.offset = a.opt.lastMdlibPath;
				//CMN.show(a.opt.lastMdlibPath+":"+Environment.getExternalStorageDirectory().getAbsolutePath());
				properties1.opt_dir=new File(a.opt.pathToDatabases().append("favorite_dirs/").toString());
				properties1.opt_dir.mkdirs();
				properties1.title_id=R.string.pick_main;
				//properties1.extensions = new String[] {"mdx"};
				FilePickerDialog dialog1 = new FilePickerDialog(a, properties1);
				dialog1.setDialogSelectionListener(new DialogSelectionListener() {
					@Override
					public void
					onSelectedFilePaths(String[] files, File n) {
						if(files.length>0) {
							a.pendingModPath(new File(files[0]).getAbsolutePath());
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
					
					@Override
					public void onDismiss() {
					
					}
				});
				dialog1.show();
			} break;
			//词典管理中心
			case R.string.manager:  {
				a.showDictionaryManager();
			} break;
			//切换生词本
			case R.string.switch_favor:  {
				a.showPickFavorFolder();
			} break;
			case R.string.settings:  {
				((AgentApplication)a.getApplication()).opt=a.opt;
				a.launchSettings(0, 1297);
			} break;
		}
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		d = null;
	}
	
	public static String getIP(Context context) throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
			{
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
				{
					return inetAddress.getHostAddress();
				}
			}
		}
		return null;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
			case R.id.sw1:{
				if(isChecked) {
					a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
					 {
						View decorView = a.getWindow().getDecorView();
						int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_FULLSCREEN;
						if(PDICMainAppOptions.isFullscreenHideNavigationbar()) uiOptions|=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LOW_PROFILE
								| View.SYSTEM_UI_FLAG_IMMERSIVE;
						decorView.setSystemUiVisibility(uiOptions);
					}
				}else {
					a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					View decorView = a.getWindow().getDecorView();
					int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
							View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
					decorView.setSystemUiVisibility(uiOptions);
				}
				a.opt.setFullScreen(isChecked);
			} break;
			case R.id.sw2:{
				a.setNestedScrollingEnabled(PDICMainAppOptions.setEnableSuperImmersiveScrollMode(isChecked));
			} break;
			case R.id.sw3:{
				a.opt.setServerStarted(isChecked);
				//a.viewPager.setNoScroll(isChecked);
				a.startServer(isChecked);
				if(isChecked) {
					if(HeaderView2==null) {
						HeaderView2 = a.getLayoutInflater().inflate(R.layout.activity_main_navi_server_header, null);
						HeaderView2.setOnClickListener(this);
						if(GlobalOptions.isDark) {
							HeaderView2.getBackground().setColorFilter(GlobalOptions.NEGATIVE);
							((TextView)HeaderView2.findViewById(R.id.text)).setTextColor(a.AppBlack);
						}
					}
					if(HeaderView2.getParent()==null) {
						mDrawerList.addHeaderView(HeaderView2);
					}
					FlowTextView ipView = (FlowTextView) HeaderView2.findViewById(R.id.subtext);
					ipView.trim=false;
					try {
						ipView.setText("http://"+getIP(getContext())+":"+a.server.getListeningPort());
					} catch (Exception ignored) { }
				} else if(HeaderView2!=null && HeaderView2.getParent()!=null) {
					mDrawerList.removeHeaderView(HeaderView2);
				}
			} break;
			/* 切换黑暗模式 */
			case R.id.sw4:{
				//if(Build.VERSION.SDK_INT<29){
					GlobalOptions.isDark = false;
//				}else{
//					GlobalOptions.isDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;
//				}

				a.opt.setInDarkMode(isChecked);
				
				if(buttonView.getTag()==null || GlobalOptions.isDark)
					a.changeToDarkMode();
				
				if(HeaderView2!=null) {
					HeaderView2.getBackground().setColorFilter(GlobalOptions.isDark?GlobalOptions.NEGATIVE:null);
					((TextView)HeaderView2.findViewById(R.id.text)).setTextColor(a.AppBlack);
				}
				
				buttonView.setTag(null);
			} break;
			case R.id.sw5:{
				a.opt.setUseVolumeBtn(isChecked);
			} break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		int id = v.getId();
		if(id==R.string.addd) {
			toPDF=true;
			((FlowTextView)v.findViewById(R.id.subtext)).setText("Oh PDF !");
			return false;
		} else if(id==R.string.pick_main) {
			try { // MLSN
				a.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
						.addCategory(Intent.CATEGORY_OPENABLE)
						.setType("*/*"), Constants.OpenBooksRequset);
			} catch (Exception e) {
				a.showT(e.getMessage());
			}
			return true;
		}
		Intent i = new Intent(getActivity(), CuteFileManager.class);
		startActivity(i);
		return false;
	}
	
	public void decorateHeader(View footchechers) {
		PDICMainActivity a = ((PDICMainActivity) getActivity()); if(a==null) return;
		View.OnClickListener clicker = v -> {
			int id = v.getId();
			CheckBox ck = (CheckBox) v;
			switch (id) {
				case R.id.CKPBEnable:
					a.opt.setPasteBinEnabled(ck.isChecked());
					break;
				case R.id.CKPBUpdate:{
					a.opt.setPasteBinUpdateDirect(ck.isChecked());
				} break;
				case R.id.CKPBBringBack:{
					a.opt.setPasteBinBringTaskToFront(ck.isChecked());
				} break;
			}
		};
		CheckBox ck = footchechers.findViewById(R.id.CKPBEnable);//粘贴至翻阅模式
		ck.setChecked(a.opt.getPasteBinEnabled());
		ck.setOnClickListener(clicker);

		if(PDICMainAppOptions.getPasteToPeruseModeWhenFocued()) ck.setTextColor(getResources().getColor(R.color.colorAccent));
		ck.setOnLongClickListener(v -> {
			CheckBox ck1 = (CheckBox) v;
			if(PDICMainAppOptions.setPasteToPeruseModeWhenFocued(!PDICMainAppOptions.getPasteToPeruseModeWhenFocued()))
				ck1.setTextColor(getResources().getColor(R.color.colorAccent));
			else
				ck1.setTextColor(Color.BLACK);
			return true;
		});

		ck = footchechers.findViewById(R.id.CKPBUpdate);
		ck.setChecked(a.opt.getPasteBinUpdateDirect());
		ck.setOnClickListener(clicker);
		ck = footchechers.findViewById(R.id.CKPBBringBack);
		ck.setChecked(a.opt.getPasteBinBringTaskToFront());
		ck.setOnClickListener(clicker);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ClipboardManager clipboardManager = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboardManager!=null && ClipListener!=null)
			clipboardManager.removePrimaryClipChangedListener(ClipListener);
		if(PasteFileTarget!=null) {
			try {
				PasteFileTarget.close();
			} catch (Exception ignored) { }
		}
	}
}
