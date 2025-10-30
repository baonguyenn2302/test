/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


import java.util.Date;
/**
 *
 * @author baonguyenn
 */
public class DocGia {
    private String maDocGia;
    private String hoTen;
    private Date ngaySinh;
    private String email;
    private String diaChi;
    private String sdt;
    private boolean blocked; // Trạng thái bị khóa (true = bị khóa, false = hoạt động)

    public DocGia() {
        this.blocked = false;
    }

    public DocGia(String maDocGia, String hoTen, Date ngaySinh, String email, String diaChi, String sdt, boolean blocked) {
        this.maDocGia = maDocGia;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.blocked = blocked;
    }

    public String getMaDocGia() {
        return maDocGia;
    }

    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getTrangThai() {
        return this.blocked ? "Bị khóa" : "Hoạt động";
    }

    @Override
    public String toString() {
        return "DocGia{" +
                "maDocGia='" + maDocGia + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", email='" + email + '\'' +
                ", blocked=" + blocked +
                '}';
    }
}
