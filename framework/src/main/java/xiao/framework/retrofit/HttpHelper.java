package xiao.framework.retrofit;

import android.text.TextUtils;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by xiaoguochang on 2016/1/30.
 */
public class HttpHelper {
    /**
     * 添加文本RequestBody
     *
     * @param params
     * @param paramName
     * @param text
     */
    public static void addTextBody(Map<String, RequestBody> params, String paramName, String text) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), text);
        params.put(paramName, body);
    }

    /**
     * 添加图片RequestBody
     *
     * @param params
     * @param paramName 参数名称
     * @param file
     */
    public static void addImageBody(Map<String, RequestBody> params, String paramName, File file) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        String fileName = file.getName();
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }
        String key = "%s\"; filename=\"%s";
        key = String.format(key, paramName, fileName);

        params.put(key, body);
    }

    /**
     * 添加图片RequestBody
     *
     * @param params
     * @param paramName 参数名称
     * @param fileName  文件名（如果fileName为空，则去系统时间）
     * @param data
     */
    public static void addImageBody(Map<String, RequestBody> params, String paramName, String fileName, byte[] data) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), data);
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }
        String key = "%s\"; filename=\"%s";
        key = String.format(key, paramName, fileName);

        params.put(key, body);
    }
}
