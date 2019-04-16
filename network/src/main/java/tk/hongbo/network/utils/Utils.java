package tk.hongbo.network.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import tk.hongbo.network.ContentType;
import tk.hongbo.network.RetrofitTools;
import tk.hongbo.network.callback.ResponseCallback;
import tk.hongbo.network.request.RetrofitRequestBody;

public class Utils {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data;";
    public static final String MULTIPART_IMAGE_DATA = "image/*; charset=utf-8";
    public static final String MULTIPART_JSON_DATA = "application/json; charset=utf-8";
    public static final String MULTIPART_VIDEO_DATA = "video/*";
    public static final String MULTIPART_AUDIO_DATA = "audio/*";
    public static final String MULTIPART_TEXT_DATA = "text/plain";
    public static final String MULTIPART_APK_DATA = "application/vnd.android.package-archive";
    public static final String MULTIPART_JAVA_DATA = "java/*";
    public static final String MULTIPART_MESSAGE_DATA = "message/rfc822";

    private static String FILE_SUFFIX = ".tmpl";
    public static final String DEFAULT_FILENAME = "retrofitdownfile" + FILE_SUFFIX;
    private static final String DEFAULT_FILETAG = "DownLoads";

    public static int checkDuration(String name, long duration, TimeUnit unit) {
        if (duration < 0) throw new IllegalArgumentException(name + " < 0");
        if (unit == null) throw new NullPointerException("unit == null");
        long millis = unit.toMillis(duration);
        if (millis > Integer.MAX_VALUE) throw new IllegalArgumentException(name + " too large.");
        if (millis == 0 && duration > 0) throw new IllegalArgumentException(name + " too small.");
        return (int) millis;
    }

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
    public static boolean checkMain() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * createRequestBody
     * @param file file
     * @param type  see {@link ContentType}
     * @return NovateRequestBody
     */
    @NonNull
    public static RetrofitRequestBody createRequestBody(@NonNull File file, @NonNull ContentType type, ResponseCallback callback) {
        return new RetrofitRequestBody(createBody(file, type), callback);
    }

    /**
     * @param file file
     * @param type see {@link ContentType}
     * @return
     */
    @NonNull
    public static RequestBody createBody(File file, ContentType type) {
        checkNotNull(file, "file not be null!");
        checkNotNull(file, "type not be null!");
        return createBody(file, typeToString(type));
    }

    @NonNull
    public static RequestBody createBody(File file, String mediaType) {
        checkNotNull(file, "file not null!");
        if (TextUtils.isEmpty(mediaType)) {
            throw new NullPointerException("contentType not be null");
        }
        return RequestBody.create(okhttp3.MediaType.parse(mediaType), file);
    }

    /**
     * ContentType To String
     * @param type see {@link ContentType}
     * @return String mediaType
     */
    @NonNull
    public static String typeToString(@NonNull ContentType type) {
        switch (type) {
            case APK:
                return MULTIPART_APK_DATA;
            case VIDEO:
                return MULTIPART_VIDEO_DATA;
            case AUDIO:
                return MULTIPART_AUDIO_DATA;
            case JAVA:
                return MULTIPART_JAVA_DATA;
            case IMAGE:
                return MULTIPART_IMAGE_DATA;
            case TEXT:
                return MULTIPART_TEXT_DATA;
            case JSON:
                return MULTIPART_JSON_DATA;
            case FORM:
                return MULTIPART_FORM_DATA;
            case MESSAGE:
                return MULTIPART_MESSAGE_DATA;
            default:
                return MULTIPART_IMAGE_DATA;
        }
    }

    @NonNull
    public static String typeToStringName(@NonNull ContentType type){
        switch (type) {
            case APK:
                return "apk";
            case VIDEO:
                return "video";
            case AUDIO:
                return "audio";
            case JAVA:
                return "java";
            case IMAGE:
                return "image";
            case TEXT:
                return "text";
            case JSON:
                return "json";
            case FORM:
                return "from";
            case MESSAGE:
                return "massage";
            default:
                return "image";
        }
    }

    /**
     * 新建下载文件
     *
     * @param savePath
     * @return
     */
    public static File createDownloadFile(String savePath, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            fileName = DEFAULT_FILENAME;
        }

        if (TextUtils.isEmpty(savePath)) {
            savePath = Utils.getDownLoadPath(RetrofitTools.getmContext());
        }

        return createFile(savePath, fileName);
    }

    /**
     * 新建下载文件
     *
     * @param savePath
     * @return
     */
    public static File createFile(String savePath, String fileName) {
        if (TextUtils.isEmpty(savePath)) {
            throw new RuntimeException("you should define downloadFolder path!");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new RuntimeException("you should define downloadFileName !");
        }
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(savePath, fileName);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static String getDownLoadPath(Context context) {
        if (null == getSDPath()) {
            File filepath = new File(context.getExternalFilesDir(null) + File.separator + DEFAULT_FILETAG);
            if (!filepath.exists()){
                filepath.mkdirs();
            }
            return context.getExternalFilesDir(null) + File.separator + DEFAULT_FILETAG + File.separator;
        }
        return getBasePath(context) + File.separator + DEFAULT_FILETAG + File.separator;
    }

    public static String getBasePath(Context context) {
        if (null == getSDPath()) {
            return context.getCacheDir().getAbsolutePath();
        }
        File file = new File(getSDPath() + File.separator + "download");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return null;
        }
    }
}
