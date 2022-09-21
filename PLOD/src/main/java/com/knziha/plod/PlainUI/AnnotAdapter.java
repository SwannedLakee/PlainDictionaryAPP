package com.knziha.plod.PlainUI;

import static com.knziha.plod.db.LexicalDBHelper.FIELD_CREATE_TIME;
import static com.knziha.plod.widgets.ViewUtils.EmptyCursor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.knziha.filepicker.widget.MaterialCheckbox;
import com.knziha.paging.ConstructorInterface;
import com.knziha.paging.CursorAdapter;
import com.knziha.paging.CursorReader;
import com.knziha.paging.CursorReaderMultiSortNum;
import com.knziha.paging.MultiFieldPagingCursorAdapter;
import com.knziha.paging.PagingAdapterInterface;
import com.knziha.paging.PagingCursorAdapter;
import com.knziha.plod.db.LexicalDBHelper;
import com.knziha.plod.dictionarymodels.BookPresenter;
import com.knziha.plod.plaindict.CMN;
import com.knziha.plod.plaindict.MainActivityUIBase;
import com.knziha.plod.plaindict.MainActivityUIBase.ViewHolder;
import com.knziha.plod.plaindict.PDICMainAppOptions;
import com.knziha.plod.plaindict.R;
import com.knziha.plod.widgets.ViewUtils;
import com.knziha.text.ColoredTextSpan;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by KnIfER on 2021/11/16.
 */

public class AnnotAdapter extends RecyclerView.Adapter<AnnotAdapter.VueHolder> implements View.OnClickListener, PagingCursorAdapter.OnLoadListener, View.OnLongClickListener {
	BookPresenter presenter;
	public boolean showDelete;
	int resourceID;
	int textViewResourceID;
	MainActivityUIBase a;
	public boolean darkMode;
	WeakReference<BookNotes> bookNotesRef = ViewUtils.DummyRef;
	final int scope;
	
	private PagingAdapterInterface<AnnotationReader> DummyReader = new CursorAdapter<>(EmptyCursor, new AnnotationReader());
	PagingAdapterInterface<? extends AnnotationReader> dataAdapter = DummyReader;
	ImageView pageAsyncLoader;
	
	public AnnotAdapter(MainActivityUIBase a, int resource, int textViewResourceId, BookPresenter md_
			, SQLiteDatabase database, int scope
			, RecyclerView lv) {
		//this(a,resource,textViewResourceId,objects);
		this.a=a;
		resourceID=resource;
		textViewResourceID=textViewResourceId;
		presenter=md_;
		this.scope = scope;
		refresh(md_, database, lv);
	}
	
	@Override
	public void onClick(View v) {
		BookNotes notes = bookNotesRef.get();
		if (notes != null) {
			notes.click(this, v, (VueHolder)v.getTag(), false);
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		BookNotes notes = bookNotesRef.get();
		if (notes != null) {
			notes.click(this, v, (VueHolder)v.getTag(), true);
		}
		return true;
	}
	
	public static class AnnotationMultiSortReader extends AnnotationReader implements CursorReaderMultiSortNum{
		@Override
		public void ReadCursor(Cursor cursor, long rowID, long[] sortNums) {
			multiSorts = 3;
			sort_numbers = sortNums;
			//sort_numbers = new long[sortNums.length];
			//System.arraycopy(sortNums, 0, sort_numbers, 0, sortNums.length);
			ReadCursor(cursor, rowID, sortNums[0]);
		}
		static ConstructorInterface<AnnotationReader> readerMaker = length -> new AnnotationMultiSortReader();
	}
	
	public static class AnnotationReader implements CursorReader{
		public long row_id;
		public long sort_number;
		public long[] sort_numbers;
		public String entryName;
		public String annotText;
		public long position;
		public long bid;
		public JSONObject annot;
		int multiSorts = 0;
		
		@Override
		public void ReadCursor(Cursor cursor, long rowID, long sortNum) {
			if (rowID!=-1) {
				row_id = rowID;
				sort_number = sortNum;
			} else {
				row_id = cursor.getLong(0);
				sort_number = cursor.getLong(1);
			}
			entryName = cursor.getString(multiSorts+2);
			annotText = cursor.getString(multiSorts+3);
			position = cursor.getLong(multiSorts+4);
			bid = cursor.getLong(multiSorts+5);
			try {
				annot = JSONObject.parseObject(cursor.getString(multiSorts+6));
			} catch (Exception e) {
				CMN.debug(e);
				annot = new JSONObject();
			}
		}
		
		@Override
		public String toString() {
			return "WebAnnotationCursorReader{" +
					"lex='" + annotText + '\'' +
					"entry='" + entryName + '\'' +
					'}';
		}
		static ConstructorInterface<AnnotationReader> readerMaker = length -> new AnnotationReader();
	}
	
	public void refresh(BookPresenter invoker, SQLiteDatabase database, RecyclerView lv) {
		//if(invoker!=md || con!=con_ || cr==null)
		try {
			presenter = invoker;
			dataAdapter.close();
			dataAdapter = DummyReader;
			{
				int sortType = scope==0?PDICMainAppOptions.annotDBSortBy():PDICMainAppOptions.annotDB1SortBy();
				sortType = 2;
				boolean bSingleThreadLoading = false;
				if (bSingleThreadLoading) {
					String sql = "select id,last_edit_time,entry,lex,pos,bid,annot from "
							+LexicalDBHelper.TABLE_BOOK_ANNOT_v2;
					if(scope == 1) {
						sql += " where bid=?";
					}
					Cursor cursor = database.rawQuery(sql+" order by last_edit_time desc"
							, scope == 1?new String[]{presenter.getId()+""}:null);
					CMN.Log("查询个数::"+cursor.getCount());
					dataAdapter = new CursorAdapter<>(cursor, new AnnotationReader());
					notifyDataSetChanged();
				}
				else if (sortType <= 1) {
					if (pageAsyncLoader == null) {
						pageAsyncLoader = new ImageView(a);
					}
					saveListPosition();
					PagingCursorAdapter<AnnotationReader> dataAdapter = new PagingCursorAdapter<>(database
							//, new SimpleClassConstructor<>(HistoryDatabaseReader.class)
							, AnnotationReader.readerMaker
							, AnnotationReader[]::new);
					this.dataAdapter = dataAdapter;
					dataAdapter.bindTo(lv)
							.setAsyncLoader(a, pageAsyncLoader)
							.sortBy(LexicalDBHelper.TABLE_BOOK_ANNOT_v2, FIELD_CREATE_TIME, sortType==0, "entry,lex,pos,bid,annot");
					if (scope == 1) {
						dataAdapter.where("bid=?", new String[]{presenter.getId() + ""});
					}
					long[] pos = savedPositions.get(getFragmentId());
					long lastTm = 0, offset = 0;
					if (pos != null) {
						lastTm = pos[0];
						offset = pos[1];
					}
					dataAdapter.startPaging(lastTm, offset, 20, 15, this);
					notifyDataSetChanged();
				}
				else {
					if (pageAsyncLoader == null) {
						pageAsyncLoader = new ImageView(a);
					}
					saveListPosition();
					MultiFieldPagingCursorAdapter<AnnotationMultiSortReader> dataAdapter = new MultiFieldPagingCursorAdapter(database
							//, new SimpleClassConstructor<>(HistoryDatabaseReader.class)
							, AnnotationMultiSortReader.readerMaker
							, AnnotationMultiSortReader[]::new);
					this.dataAdapter = dataAdapter;
					String[] sortBy;
					if (sortType<=5) {
						String sf = sortType <= 3 ? "tPos" : FIELD_CREATE_TIME;
						sortBy = new String[]{"bid", "pos", sf, "ROWID"};
					} else {
						sortBy = new String[]{"bid", FIELD_CREATE_TIME};
					}
					dataAdapter.bindTo(lv)
							.setAsyncLoader(a, pageAsyncLoader)
							.sortBy(LexicalDBHelper.TABLE_BOOK_ANNOT_v2, sortBy, sortType%2==0, "entry,lex,pos,bid,annot");
					if (scope == 1) {
						dataAdapter.where("bid=?", new String[]{presenter.getId() + ""});
					}
					long[] pos = savedPositions.get(getFragmentId());
					long lastTm = 0, offset = 0;
					if (pos != null) {
						lastTm = pos[0];
						offset = pos[1];
					}
					// dataAdapter.new_sorts(0)
					dataAdapter.startPaging(null, offset, 20, 15, this);
					notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
			CMN.debug(e);
			dataAdapter = DummyReader;
		}
	}
	
	/** type[act|ui|db], long[]{pos, view offset} */
	public final static SparseArray<long[]> savedPositions = new SparseArray();
	
	public int getFragmentId() {
		return (scope<<15)|a.thisActType.ordinal();
	}
	
	private void saveListPosition() {
		try {
			BookNotes notes = bookNotesRef.get();
			View view = notes.viewList[scope].getChildAt(0);
			if (view!=null) {
				VueHolder holder = (VueHolder) view.getTag();
				AnnotationReader reader = (AnnotationReader) holder.tag;
				// CMN.debug("saveListPosition::", holder.getLayoutPosition());
				if (holder.getLayoutPosition() > 0) {
					savedPositions.put(getFragmentId(), new long[]{reader.sort_number, view.getTop()});
				} else {
					savedPositions.remove(getFragmentId());
				}
				CMN.debug("savedPositions::save::", scope+" "+reader.entryName+" "+new Date(reader.sort_number).toLocaleString());
			}
		} catch (Exception e) {
			CMN.debug(e);
		}
	}
	
	@NonNull
	@Override
	public AnnotAdapter.VueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		VueHolder vh = new VueHolder(a, parent, LayoutInflater.from(a).inflate(R.layout.listview_item01_book_notes, parent, false));
		vh.itemView.setOnClickListener(this);
		vh.itemView.setOnLongClickListener(this);
		return vh;
	}
	
	@Override
	public void onBindViewHolder(@NonNull AnnotAdapter.VueHolder holder, int position) {
		ViewHolder vh = holder.vh;
		String lex, entry, bookName;
		AnnotationReader reader = null;
		try {
			reader = dataAdapter.getReaderAt(position);
			lex=reader.annotText;
			entry=reader.entryName;
			bookName=a.getBookInLstNameByIdNoCreation(reader.bid);
		} catch (Exception e) {
			lex="!!!Error: "+e.getLocalizedMessage();
			entry="";
			bookName="";
		}
		holder.tag = reader;
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(" ");
		ssb.append(lex);
		ssb.append(" ");
		
		vh.title.setTextColor(a.AppBlack);
		
		ViewUtils.setVisible(vh.subtitle, true);
		
		int color=0xffffaaaa, type=0;
		if(reader!=null) {
			String note = reader.annot.getString("note");
			try {
				color = reader.annot.getInteger("clr");
			} catch (Exception e) {
				//CMN.debug(e);
			}
			try {
				type = reader.annot.getInteger("typ");
			} catch (Exception e) {
				//CMN.debug(e);
			}
			ViewUtils.setVisible(vh.preview, note!=null);
			ViewUtils.setVisibility(holder.dotVue, note!=null);
			
			int tmp = 2;
			int c = ColorUtils.blendARGB(a.AppWhite, a.AppBlack, tmp==0?0.08f:tmp==1?0.5f:0.8f);
			tmp = PDICMainAppOptions.listPreviewFont();
			int size = tmp==0?12:tmp==1?14:17;
			
			vh.preview.setText(note);
			vh.preview.setTextColor(c);
			vh.preview.setTextSize(size);
		} else {
			ViewUtils.setVisible(vh.preview, false);
			ViewUtils.setVisible(holder.dotVue, false);
		}
		ssb.setSpan(new ColoredTextSpan(color, 8.f, type==1?2:1), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		vh.title.setText(ssb);
		
		ssb.clear();
		ssb.clearSpans();
		if (!TextUtils.isEmpty(entry)) {
			ssb
				//ssb.append(" — ").
				.append(Character.toUpperCase(entry.charAt(0)))
				.append(entry, 1, entry.length())
				.append(" | ")
			;
		}
		ssb.append(bookName);
		vh.subtitle.setText(ssb);
		//vh.ivDel.setVisibility(showDelete?View.VISIBLE:View.GONE);
	}
	
	@Override
	public int getItemCount() {
		return dataAdapter.getCount();
	}

	@Override
	public long getItemId(int position) {
		try {
			return dataAdapter.getReaderAt(position).row_id;
		} catch (Exception e) {
			CMN.debug(e);
			return 0;
		}
	}
	
	public void clear() {
		dataAdapter.close();
		dataAdapter=DummyReader;
	}
	
	static class VueHolder extends RecyclerView.ViewHolder{
		final MainActivityUIBase.ViewHolder vh;
		MaterialCheckbox dotVue;
		View typVue;
		AnnotationReader tag;
		VueHolder(MainActivityUIBase a, ViewGroup parent, View view) {
			super(view);
			view.setTag(this);
			ViewGroup vg = (ViewGroup)view;
			ViewHolder vh = new ViewHolder(a, 0, (ViewGroup)vg.getChildAt(1));
			dotVue = vg.getChildAt(0).findViewById(R.id.dotVue);
			dotVue.bgFrame = 0xff999999;
			typVue = vg.getChildAt(2);
			this.vh = vh;
			//ViewUtils.setPadding(vh.itemView, (int) (GlobalOptions.density*15), -1, -1, -1);
			vh.title.setMaxLines(3);
			vh.title.setEllipsize(TextUtils.TruncateAt.END);
			vh.subtitle.setMaxLines(2);
		}
	}
	
	public void setBookNotes(BookNotes bookNotes) {
		this.bookNotesRef = new WeakReference<>(bookNotes);
	}
	
	@Override
	public void onLoaded(PagingAdapterInterface adapter) {
		BookNotes notes = bookNotesRef.get();
		if (notes!=null) {
			notes.viewList[scope].suppressLayout(false);
		}
	}
}
