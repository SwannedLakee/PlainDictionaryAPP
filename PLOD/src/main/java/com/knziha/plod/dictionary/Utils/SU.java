/*  Copyright 2018 KnIfER Zenjio-Kang

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	Mdict-Java Query Library
*/

package com.knziha.plod.dictionary.Utils;

import com.knziha.plod.plaindict.CMN;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * @author KnIfER
 * @date 2018/05/31
 */
public class  SU{
	public static Object UniversalObject;
    public static boolean debug;//StringUtils
	public static byte[] EmptyBytes = new byte[0];
	
	public static String trimStart(String input) {
		int len = input.length();
        int st = 0;

        while ((st < len) && (input.charAt(st) <= ' ')) {
            st++;
        }
        
        return st > 0 ? input.substring(st, len) : input;
    }
	
    public static int compareTo(String strA,String strB,int start, int lim) {
        int len1 = strA.length();
        int len2 = strB.length();
        int _lim = Math.min(Math.min(len1-start, len2-start),lim);

        int k = 0;
        while (k < _lim) {
            char c1 = strA.charAt(k+start);
            char c2 = strB.charAt(k+start);
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return _lim==lim?0:len1 - len2;
    }


	public static void Log(Object... o) {
		StringBuilder msg= new StringBuilder("fatal_log_mdict : ");
		if(o!=null)
			for (Object value : o) {
				if (value instanceof Exception) {
					ByteArrayOutputStream s = new ByteArrayOutputStream();
					PrintStream p = new PrintStream(s);
					((Exception) value).printStackTrace(p);
					msg.append(s.toString());
				}
				msg.append(value).append(" ");
			}
		System.out.println(msg);
	}
	
	public static long stst;
	public static long ststrt;
	public static long stst_add;
	public static void rt() {
		ststrt = System.currentTimeMillis();
	}
	public static void pt(Object...args) {
		SU.Log(CMN.listToStr(args)+" "+(System.currentTimeMillis()-ststrt));
	}
	
	public static boolean isNoneSetFileName(String fname) {
		int suffix_index = fname.lastIndexOf(".");
		return suffix_index<0||!fname.regionMatches(true,suffix_index+1, "set", 0, 3);
	}
	
	public static String legacySetFileName(String line) {
		if(isNoneSetFileName(line)) { //legacy
			return line+".set";
		}
		return line;
	}
	
	public static boolean isNotEmpty(CharSequence cs) {
		int len=cs.length();
		if(len>0) {
			int st = 0;
			while ((st < len) && (cs.charAt(st) <= ' ')) {
				st++;
				len--;
			}
			if(len>0) {
				while ((st < len) && (cs.charAt(len - 1) <= ' ')) {
					len--;
				}
			}
		}
		return len>0;
	}
	
	public boolean CharsequenceEqual(CharSequence cs1, CharSequence cs2) {
		if(cs1!=null&&cs2!=null) {
			int len1=cs1.length();
			if(len1==cs2.length()) {
				for (int i = 0; i < len1; i++) {
					if(cs1.charAt(i)!=cs2.charAt(i)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
	

