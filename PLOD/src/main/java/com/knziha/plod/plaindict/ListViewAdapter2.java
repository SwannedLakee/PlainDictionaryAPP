package com.knziha.plod.plaindict;

import static com.knziha.plod.plaindict.PDICMainActivity.ViewHolder;
import static com.knziha.plod.plaindict.WebViewListHandler.WEB_VIEW_SINGLE;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;

import com.knziha.plod.dictionarymodels.resultRecorderDiscrete;
import com.knziha.plod.dictionarymodels.resultRecorderScattered;
import com.knziha.plod.widgets.ViewUtils;

import java.util.List;

// 联合搜索、全文搜索词条列表
public class ListViewAdapter2 extends BasicAdapter {
	final MainActivityUIBase a;
	final PDICMainAppOptions opt;
	final int id;
	int itemId = R.layout.listview_item0;
	public ListViewAdapter2(MainActivityUIBase a, ViewGroup vg, MenuBuilder allMenus, List<MenuItemImpl> contentMenu, int resId, int id)
	{
		this(a, vg, allMenus, contentMenu, id);
		itemId = resId;
	}
	//构造
	public ListViewAdapter2(MainActivityUIBase a, ViewGroup vg, MenuBuilder allMenus, List<MenuItemImpl> contentMenu, int id)
	{
		super(a.contentUIData, a.weblistHandler, allMenus, contentMenu);
		this.webviewHolder=vg;
		this.a = a;
		this.opt = a.opt;
		this.id = id;
		combining_search_result = new resultRecorderDiscrete(a);
	}
	@Override
	public int getCount() {
		return combining_search_result.size();
	}
	
	public int expectedPos;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//return lstItemViews.get(position);
		ViewHolder vh;
		CharSequence currentKeyText = combining_search_result.getResAt(a, position);
		if(convertView!=null) {
			vh=(ViewHolder) convertView.getTag();
		} else {
			vh=new ViewHolder(a.getApplicationContext(), itemId, parent);
			//vh.itemView.setOnClickListener(this);
			//vh.itemView.setOnLongClickListener(MainActivity.this);
			if(itemId==R.layout.listview_item1)
				vh.subtitle.setTag(vh.itemView.findViewById(R.id.counter));
		}
		if( vh.title.getTextColors().getDefaultColor()!=a.AppBlack) {
			//decorateBackground(vh.itemView);
			vh.title.setTextColor(a.AppBlack);
		}
		vh.title.setText(currentKeyText);
//			if(combining_search_result.mflag.data!=null){
//				vh.subtitle.setText(Html.fromHtml(currentDictionary.appendCleanDictionaryName(null).append("<font color='#2B4391'> < ").append(combining_search_result.mflag.data).append(" ></font >").toString()));
//			} else {
//
//			}
		vh.subtitle.setText(a.getBookById(combining_search_result.bookId).getDictionaryName());
		if(id==2) {
			TextView v = ((TextView) vh.subtitle.getTag());
			if(combining_search_result.count!=null) {
				v.setText(combining_search_result.count);
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.GONE);
			}
		}
		//vh.itemView.setTag(R.id.position,position);
		return vh.itemView;
	}
	
	@Override
	public void shutUp() {
		combining_search_result.shutUp();
		notifyDataSetChanged();
	}
	
	@Override
	public void SaveVOA() {
		//111
//		if(this!=a.adaptermy2||a.opt.getRemPos2()) {
//			new a.SaveAndRestorePagePosDelegate().SaveVOA(contentUIData.PageSlider.WebContext, this);
//		}
//		//lastClickTime=System.currentTimeMillis();
//		if(Kustice && PDICMainAppOptions.getHistoryStrategy8() == 2
//				&& combining_search_result.shouldSaveHistory()
//				&& !PDICMainAppOptions.getHistoryStrategy0())
//			a.insertUpdate_histroy(currentKeyText, 0, webviewHolder);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		if(a.checkAllWebs(combining_search_result, view, pos)) return;
		contentUIData.mainProgressBar.setVisibility(View.GONE);
		userCLick=true;
		lastClickedPosBeforePageTurn=-1;
//		a.bNeedReAddCon=false;
		super.onItemClick(parent, view, pos, id);
	}
	
	
	@Override
	public void onItemClick(int pos){//lv2 mlv1 mlv2
		a.shuntAAdjustment();
		weblistHandler.WHP.touchFlag.first=true;
		if(a.PeruseListModeMenu.isChecked()) {
			PeruseView pv = a.getPeruseView();
			pv.RestoreOldAI();
			a.JumpToPeruseMode(combining_search_result.getResAt(a, pos).toString(), combining_search_result.getBooksAt(pv.bookIds, pos), -2, true);
			a.imm.hideSoftInputFromWindow(a.main.getWindowToken(),0);
			return;
		}
		if(a.DBrowser!=null) return;
		
		lastClickedPosBeforePageTurn = lastClickedPos;
		
		if(pos<0 || pos>=getCount()) {
			a.show(R.string.endendr);
			return;
		}
		
		
		if(allMenus!=null) {
			allMenus.setItems(contentMenus);
		}
		//a.showT(allMenus+""+contentMenus);
		
		
		if(id==2) {
			weblistHandler.bShowInPopup = false;
			weblistHandler.bMergeFrames = -2;
			
//			boolean bUseMergedUrl = false;
//			weblistHandler.setViewMode(WEB_LIST_MULTI, bUseMergedUrl);
//			weblistHandler.bMergeFrames = bUseMergedUrl;
//			a.ensureContentVis(weblistHandler, contentUIData.webSingleholder);
//			weblistHandler.initMergedFrame(bUseMergedUrl, false, bUseMergedUrl);

//			if(bUseMergedUrl) {
//				ViewUtils.addViewToParentUnique(weblistHandler.getMergedFrame().rl, weblistHandler.WHP1);
//			}
			//a.showT("111 !");
			
			//////////a.locateNaviIcon();
//			if(opt.getRemPos2() && !a.bRequestedCleanSearch) {
//				new a.SaveAndRestorePagePosDelegate().SaveAndRestorePagesForAdapter(this, pos);
//			}
			//111
		}
		else {
			boolean bUseMergedUrl = false;
			weblistHandler.setViewMode(WEB_VIEW_SINGLE, bUseMergedUrl, null);
			weblistHandler.initMergedFrame(false, false, bUseMergedUrl);
			if(bUseMergedUrl) {
				ViewUtils.addViewToParentUnique(weblistHandler.getMergedFrame().rl, a.webSingleholder);
			}
			
			a.ensureContentVis(contentUIData.webSingleholder, weblistHandler);
		}

		a.ActivedAdapter=this;
		super.onItemClick(pos);

//		contentUIData.webcontentlister.setVisibility(View.VISIBLE);
//		contentUIData.webcontentlister.setAlpha(1);
		
		if(!a.bWantsSelection) {
			a.imm.hideSoftInputFromWindow(a.main.getWindowToken(),0);
			a.etSearch.clearFocus();
		}
		
		combining_search_result.renderContentAt(lastClickedPos, a,this, weblistHandler);
		
		a.decorateContentviewByKey(null, currentKeyText = combining_search_result.getResAt(a, pos).toString());
		
		if(userCLick) {
			userCLick=false;
		} else {
			Kustice=true;
		}
		
		a.bWantsSelection=true;
//		if(PDICMainAppOptions.getInPageSearchAutoUpdateAfterClick()){
//			a.prepareInPageSearch(currentKeyText, true);
//		} //333
		contentUIData.webcontentlister.setTag(R.id.image, a.PhotoPagerHolder!=null&&a.PhotoPagerHolder.getParent()!=null?false:null);
		contentUIData.PageSlider.TurnPageEnabled=(this==a.adaptermy2?opt.getPageTurn2():opt.getPageTurn1())&&opt.getTurnPageEnabled();
		a.etSearch_ToToolbarMode(1);
		
		if(!combining_search_result.addHistoryIfNeeded(a)
				&& !PDICMainAppOptions.storeNothing()
				&& PDICMainAppOptions.storeClick()
				&& combining_search_result.shouldSaveHistory()
				&& (userCLick||PDICMainAppOptions.storePageTurn()==0)) {
			a.addHistroy(currentKeyText, 0, webviewHolder);
		}
	}
	
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String currentKeyText() {
		return combining_search_result instanceof resultRecorderScattered?
				((resultRecorderScattered)combining_search_result).getCurrentKeyText(a, lastClickedPos)
				:currentKeyText;
	}
}
