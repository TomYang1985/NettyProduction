package xiao.framework.retrofit.converter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;
import xiao.framework.util.LogUtil;

/**
 * Created by xiaoguochang on 2016/2/4.
 * 自定义ResponseBody转换器
 */
public class BeanRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public BeanRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
//        Buffer buffer = new Buffer();
//        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
//        JsonWriter jsonWriter = gson.newJsonWriter(writer);
//        try {
//            adapter.write(jsonWriter, value);
//            jsonWriter.flush();
//        } catch (IOException e) {
//            throw new AssertionError(e); // Writing to Buffer does no I/O.
//        }
//
//        RequestBody data = RequestBody.create(MEDIA_TYPE, buffer.readByteString());
//
//        return data;

        String content = gson.toJson(value);
        if (!TextUtils.isEmpty(content)) {
            LogUtil.json(content);
            content = URLEncoder.encode(content, "UTF-8");
        }

        return RequestBody.create(MEDIA_TYPE, content);
    }


}
