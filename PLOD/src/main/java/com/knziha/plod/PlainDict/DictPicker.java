package com.knziha.plod.PlainDict;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.knziha.plod.dictionary.Utils.IU;
import com.knziha.plod.widgets.Framer;
import com.knziha.plod.widgets.RecyclerViewmy.OnItemClickListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.GlobalOptions;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class DictPicker extends DialogFragment implements OnClickListener
{
	MainActivityUIBase a;

	public DictPicker() {
		this(null);
	}
	DictPicker(MainActivityUIBase a_){
		super();
		if(a_!=null)
			a = a_;
	}

	@Override
	public void onAttach(Context context){
		super.onAttach(context);
		CMN.Log("onAttach");
		refresh();
	}

	RecyclerView mRecyclerView;
	LinearLayoutManager lman;
	private List<String> mDatas;
	public boolean bShouldCloseAfterChoose=false;
	private HomeAdapter mAdapter;public HomeAdapter adapter(){return mAdapter;}
	private OnItemClickListener OIC = new OnItemClickListener(){
		@Override
		public void onItemClick(View view, int position) {
			int tmpPos = a.adapter_idx;
			a.adapter_idx=position;
			((MainActivityUIBase) a).switchToDictIdx(position);
			mAdapter.notifyItemChanged(tmpPos);
			mAdapter.notifyItemChanged(position);
			if(bShouldCloseAfterChoose)
				view.post(new Runnable() {
					@Override
					public void run() {
						dismiss();
					}});
		}
	};//public OnItemClickListener OIC(){return OIC;}
	public boolean isDirty=false;
	private Framer root;
	public void refresh() {
		//if(!isDirty) return;
		//isDirty=false;
		if(lman!=null)
			if(a.adapter_idx>lman.findLastVisibleItemPosition() || a.adapter_idx<lman.findFirstVisibleItemPosition()) {
				int target = Math.max(0, a.adapter_idx-5);
				lman.scrollToPositionWithOffset(target, 0);
				CMN.Log("scrolled");
			}
		if(a.dialogHolder!=null) {
			a.dialogHolder.setAlpha(1.0f);
			a.dialogHolder.setVisibility(View.VISIBLE);
		}
		//mAdapter.notifyDataSetChanged();

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		root = (Framer) inflater.inflate(R.layout.dialog_1_fc, container, false);
		//view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels*2/3);
		//view.setLayoutParams(new LayoutParams(-2,-1));
		//getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mRecyclerView = root.findViewById(R.id.choose_dict);
		lman = new LinearLayoutManager(a.getApplicationContext());
		mRecyclerView.setLayoutManager(lman);
		mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
		mRecyclerView.setMinimumWidth(getResources().getDisplayMetrics().widthPixels*2/3);
		mRecyclerView.setVerticalScrollBarEnabled(true);

		mAdapter.setOnItemClickListener(OIC);
		int LIP = lman.findLastVisibleItemPosition();
		if(a.adapter_idx>LIP) {
			int target = Math.max(0, a.adapter_idx-5);
			lman.scrollToPositionWithOffset(target, 0);
		}
		//view.setBackgroundResource(R.drawable.popup_shadow_l);
		return root;
	}

	public interface OnViewCreatedListener{
		void OnViewCreated(Dialog dialog);
	}
	OnViewCreatedListener ovcl;
	public void setOnViewCreatedListener(OnViewCreatedListener onViewCreatedListener) {
		ovcl=onViewCreatedListener;
	}

	int  width=-1,height=-1,mMaxH=-1;
	public void onResume()
	{
		super.onResume();
		if(width!=-1 || height!=-1)//
			if(getDialog()!=null) {
				Window window = getDialog().getWindow();
				if(window!= null) {
					WindowManager.LayoutParams  attr = window.getAttributes();
					if(attr.width!=width || attr.height!=height) {
						//CMN.Log("onResume_");
						window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
						window.setDimAmount(0.1f);
						window.setBackgroundDrawableResource(R.drawable.popup_shadow_l);
						root.mMaxHeight=mMaxH;
						window.setLayout(width,height);

						getView().post(() -> refresh());
					}
				}
				getDialog().setCanceledOnTouchOutside(true);
			}
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(ovcl!=null)
			ovcl.OnViewCreated(getDialog());

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide title of the dialog
		setStyle(STYLE_NO_FRAME, 0);
	}

	public interface OnItemLongClickListener{
		void onItemLongClick(View view,int position);
	}
	class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
	{

		private OnItemClickListener mOnItemClickListener;
		private OnItemLongClickListener mOnItemLongClickListener;

		public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
			this.mOnItemClickListener = mOnItemClickListener;
		}

		public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
			this.mOnItemLongClickListener = mOnItemLongClickListener;
		}
		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			MyViewHolder holder = new MyViewHolder(getActivity().getLayoutInflater().inflate(R.layout.diag1_fc_list_item, parent,
					false));
			return holder;
		}

		@Override
		public void onBindViewHolder(final MyViewHolder holder, final int position)
		{
			if(a.adapter_idx==position) {
				holder.itemView.setBackgroundColor(Color.parseColor("#4F7FDF"));//FF4081
				holder.tv.setTextColor(Color.WHITE);
			}else {
				holder.itemView.setBackgroundColor(Color.TRANSPARENT);//aaa0f0f0//Color.parseColor("#aaa0f0f0")
				holder.tv.setTextColor(Color.BLACK);
			}
			CharSequence name = a.md.get(position)._Dictionary_fName;
			if(SearchIncantation!=null) {
				Matcher m = SearchPattern.matcher(name);
				name = new SpannableStringBuilder(name);
				while(m.find()){
					((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
			holder.tv.setText(name);
			if(GlobalOptions.isDark) {
				holder.tv.setTextColor(Color.WHITE);
			}


			holder.cover.setTag(position);

			if(a.md.get(position).cover!=null)
				holder.cover.setImageDrawable(a.md.get(position).cover);
			else
				holder.cover.setImageDrawable(null);
			//判断是否设置了监听器
			if(mOnItemClickListener != null){
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//int position = holder.getLayoutPosition(); // 1
						mOnItemClickListener.onItemClick(holder.itemView,position); // 2
					}
				});
			}
			if(mOnItemLongClickListener != null){
				holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						int position = holder.getLayoutPosition();
						mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
						return true;
					}
				});
			}

		}

		@Override
		public int getItemCount()
		{
			return a.md.size();
		}

		class MyViewHolder extends ViewHolder
		{
			TextView tv;
			ImageView cover;
			public MyViewHolder(View view)
			{
				super(view);
				tv = (TextView) view.findViewById(R.id.id_num);
				cover = (ImageView) view.findViewById(R.id.cover);
				cover.setOnClickListener(DictPicker.this);
				//tv.setTextColor(Color.parseColor("#ff000000"));
				//tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,a.scale(81));//TODO: optimize
			}
		}
	}

	public String SearchIncantation;
	public Pattern SearchPattern;

	public void SetSearchIncantation(String pattern) {
		SearchIncantation = pattern;
		try {
			SearchPattern = Pattern.compile(SearchIncantation,Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			SearchPattern = Pattern.compile(SearchIncantation, Pattern.CASE_INSENSITIVE|Pattern.LITERAL);
		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		a=(MainActivityUIBase) getActivity();
		if(a.dialogHolder!=null)
			a.dialogHolder.setVisibility(View.VISIBLE);

		if(GlobalOptions.isDark) {
			try {
				Object Scrollbar = a.ScrollCacheField.get(mRecyclerView);
				Drawable ScrollbarDrawable = (Drawable) a.ScrollBarDrawableField.get(Scrollbar);
				ScrollbarDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			} catch (Exception e) {}
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.cover:
				Integer id = IU.parseInt(String.valueOf(v.getTag()));
				if(id!=null) {
					final View dv = a.inflater.inflate(R.layout.dialog_about,null);
					final TextView tv = ((TextView)dv.findViewById(R.id.resultN));
					TextView title = ((TextView)dv.findViewById(R.id.title));
					title.setText("词典信息");
					if(a.opt.isLarge) tv.setTextSize(tv.getTextSize());
					tv.setTextIsSelectable(true);
					//404

					tv.setText(Html.fromHtml(a.md.get(id).getAboutString(),Html.FROM_HTML_MODE_COMPACT));

					tv.setMovementMethod(LinkMovementMethod.getInstance());
					AlertDialog.Builder builder2 = new AlertDialog.Builder(a);
					builder2.setView(dv);
					final AlertDialog d = builder2.create();
					dv.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}});
					d.getWindow().setDimAmount(0);
					d.setCanceledOnTouchOutside(true);
					d.show();
					android.view.WindowManager.LayoutParams lp = d.getWindow().getAttributes();  //获取对话框当前的参数值
					lp.height = -2;
					d.getWindow().setAttributes(lp);
				}
				break;
		}
	}

}  