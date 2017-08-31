package xiao.framework.retrofit.converter;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import xiao.framework.util.LogUtil;

/**
 * Created by xiaoguochang on 2016/2/4.
 * 自定义ResponseBody转换器
 */

public class StringResponseBodyConverter implements Converter<ResponseBody, String> {

    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            String data = value.string();
            if (!TextUtils.isEmpty(data)) {
                LogUtil.json(data);
            }

            return data;
        } finally {
            value.close();
        }
    }
}

