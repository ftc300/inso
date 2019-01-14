package com.inshow.watch.android.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 *
 */
public class CheckPermission {

	private static final String PERMISSION_READ_CONTACT = "android.permission.READ_CONTACTS";
	public static boolean  checkContact(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return ContextCompat.checkSelfPermission(context,PERMISSION_READ_CONTACT) == PackageManager.PERMISSION_GRANTED;
		}
		return  true;
	}
}
