package com.knziha.plod.plaindict;

import com.knziha.plod.dictionarymodels.BookPresenter;

public interface MdictServerLet {
	String md_getName(int i);
	BookPresenter md_get(int i);
	int md_getSize();
}