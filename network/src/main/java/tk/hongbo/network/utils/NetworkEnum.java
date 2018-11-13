package tk.hongbo.network.utils;

public enum NetworkEnum {

    TYPE_NONE(-1, "null", 0),
    TYPE_2G(0, "2G", 1 << 0),
    TYPE_3G(1, "3G", 1 << 1),
    TYPE_4G(2, "4G", 1 << 2),
    TYPE_WIFI(3, "WIFI", 1 << 3),
    TYPE_ALL(4, "ALL", 0xFF);

    private int type;
    private String typeStr;
    private int section;

    NetworkEnum(int type, String typeStr, int section) {
        this.type = type;
        this.typeStr = typeStr;
        this.section = section;
    }

    public int getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public int getSection() {
        return section;
    }
}