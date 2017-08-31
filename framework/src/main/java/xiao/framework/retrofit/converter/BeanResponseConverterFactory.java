package xiao.framework.retrofit.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by xiaoguochang on 2016/1/9.
 */
public class BeanResponseConverterFactory extends Converter.Factory {
    private final Gson gson;

    public static BeanResponseConverterFactory create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static BeanResponseConverterFactory create(Gson gson) {
        return new BeanResponseConverterFactory(gson);
    }

    private BeanResponseConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }

        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new BeanResponseBodyConverter<>(gson, type);
    }

//    @Override
//    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations,
//                                                          Retrofit retrofit) {
//        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new BeanRequestBodyConverter<>(new Gson(), adapter);
//    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new BeanRequestBodyConverter<>(gson, adapter);
    }
}
//public class BeanResponseConverterFactory extends Converter.Factory {
//    /**
//     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
//     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
//     */
//    public static BeanResponseConverterFactory create() {
//        return create(new Gson());
//    }
//
//    /**
//     * Create an instance using {@code gson} for conversion. Encoding to JSON and
//     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
//     */
//    public static BeanResponseConverterFactory create(Gson gson) {
//        return new BeanResponseConverterFactory(gson);
//    }
//
//    private final Gson gson;
//
//    private BeanResponseConverterFactory(Gson gson) {
//        if (gson == null) throw new NullPointerException("gson == null");
//        this.gson = gson;
//    }
//
//    @Override
//    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
//                                                            Retrofit retrofit) {
//        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new BeanResponseBodyConverter<>(adapter);
//    }
//
//    @Override
//    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations,
//                                                          Retrofit retrofit) {
//        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new BeanRequestBodyConverter<>(gson, adapter);
//    }
//
//}
