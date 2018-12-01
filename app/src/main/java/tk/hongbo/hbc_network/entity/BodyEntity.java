package tk.hongbo.hbc_network.entity;

public class BodyEntity {

    private String eventId;
    private BodyInfoEntity data;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public BodyInfoEntity getData() {
        return data;
    }

    public void setData(BodyInfoEntity data) {
        this.data = data;
    }
}
