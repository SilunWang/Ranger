package mass.Ranger.User;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by v-mengyl on 2015/1/26.
 */
public class ShareToOtherApp {

    /**
     * 判断是否安装腾讯、新浪等指定的分享应用
     *
     * @param packageName 应用的包名
     */
    public boolean checkInstallation(String packageName, Activity activity) {
        try {
            activity.getApplication().getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void doInstall(Activity activity) {
        Uri uri = Uri.parse("market://details?id=应用包名");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        activity.getApplication().startActivity(it);
    }

    public void shareTo(Activity activity) {

    }

}
