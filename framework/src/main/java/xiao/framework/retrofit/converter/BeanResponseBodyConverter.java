package xiao.framework.retrofit.converter;

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
//public class BeanResponseBodyConverter<T> implements Converter<ResponseBody, T>{
//    private final TypeAdapter<T> adapter;
//
//    BeanResponseBodyConverter(TypeAdapter<T> adapter) {
//        this.adapter = adapter;
//    }
//
//    @Override public T convert(ResponseBody value) throws IOException {
//        if (Config.isDebug) {
//            LogUtil.json(value.string());
//        }
//        Reader reader = value.charStream();
//        try {
//            return adapter.fromJson(reader);
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException ignored) {
//                }
//            }
//        }
//    }
//}
public class BeanResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    BeanResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        LogUtil.json(response);
        return gson.fromJson(response, type);
    }
}

