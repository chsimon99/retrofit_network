package tk.hongbo.network.data;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class OssTokenBean implements Serializable {

    private String address;
    private Long validMinutes;
    private OssTokenParamBean param;
    private List<OssTokenKeyBean> keys;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getValidMinutes() {
        return validMinutes;
    }

    public void setValidMinutes(Long validMinutes) {
        this.validMinutes = validMinutes;
    }

    public OssTokenParamBean getOssTokenParamBean() {
        return param;
    }

    public void setOssTokenParamBean(OssTokenParamBean param) {
        this.param = param;
    }

    public List<OssTokenKeyBean> getKeys() {
        return keys;
    }

    public void setKeys(List<OssTokenKeyBean> keys) {
        this.keys = keys;
    }
}
