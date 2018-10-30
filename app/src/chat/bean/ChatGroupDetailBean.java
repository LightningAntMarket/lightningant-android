package inter.baisong.chat.bean;

import java.util.List;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupDetailBean {
    private int status;
    private String msg;
    private Data data;
    private List<User> admin;

    public List<User> getAdmin() {
        return admin;
    }

    public void setAdmin(List<User> admin) {
        this.admin = admin;
    }

    public class User{
        private String nickname;
        private String face;
        private String member;

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        private String id;
        private String name;
        private String description;
        private String owner;
        private long created;
        private String maxusers;
        private String affiliations_count;
        private String groupface;
        private String notice;
        private String noticetime;
        private String desctime;

        public String getNoticetime() {
            return noticetime;
        }

        public void setNoticetime(String noticetime) {
            this.noticetime = noticetime;
        }

        public String getDesctime() {
            return desctime;
        }

        public void setDesctime(String desctime) {
            this.desctime = desctime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public String getMaxusers() {
            return maxusers;
        }

        public void setMaxusers(String maxusers) {
            this.maxusers = maxusers;
        }

        public String getAffiliations_count() {
            return affiliations_count;
        }

        public void setAffiliations_count(String affiliations_count) {
            this.affiliations_count = affiliations_count;
        }

        public String getGroupface() {
            return groupface;
        }

        public void setGroupface(String groupface) {
            this.groupface = groupface;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }
    }
}
