package tk.hongbo.network.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imxu on 2018/7/5.
 */

public class RetrofitBeanHome implements Serializable {

    public int status;
    public BeanData data;

    public class BeanData implements Serializable{
        public int totalAmount;//总资产
        public int useableAmount;//可以提现金额，已经结算金额
        public int onTheWayAmount;//在途资金,不可提现
        public int accountStatus;//收款账户状态是否有异常：0，异常；1，正常
        public int withdrawStatus;//提现状态：0，禁止提现；1，正常
        public String forbitEnddate;//禁止提现截止日期
        public String forbitReason;//禁止提现原因
        public List<SUDOKUS> sudokus;

       public class SUDOKUS implements Serializable{
            public int flag;
            public String iconUrl;
            public int index;
            public String title;
            public int type;
            public String destUrl;

        }
    }

}
