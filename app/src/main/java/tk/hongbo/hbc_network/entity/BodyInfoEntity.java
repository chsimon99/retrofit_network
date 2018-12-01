package tk.hongbo.hbc_network.entity;

public class BodyInfoEntity {
    private int captchaValid;
    private String role;
    private int userExist;
    private int valid;
    private String deviceId;
    private String eventName;
    private String ip;
    private String timestamp;
    private String tokenId;

    public int getCaptchaValid() {
        return captchaValid;
    }

    public void setCaptchaValid(int captchaValid) {
        this.captchaValid = captchaValid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserExist() {
        return userExist;
    }

    public void setUserExist(int userExist) {
        this.userExist = userExist;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
