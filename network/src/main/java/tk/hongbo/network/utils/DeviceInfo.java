package tk.hongbo.network.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import tk.hongbo.network.Net;

public class DeviceInfo {

    private static String net; // 手机上网方式

    /**
     * 联网方式
     */
    public static String getNet(String currentNetwork) {
        String operatorName = "unknown";
        if (net == null) {
            TelephonyManager tm = (TelephonyManager) Net.getIns().getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (DeviceInfo.isWifi(currentNetwork)) {
                    operatorName = tm.getNetworkOperatorName();
                } else {
                    operatorName = tm.getSimOperatorName();
                }
            }
            return operatorName;
        }
        return net;
    }

    public static boolean isWifi(String networkType) {
        return TextUtils.equals(networkType, NetworkEnum.TYPE_WIFI.getTypeStr());
    }

    public static String getNetWorkTypeStr() {
        return getNetWorkType(Net.getIns().getApplication()).getTypeStr();
    }

    public static NetworkEnum getNetWorkType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return NetworkEnum.TYPE_WIFI;
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkEnum.TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetworkEnum.TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkEnum.TYPE_4G;
        }
        return NetworkEnum.TYPE_NONE;
    }

}
