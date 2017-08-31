package xiao.framework.retrofit.converter;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;
import xiao.framework.util.LogUtil;

/**
 * Created by xiaoguochang on 2016/2/4.
 * 自定义ResponseBody转换器
 */
public class StringRequestBodyConverter implements Converter<String, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public RequestBody convert(String value) throws IOException {
        if (!TextUtils.isEmpty(value)) {
            LogUtil.json(value);
        }

        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        writer.write(value);
        writer.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }

}
