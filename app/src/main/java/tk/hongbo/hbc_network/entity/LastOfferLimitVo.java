package tk.hongbo.hbc_network.entity;

/**
 * 次租查询提前预定期
 * Created by HONGBO on 2018/5/13 12:28.
 */
public class LastOfferLimitVo {
    
    private int lastOfferLimit; //提前预定期（分钟）
    private String lastOfferLimitTip; //提前预定期提示语

    public int getLastOfferLimit() {
        return lastOfferLimit;
    }

    public void setLastOfferLimit(int lastOfferLimit) {
        this.lastOfferLimit = lastOfferLimit;
    }

    public String getLastOfferLimitTip() {
        return lastOfferLimitTip;
    }

    public void setLastOfferLimitTip(String lastOfferLimitTip) {
        this.lastOfferLimitTip = lastOfferLimitTip;
    }
}
