package inter.baisong.chat.bean;

import java.util.List;

/**
 * Created by 于德海 on 2018/1/20.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatMoneyRobListBean {

    private int status;
    private String msg;
    private List<MoneyLog> data;

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

    public List<MoneyLog> getData() {
        return data;
    }

    public void setData(List<MoneyLog> data) {
        this.data = data;
    }

    public class MoneyLog{
//        private String id; 注释的都是不需要的字段 防止服务器智障  解析错误
//        private String userid;
        private String score;
//        private String detail;
//        private String type;
        private String time;
//        private String gid;
//        private String fromuid;
//        private String oid;
//        private String txid;

//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getUserid() {
//            return userid;
//        }
//
//        public void setUserid(String userid) {
//            this.userid = userid;
//        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

//        public String getDetail() {
//            return detail;
//        }
//
//        public void setDetail(String detail) {
//            this.detail = detail;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

//        public String getGid() {
//            return gid;
//        }
//
//        public void setGid(String gid) {
//            this.gid = gid;
//        }
//
//        public String getFromuid() {
//            return fromuid;
//        }
//
//        public void setFromuid(String fromuid) {
//            this.fromuid = fromuid;
//        }
//
//        public String getOid() {
//            return oid;
//        }
//
//        public void setOid(String oid) {
//            this.oid = oid;
//        }
//
//        public String getTxid() {
//            return txid;
//        }
//
//        public void setTxid(String txid) {
//            this.txid = txid;
//        }
    }
}
