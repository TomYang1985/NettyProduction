package com.netty.client.utils;

import android.text.TextUtils;
import android.util.Log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.google.gson.GsonBuilder;
import com.netty.client.Config;


//Logcat管理类
public class L {
    private static Logger androidLogger;
    private static Logger fileLogger;

    static {
        if (Config.isDebug) {
            LogConfiguration config = new LogConfiguration.Builder()
                    .tag(Config.TAG)                   // Specify TAG, default: "X-LOG"
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

            XLog.init(config);

            androidLogger = new Logger.Builder().printers(new AndroidPrinter()).build();

            Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                    .Builder(Config.getLogPath())       // Specify the path to save log file
                    .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                    // .backupStrategy(new MyBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                    .logFlattener(new ClassicFlattener())                  // Default: DefaultFlattener
                    .setFileLogRetentionPeriod(Config.LOG_RETENTION_PERIOD)
                    .build();
            fileLogger = new Logger.Builder().printers(filePrinter).build();
        }
    }

    public static void d(String msg) {
        if (Config.isDebug) {
            androidLogger.d(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Config.isDebug) {
            androidLogger.d(tag, msg);
        }
    }

    public static void e(Throwable tr) {
        if (Config.isDebug) {
            androidLogger.e("", tr);
        }
    }

    /**
     * 将对象输出为json
     *
     * @param object
     */
    public static void d(Object object) {
        if (Config.isDebug) {
            if (object != null) {
                androidLogger.json(new GsonBuilder().serializeNulls().create().toJson(object));
            }
        }
    }

    public static void d(String format, Object... param) {
        if (Config.isDebug) {
            androidLogger.d(format, param);
        }
    }

    public static void print(String msg) {
        if (Config.isDebug) {
            Log.d(Config.TAG, msg);
        }
    }

    /**
     * 打印json字符串
     * 必须是json字符串否则，输出为null
     *
     * @param json
     */
    public static void json(String json) {
        if (Config.isDebug) {
            androidLogger.json(json);
        }
    }

    public static void json(Object[] array) {
        if (Config.isDebug) {
            androidLogger.d(array);
        }
    }

    public static void writeFile(String msg) {
        if (Config.isDebug && !TextUtils.isEmpty(msg)) {
            fileLogger.d(msg);
        }
    }

    public static void writeFileJson(String json) {
        if (Config.isDebug && !TextUtils.isEmpty(json)) {
            fileLogger.json(json);
        }
    }

    public static void writeFile(Object object) {
        if (Config.isDebug && object != null) {
            fileLogger.json(new GsonBuilder().serializeNulls().create().toJson(object));
        }
    }

    public static void writeFile(Object[] array) {
        if (Config.isDebug && array != null) {
            fileLogger.d(array);
        }
    }

    public static void writeFile(Throwable tr) {
        if (Config.isDebug) {
            fileLogger.d("", tr);
        }
    }

}