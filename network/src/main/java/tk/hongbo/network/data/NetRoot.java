package tk.hongbo.network.data;

import java.io.Serializable;

public class NetRoot<M> implements Serializable {

    private int status;
    private String message;
    private M data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }
}
