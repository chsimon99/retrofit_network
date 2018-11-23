package tk.hongbo.network.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tk.hongbo.network.Net;
import tk.hongbo.network.NetHelper;
import tk.hongbo.network.bussiness.IRequestSend;
import tk.hongbo.network.data.OssTokenBean;
import tk.hongbo.network.data.OssTokenKeyBean;
import tk.hongbo.network.net.NetListener;
import tk.hongbo.network.utils.LogHelper;

/**
 * 上传文件取号器
 * Created by ZHZEPHI on 2016-12-21.
 */
public class TakeNumHelper {

    private OssTokenBean ossTokenBean; //当前上传环境参数
    private List<OssTokenKeyBean> keysList = Collections.synchronizedList(new LinkedList<>());
    private long getTime = 0; //记录最后一次获取key的时间
    private int current = 0; //重试获取key次数

    private volatile static TakeNumHelper instance;

    private TakeNumHelper() {
    }

    public static TakeNumHelper getInstance() {
        if (instance == null) {
            synchronized (TakeNumHelper.class) {
                if (instance == null) {
                    instance = new TakeNumHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 上次取号是否过期
     *
     * @return
     */
    private boolean isValidate() {
        long haveTime = System.currentTimeMillis() - getTime; //现在剩余的时间
        if (ossTokenBean != null) {
            double sumTime = ossTokenBean.getValidMinutes() * 60 * 1000 * 0.7;
            LogHelper.d("上传阿里云取号，已过Time:" + haveTime + "，剩余Time:" + sumTime);
            return haveTime < sumTime;
        }
        return false;
    }

    /*
    1.检查key是否过期（剩余有效期的70%）
    2.检查剩余是否有key
    3.有可用的key则直接使用
     */
    public void getKey(@NonNull KeyBackListener keyBackListener) {
        current = 0;
        if (keyBackListener != null) {
            if (!isValidate() || isHaveKey()) {
                getUploadToken(keyBackListener);
            } else {
                current = 0;
                keyBackListener.onKeyBack(ossTokenBean, keysList.get(0));
                keysList.remove(0);
            }
        }
    }

    private boolean isHaveKey() {
        return keysList.isEmpty();
    }

    /**
     * 获取上传文件所需要环境
     */
    private void getUploadToken(final KeyBackListener keyBackListener) {
        if (current < 2) {
            //只允许重新获取一次
            IRequestSend service = Net.getIns().getRetrofit().create(IRequestSend.class);
            NetHelper.getIns().request(service.getOssToken(), new NetListener<OssTokenBean>() {
                @Override
                public void onSuccess(OssTokenBean ossTokenEntity) {
                    if (ossTokenEntity != null) {
                        getTime = System.currentTimeMillis();
                        ossTokenBean = ossTokenEntity;
                        keysList.clear();
                        keysList.addAll(ossTokenBean.getKeys());
                        getKey(keyBackListener);
                    }
                }

                @Override
                public void onFailure(int status, String message, Throwable t) {
                    if (!TextUtils.isEmpty(message)) {
                        keyBackListener.onFail(message);
                    }
                }
            });
        }
    }

    public interface KeyBackListener {
        void onKeyBack(OssTokenBean ossTokenBean, OssTokenKeyBean ossTokenKeyBean);

        void onFail(String message);
    }
}
