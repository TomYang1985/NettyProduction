package xiao.framework.rxjava;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import xiao.framework.FrameworkApi;
import xiao.framework.util.LogUtil;


/**
 * Created by xiaoguochang on 2016/2/4.
 */
public abstract class AbsSubscriber<T> extends Subscriber<T> {
    public static final int REQUEST_TIMEOUT = 408;
    //自定义网络异常
    public static final int CONNECT_EXCEPTION = 900;
    //未知错误
    public static final int UNKNOWN = -1001;
    //json解析错误
    public static final int PARSE_ERROR = -1002;
    //retrofit空指针异常
    public static final int CLIENT_CODE_RETROFIT_NULLPOINTER_ERR = -1003;
    //GSON实体解析错误
    public static final int CLIENT_CODE_GSON_ERR = -1004;

    @Override
    public void onError(Throwable e) {
        Throwable throwable = e;
        //获取最根源的异常
        while (throwable.getCause() != null) {
            e = throwable;
            throwable = throwable.getCause();
        }

        int code;
        String msg;

        if (e instanceof HttpException) {//HTTP错误
            HttpException httpException = (HttpException) e;
            code = httpException.code();
            msg = "网络错误";
        } else if (e instanceof SocketTimeoutException) {
            code = REQUEST_TIMEOUT;
            msg = "socket请求超时";
        } else if (e instanceof ConnectTimeoutException) {
            code = REQUEST_TIMEOUT;
            msg = "连接请求超时";
        } else if (e instanceof ConnectException) {
            code = CONNECT_EXCEPTION;
            msg = "网络不可访问";
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            code = PARSE_ERROR;
            msg = "数据解析错误";
        } else if (e instanceof CustomException) {//自定义错误
            CustomException exception = (CustomException) e;
            code = exception.code;
            msg = exception.message;
        } else if (e instanceof NullPointerException) {//空指针异常
            code = CLIENT_CODE_RETROFIT_NULLPOINTER_ERR;
            msg = "空指针错误";
        } else if (e instanceof IllegalStateException) {//JSON转换解析错误，例如Expected a string but was BEGIN_OBJECT
            code = CLIENT_CODE_GSON_ERR;
            msg = "GSON实体错误";
        } else {
            code = UNKNOWN;
            msg = "未知错误";
        }

        //LogUtil.d("请求错误："+ code + "--" + msg);
        Log.d(FrameworkApi.TAG, "请求错误："+ code + "--" + msg);
        onError(code, msg);
        doEnd();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
        doEnd();
    }

    /**
     * 订阅开始前执行，一般用于网络请求时开始UI操作
     */
    public void doStart() {

    }

    protected abstract void onSuccess(T t);

    protected abstract void onError(int code, String msg);

    /**
     * 订阅开始前执行，一般用于网络请求结束处理UI操作
     */
    public void doEnd() {

    }
}
