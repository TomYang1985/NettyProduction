package xiao.framework.util;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.google.gson.GsonBuilder;

import xiao.framework.FrameworkApi;


//Logcat管理类
public class LogUtil {
    private static Logger androidLogger;

    static {
        if (false) {
            LogConfiguration config = new LogConfiguration.Builder()
                    .tag(FrameworkApi.TAG)                   // Specify TAG, default: "X-LOG"
                    .t()                                                // Enable thread info, disabled by default
                    .st(3)                                              // Enable stack trace info with depth 2, disabled by default
                    .b()                                                // Enable border, disabled by default
                    // .jsonFormatter(new MyJsonFormatter())               // Default: DefaultJsonFormatter
                    // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
                    // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
                    // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
                    // .stackTraceFormatter(new MyStackTraceFormatter())   // Default: DefaultStackTraceFormatter
                    // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
                    // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
                    //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
                    // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
                    //     "whitelist1", "whitelist2", "whitelist3"))
                    // .addInterceptor(new MyInterceptor())                // Add a log interceptor
                    .build();
            if(!XLog.sIsInitialized) {
                XLog.init(config);
            }

            androidLogger = new Logger.Builder().printers(new AndroidPrinter()).build();
        }
    }

    public static void d(String msg) {
        if (FrameworkApi.isDebug) {
            androidLogger.d(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (FrameworkApi.isDebug) {
            androidLogger.d(tag, msg);
        }
    }

    public static void e(Throwable tr) {
        if (FrameworkApi.isDebug) {
            androidLogger.e("", tr);
        }
    }

    /**
     * 将对象输出为json
     *
     * @param object
     */
    public static void d(Object object) {
        if (FrameworkApi.isDebug) {
            if (object != null) {
                androidLogger.json(new GsonBuilder().serializeNulls().create().toJson(object));
            }
        }
    }

    public static void d(String format, Object... param) {
        if (FrameworkApi.isDebug) {
            androidLogger.d(format, param);
        }
    }

    /**
     * 打印json字符串
     * 必须是json字符串否则，输出为null
     *
     * @param json
     */
    public static void json(String json) {
        if (FrameworkApi.isDebug) {
            androidLogger.json(json);
        }
    }

    public static void json(Object[] array) {
        if (FrameworkApi.isDebug) {
            androidLogger.d(array);
        }
    }

}