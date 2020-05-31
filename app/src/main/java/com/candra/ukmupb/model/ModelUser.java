package com.candra.ukmupb.model;

public class ModelUser {

    private String namalengkap, email, nim, anggotaukm, image, cover, Uid;

    public ModelUser() {

    }

    public ModelUser(String namalengkap, String email, String nim, String anggotaukm, String image, String cover, String Uid) {
        this.namalengkap = namalengkap;
        this.email = email;
        this.nim = nim;
        this.anggotaukm = anggotaukm;
        this.image = image;
        this.cover = cover;
        this.Uid = Uid;
    }

    public String getNamaLengkap() {
        return namalengkap;
    }

    public void setNamaLengkap(String namalengkap) {
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

    public void setUid(String Uid) {
        this.Uid = Uid;
    }
}
