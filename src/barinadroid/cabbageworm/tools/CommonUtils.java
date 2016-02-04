package barinadroid.cabbageworm.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class CommonUtils
{
	public static String getApplicationName(Context context)
	{
		String name = new String();
		try
		{
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			name = pInfo.versionName;
		}
		catch(NameNotFoundException e)
		{
			LogWorm.w("Can't get app name.", e);
		}
		return name;
	}
}