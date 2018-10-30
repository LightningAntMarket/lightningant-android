package inter.baisong.chat.bean;

/**
 * Created by 于德海 on 2018/1/14.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class HxGroupBean {
    private String group_name;
    private String groupid;
    private int istop;
    private int isMute;
    private String img_url;

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public int getIstop() {
        return istop;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }

    public int getIsMute() {
        return isMute;
    }

    public void setIsMute(int isMute) {
        this.isMute = isMute;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
