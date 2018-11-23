package tk.hongbo.network.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * 日志打印
 * 支持大量数据不中断
 */

public class Log {

    private static String LOG_TAG = "NET";
    private static boolean ISDEBUG = true; //默认打开日志
    private static boolean IS_SHOW_THREAD = false; //默认不显示线程信息
    private static int METHOD_COUNT = 0; //方法显示层级
    private static int METHOD_OFFSET = 2; //隐藏内部方法调用

    static {
        init();
    }

    private static void init() {
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(IS_SHOW_THREAD)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(METHOD_COUNT)         // (Optional) How many method line to show. Default 2
                .methodOffset(METHOD_OFFSET)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(LOG_TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    /**
     * 打印调试信息
     *
     * @param msg
     */
    public static void d(String msg) {
        if (ISDEBUG) {
            Logger.d(msg);
        }
    }

    /**
     * 打印信息
     *
     * @param msg
     */
    public static void i(String msg) {
        if (ISDEBUG) {
            Logger.i(msg);
        }
    }

    /**
     * 打印错误
     *
     * @param msg
     */
    public static void e(String msg) {
        if (ISDEBUG) {
            Logger.e(msg);
        }
    }

    /**
     * 支持Throwable打印
     *
     * @param msg
     * @param throwable
     */
    public static void e(String msg, Throwable throwable) {
        if (ISDEBUG) {
            Logger.e(throwable, msg);
        }
    }

    /**
     * 打印json
     *
     * @param msg
     */
    public static void json(String msg) {
        if (ISDEBUG) {
            Logger.json(msg);
        }
    }

    /**
     * 打印xml
     *
     * @param msg
     */
    public static void xml(String msg) {
        if (ISDEBUG) {
            Logger.xml(msg);
        }
    }

    /**
     * 设置调试模式
     * 只有为true才会打印日志
     *
     * @param isDebug
     */
    public static void setIsDebug(boolean isDebug) {
        Log.ISDEBUG = isDebug;
    }

    /**
     * 设置日志TAG标记
     *
     * @param logTag
     */
    public static void setLogTag(String logTag) {
        Log.LOG_TAG = logTag;
        init();
    }

    public static void setIsShowThread(boolean isShowThread) {
        IS_SHOW_THREAD = isShowThread;
        init();
    }

    /**
     * 设置方法层数
     *
     * @param methodCount
     */
    public static void setMethodCount(int methodCount) {
        Log.METHOD_COUNT = methodCount;
        init();
    }

    public static void setMethodOffset(int methodOffset) {
        METHOD_OFFSET = methodOffset;
        init();
    }
}
