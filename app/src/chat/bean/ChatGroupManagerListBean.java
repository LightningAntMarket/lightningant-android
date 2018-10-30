package inter.baisong.chat.bean;

import java.util.List;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupManagerListBean {
    private int status;
    private String msg;
    private List<User> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }

    public class  User{
        private String nickname;
        private String face;
        private String member;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public String getUid() {
            return member;
        }

        public void setUid(String uid) {
            this.member = uid;
        }
    }
}
