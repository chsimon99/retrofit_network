package tk.hongbo.network.data;

import java.io.Serializable;

public class OssTokenKeyBean implements Serializable {

    private String path;
    private String key;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
