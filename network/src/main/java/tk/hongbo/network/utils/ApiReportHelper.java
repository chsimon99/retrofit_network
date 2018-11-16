package tk.hongbo.network.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import tk.hongbo.network.Net;
import tk.hongbo.network.bussiness.IRequestSend;
import tk.hongbo.network.net.NetCallback;

/**
 * Created by on 16/10/17.
 */
public class ApiReportHelper {

    private static final String PARAMS_TRACEID = "traceId";
    private static final String PARAMS_NETWORK = "network";
    private static final String PARAMS_LATENCY = "latency";
    private static final String PARAMS_CARRIERNAME = "carriername";

    private static final int REPORT_VALVE_COUNT = 20; //满20条上报一次
    private static final int DELAYED_MILLIS = 60 * 1000;// 1分钟上传一次，无论多少

    private static volatile ApiReportHelper instance;

    private JSONArray reportList;

    private volatile boolean isStop = false;

    private ApiReportHelper() {
        reportList = new JSONArray();

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(timeRunnable);
    }

    public static ApiReportHelper getInstance() {
        if (instance == null) {
            synchronized (ApiReportHelper.class) {
                instance = new ApiReportHelper();
            }
        }
        return instance;
    }

    public void addReport(Response response) {
        if (response == null || response.headers() == null) {
            return;
        }
        long spendTime = response.raw().receivedResponseAtMillis() - response.raw().sentRequestAtMillis();
        String traceId = response.headers().get(PARAMS_TRACEID);
        String currentNetwork = DeviceInfo.getNetWorkTypeStr();
        if (TextUtils.isEmpty(traceId) || spendTime <= 0) {
            return;
        }
        String operatorName = DeviceInfo.getNet(currentNetwork);
        JSONObject obj = new JSONObject();
        try {
            obj.put(PARAMS_TRACEID, traceId);           // 请求ID
            obj.put(PARAMS_NETWORK, currentNetwork);    // 网络类型
            obj.put(PARAMS_LATENCY, "" + spendTime);    // 客户端请求耗时，精确到毫秒
            obj.put(PARAMS_CARRIERNAME, operatorName);  // 网络运营商
        } catch (JSONException e) {
            Log.e("NetWork", "组合需要汇报信息json异常", e);
        }
        reportList.put(obj);

        if (reportList.length() >= REPORT_VALVE_COUNT) {
            requestReport(reportList.toString());
            reportList = new JSONArray();
        }
    }

    private Runnable timeRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                while (!isStop && mHandler != null) {
                    Thread.sleep(DELAYED_MILLIS);
                    mHandler.sendEmptyMessage(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (isStop) {
                        return;
                    }
                    commitAllReport();
                    break;
                default:
                    break;
            }
        }
    };

    public void commitAllReport() {
        if (reportList != null && reportList.length() > 0) {
            requestReport(reportList.toString());
            reportList = new JSONArray();
        }
    }

    public void abort() {
        isStop = true;
    }

    public void requestReport(String bodyStr) {
        if (TextUtils.isEmpty(bodyStr)) {
            return;
        }
        IRequestSend service = Net.getIns().getRetrofit().create(IRequestSend.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyStr);
        service.sendReporrt(requestBody).enqueue(new NetCallback<>(null));
    }
}
