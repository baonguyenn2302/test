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
public class MuonTra {
    private int maMuonTra;
    private DocGia docGia;
    private Sach sach;
    private Date ngayMuon;
    private Date ngayHenTra;
    private Date ngayTraThucTe;
    private String trangThai;
    private String loaiMuon;

    public void setLoaiMuon(String loaiMuon) {
        this.loaiMuon = loaiMuon;
    }

    public String getLoaiMuon() {
        return loaiMuon;
    }
    
    public MuonTra() {
    }

    public int getMaMuonTra() {
        return maMuonTra;
    }

    public void setMaMuonTra(int maMuonTra) {
        this.maMuonTra = maMuonTra;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }

    public Sach getSach() {
        return sach;
    }

    public void setSach(Sach sach) {
        this.sach = sach;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public Date getNgayHenTra() {
        return ngayHenTra;
    }

    public void setNgayHenTra(Date ngayHenTra) {
        this.ngayHenTra = ngayHenTra;
    }

    public Date getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(Date ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    public String getTrangThai() {
        if (trangThai == null || trangThai.isEmpty()) {
            if (ngayTraThucTe != null) {
                return "Đã trả";
            } else if (ngayHenTra != null && new Date().after(ngayHenTra)) {
                return "Quá hạn";
            } else {
                return "Đang mượn";
            }
        }
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "MuonTra{" +
                "maMuonTra=" + maMuonTra +
                ", docGia=" + (docGia != null ? docGia.getHoTen() : "N/A") +
                ", sach=" + (sach != null ? sach.getTenSach() : "N/A") +
                ", trangThai='" + getTrangThai() + '\'' +
                '}';
    }
}
