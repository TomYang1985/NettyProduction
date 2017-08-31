package xiao.framework.permission;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.view.View;

import xiao.framework.permission.base.PermissionListener;
import xiao.framework.permission.base.PermissionsDispatcher;
import xiao.framework.util.AppUtils;

/**
 * Created by xiaoguochang on 2016/4/12.
 * APP的所有权限管理类
 */
public class PermissionManager {
//    //相册和相机权限
//    public static final int REQUEST_CODE_GALLERY_CAMERA = 1;
//
//    public static final String[] PERMISSION_GALLERY_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
//    ///********************************权限提示信息**************************************///
//    public static String APP_NAME = "\"荷塘\"";
//    //权限使用说明
//    public static String STRING_CAMERA_EXTERNAL_RATIONALE = APP_NAME + "需要使用相机权限和读外部存储权限用于访问相册和拍照";
//    //未被授权
//    public static String STRING_CAMERA_EXTERNAL_DENIED = "相机权限和读外部存储未被授权，不能进行操作";
//    //授权被拒绝并不再提醒
//    public static String STRING_CAMERA_EXTERNAL_NEVERASKAGAIN = "相机权限和读外部存储授权被拒绝, 请在\"权限管理\"中配置权限";
//
//    private Activity mAtivity;
//    private Callback mCallback;
//
//    public PermissionManager() {
//
//    }
//
//    //权限回调监听
//    private PermissionListener permissionListener = new PermissionListener() {
//        @Override
//        public void onPermissionsGranted(Activity act, int requestCode, int[] grantResults, String... permissions) {
//            if (mCallback != null) {
//                mCallback.onPermissionsGranted(requestCode, permissions);
//            }
//        }
//
//        @Override
//        public void onNeverAskAgain(Activity act, int requestCode, int[] grantResults, String... permissions) {
//            String info = "";
//
//            switch (requestCode) {
//                case REQUEST_CODE_GALLERY_CAMERA:
//                    info = STRING_CAMERA_EXTERNAL_NEVERASKAGAIN;
//                    break;
//            }
//
//            T.showShort(mAtivity, info);
//        }
//
//        @Override
//        public void onPermissionsDenied(Activity act, int requestCode, int[] grantResults, String... permissions) {
//            T.showShort(mAtivity, STRING_CAMERA_EXTERNAL_DENIED);
//        }
//
//        @Override
//        public void onShowRequestPermissionRationale(final Activity act, final int requestCode, final String... permissions) {
//            String info = "";
//
//            switch (requestCode) {
//                case REQUEST_CODE_GALLERY_CAMERA:
//                    info = STRING_CAMERA_EXTERNAL_RATIONALE;
//                    break;
//            }
//
//            showPermissionDialog(info, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PermissionsDispatcher.requestPermissions(act, requestCode, permissions);
//                }
//            });
//        }
//
//        @Override
//        public void onPermissionsError(Activity act, int requestCode, int[] grantResults, String errorMsg, String... permissions) {
//            T.showShort(mAtivity, "权限错误");
//        }
//    };
//
//    /**
//     * 检查权限
//     *
//     * @param activity
//     * @param requestCode
//     * @param callback
//     */
//    public void checkPermissions(Activity activity, int requestCode, Callback callback) {
//        mAtivity = activity;
//        mCallback = callback;
//        String[] permissions = null;
//
//        switch (requestCode) {
//            case REQUEST_CODE_GALLERY_CAMERA:
//                permissions = PERMISSION_GALLERY_CAMERA;
//                break;
//        }
//
//        //6.0以下的系统直接返回
//        if (AppUtils.getSDKVersion() < Build.VERSION_CODES.M) {
//            callback.onPermissionsGranted(requestCode, permissions);
//            return;
//        }
//
//        if (permissions != null) {
//            PermissionsDispatcher.checkPermissions(mAtivity, REQUEST_CODE_GALLERY_CAMERA, permissionListener,
//                    permissions);
//        }
//    }
//
//    /**
//     * 处理授权结果
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        PermissionsDispatcher.onRequestPermissionsResult(mAtivity, requestCode, permissions, grantResults, permissionListener);
//    }
//
//    private void showPermissionDialog(String msg, View.OnClickListener listener) {
//        new PermissionDialog(mAtivity, listener).setMessage(msg).show();
//    }
//
//    public interface Callback {
//        void onPermissionsGranted(int requestCode, String... permissions);
//    }
}
