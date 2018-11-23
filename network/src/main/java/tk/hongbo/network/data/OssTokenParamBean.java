package tk.hongbo.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OssTokenParamBean implements Serializable {

    @SerializedName("OSSAccessKeyId")
    private String ossAccessKeyId;
    @SerializedName("Signature")
    private String signature;
    private String policy;

    public String getOssAccessKeyId() {
        return ossAccessKeyId;
    }

    public void setOssAccessKeyId(String ossAccessKeyId) {
        this.ossAccessKeyId = ossAccessKeyId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

}
