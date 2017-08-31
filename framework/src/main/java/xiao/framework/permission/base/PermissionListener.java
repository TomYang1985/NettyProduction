package xiao.framework.permission.base;

import android.app.Activity;

/**
 * 检查权限的监听器<br/>
 * a permissions check listener<br/>
 * Created by yang3 on 2015/9/10.
 */
public interface PermissionListener {

    /**
     * 所有权限获得授权时，调用<br/>
     * call when permissions are granted
     *
     * @param act
     * @param requestCode
     * @param grantResults
     * @param permissions
     */
    public void onPermissionsGranted(Activity act, int requestCode, int[] grantResults, String... permissions);

    /**
     * 有一个或多个权限被拒绝时，调用<br/>
     * call when one or some permissions are denied
     *
     * @param act
     * @param requestCode
     * @param grantResults
     * @param permissions
     */
    public void onPermissionsDenied(Activity act, int requestCode, int[] grantResults, String... permissions);

    /**
     * 权限被设置不再提醒
     * @param act
     * @param requestCode
     * @param grantResults
     * @param permissions
     */
    public void onNeverAskAgain(Activity act, int requestCode, int[] grantResults, String... permissions);

    /**
     * 是否显示权限的说明（为什么需要这个权限）<br/>
     * show the permissions rationale whether or not(why your app need their permissions?)
     *
     * @param act
     * @param requestCode
     * @param permissions
     */
    public void onShowRequestPermissionRationale(Activity act, int requestCode, String... permissions);

    /**
     * 出现错误时，调用<br/>
     * get a permissions error: almost params are wrong
     *
     * @param act
     * @param requestCode
     * @param grantResults
     * @param errorMsg
     * @param permissions
     */
    public void onPermissionsError(Activity act, int requestCode, int[] grantResults, String errorMsg, String... permissions);

}
