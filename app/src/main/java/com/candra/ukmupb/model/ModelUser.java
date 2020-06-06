package com.candra.ukmupb.model;

public class ModelUser {

    private String namalengkap, email, nim, anggotaukm, image, cover, Uid, onlineStatus, typingTo;

    public ModelUser() {

    }

    public ModelUser(String namalengkap, String email, String nim, String anggotaukm, String image, String cover, String Uid, String onlineStatus, String typingTo) {
        this.namalengkap = namalengkap;
        this.email = email;
        this.nim = nim;
        this.anggotaukm = anggotaukm;
        this.image = image;
        this.cover = cover;
        this.Uid = Uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
    }

    public String getNamalengkap() {
        return namalengkap;
    }

    public void setNamalengkap(String namalengkap) {
        this.namalengkap = namalengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getAnggotaukm() {
        return anggotaukm;
    }

    public void setAnggotaukm(String anggotaukm) {
        this.anggotaukm = anggotaukm;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }
}
