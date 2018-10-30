package inter.baisong.chat.bean;

import java.util.List;

/**
 * Created by 于德海 on 2018/1/11.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class CahtGroupListBean {
    private int status;
    private List<ChatGroupInfo> data;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ChatGroupInfo> getData() {
        return data;
    }

    public void setData(List<ChatGroupInfo> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public class ChatGroupInfo{
        private String groupid;
        private String groupname;
        private String groupface;
        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }

        public String getGroupface() {
            return groupface;
        }

        public void setGroupface(String groupface) {
            this.groupface = groupface;
        }
    }
}
