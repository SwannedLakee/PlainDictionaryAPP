package com.knziha.plod.PlainDict;

import android.os.Build;

public class VersionUtils {
	
	public static void checkVersion() {
		if(true||PDICMainAppOptions.checkVersionBefore_4_0()) {
			PDICMainAppOptions.uncheckVersionBefore_4_0();
//			PDICMainAppOptions.setShareTarget(0);
//			PDICMainAppOptions.setPasteTarget(1);
//			PDICMainAppOptions.setNotifyComboRes(false);
//			PDICMainAppOptions.setRebuildToast(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P);
//			PDICMainAppOptions.setBackPrevention(0);
//			PDICMainAppOptions.setFloatHideNavigation(false);
		}
	}
}
