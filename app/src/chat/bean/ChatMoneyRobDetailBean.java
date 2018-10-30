package inter.baisong.chat.bean;

import java.util.List;

/**
 * Created by 于德海 on 2018/1/19.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatMoneyRobDetailBean {
    private int status;
    private String msg;
    private Info info;
    private List<Data> log;

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

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Data> getLog() {
        return log;
    }

    public void setLog(List<Data> log) {
        this.log = log;
    }

    public class Info{
        private String totalnumber;
        private String getnumber;
        private String lap;
        private String name;
        private String face;
        private String nickname;
        private String mygetlap;
        private int hasget;

        public String getMygetlap() {
            return mygetlap;
        }

        public void setMygetlap(String mygetlap) {
            this.mygetlap = mygetlap;
        }

        public String getTotalnumber() {
            return totalnumber;
        }

        public void setTotalnumber(String totalnumber) {
            this.totalnumber = totalnumber;
        }

        public String getGetnumber() {
            return getnumber;
        }

        public void setGetnumber(String getnumber) {
            this.getnumber = getnumber;
        }

        public String getLap() {
            return lap;
        }

        public void setLap(String lap) {
            this.lap = lap;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getHasget() {
            return hasget;
        }

        public void setHasget(int hasget) {
            this.hasget = hasget;
        }
    }

    public class Data{
        private String time;
        private String lap;
        private String nickname;
        private String face;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLap() {
            return lap;
        }

        public void setLap(String lap) {
            this.lap = lap;
        }

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
    }

}
