package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName User
 * @Description 实体类User
 * @Author 索半斤
 * @Date 2021/1/7 14:15
 * @Version 1.0
 */
public class User implements Serializable {
    private int id; //用户id
    private String account;  //账号
    private String username;  //昵称
    private String password;  //密码
    private int gender;       //性别
    private Date birthday;    //生日
    private String signature;  //签名
    private String profession;  //职业
    private String location;   //当前所在地
    private String homeland;   //家乡
    private String phoneNumber;  //手机号
    private String email;        //邮箱
    private String headerPicture;  //头像
    private List<Message> messages;  //消息列表
    private List<FriendGroup> friendGroups; //用户的好友分组
    private int status;             //在线状态

    private List<GroupGroup> groupGroupList;  //用户的群分组

    private List<FriendRequestRecord> friendRequestRecords; //用户的好友请求记录

    private List<ChatRecord> chatRecords;     //用户的聊天记录(离线消息)

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", signature='" + signature + '\'' +
                ", profession='" + profession + '\'' +
                ", location='" + location + '\'' +
                ", homeland='" + homeland + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", headerPicture='" + headerPicture + '\'' +
                ", messages=" + messages +
                ", friendGroups=" + friendGroups +
                ", status=" + status +
                ", groupGroupList=" + groupGroupList +
                ", friendRequestRecords=" + friendRequestRecords +
                ", chatRecords=" + chatRecords +
                '}';
    }

    public List<ChatRecord> getChatRecords() {
        return chatRecords;
    }

    public void setChatRecords(List<ChatRecord> chatRecords) {
        this.chatRecords = chatRecords;
    }

    public List<FriendRequestRecord> getFriendRequestRecords() {
        return friendRequestRecords;
    }

    public void setFriendRequestRecords(List<FriendRequestRecord> friendRequestRecords) {
        this.friendRequestRecords = friendRequestRecords;
    }

    public List<GroupGroup> getGroupGroupList() {
        return groupGroupList;
    }

    public void setGroupGroupList(List<GroupGroup> groupGroupList) {
        this.groupGroupList = groupGroupList;
    }

    public List<FriendGroup> getFriendGroups() {
        return friendGroups;
    }

    public void setFriendGroups(List<FriendGroup> friendGroups) {
        this.friendGroups = friendGroups;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHomeland() {
        return homeland;
    }

    public void setHomeland(String homeland) {
        this.homeland = homeland;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeaderPicture() {
        return headerPicture;
    }

    public void setHeaderPicture(String headerPicture) {
        this.headerPicture = headerPicture;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}
