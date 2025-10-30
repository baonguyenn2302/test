/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.MuonTra;
import model.Sach;
import model.DocGia;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *
 * @author baonguyenn
 */
public class MuonTraDAO {
    private SachDAO sachDAO;
    private DocGiaDAO docGiaDAO;

    public MuonTraDAO() {
        this.sachDAO = new SachDAO();
        this.docGiaDAO = new DocGiaDAO();
    }

    private MuonTra mapRowToMuonTra(ResultSet rs) throws SQLException {
        MuonTra mt = new MuonTra();
        mt.setMaMuonTra(rs.getInt("maMuonTra"));

        // Lấy thông tin Độc Giả
        String maDocGia = rs.getString("maDocGia");
        DocGia dg = docGiaDAO.getDocGiaByMaDocGia(maDocGia);
        if (dg == null) dg = new DocGia(maDocGia, "Không tìm thấy", null, null, null, null, false);
        mt.setDocGia(dg);

        // Lấy thông tin Sách
        String maSach = rs.getString("maSach");
        Sach sach = sachDAO.getSachByMaSach(maSach);
        if (sach == null) sach = new Sach(maSach, "Không tìm thấy", null, 0, 0, null, null, null, null, null);
        mt.setSach(sach);

        // Lấy các trường ngày
        Timestamp tsMuon = rs.getTimestamp("ngayMuon");
        Timestamp tsHenTra = rs.getTimestamp("ngayHenTra");
        Timestamp tsTraThucTe = rs.getTimestamp("ngayTraThucTe");
        if (tsMuon != null) mt.setNgayMuon(new Date(tsMuon.getTime()));
        if (tsHenTra != null) mt.setNgayHenTra(new Date(tsHenTra.getTime()));
        if (tsTraThucTe != null) mt.setNgayTraThucTe(new Date(tsTraThucTe.getTime()));

        mt.setTrangThai(rs.getString("trangThai"));

        return mt;
    }

    // Lấy danh sách phiếu mượn, có thể lọc theo trạng thái
    public List<MuonTra> getDanhSachMuon(String filterTrangThai) {
        List<MuonTra> ds = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT mt.maMuonTra, mt.maDocGia, mt.maSach, mt.ngayMuon, mt.ngayHenTra, mt.ngayTraThucTe, mt.trangThai FROM MUONTRA mt "
        );

        if (filterTrangThai != null && !filterTrangThai.equalsIgnoreCase("Tất cả")) {
            if (filterTrangThai.equals("Quá hạn")) {
                sql.append("WHERE mt.ngayTraThucTe IS NULL AND NOW() > mt.ngayHenTra ");
            } else if (filterTrangThai.equals("Đang mượn")) {
                sql.append("WHERE mt.ngayTraThucTe IS NULL AND (NOW() <= mt.ngayHenTra OR mt.ngayHenTra IS NULL) ");
            } else if (filterTrangThai.equals("Đã trả")) {
                sql.append("WHERE mt.ngayTraThucTe IS NOT NULL ");
            }
        }

        sql.append("ORDER BY mt.ngayMuon DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString());
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ds.add(mapRowToMuonTra(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Cho mượn sách (transaction)
    public boolean muonSach(MuonTra muonTra) throws SQLException {
        String sqlInsertMuon = "INSERT INTO MUONTRA(maDocGia, maSach, ngayMuon, ngayHenTra, trangThai, loaiMuon) VALUES (?, ?, NOW(), ?, ?, ?)";
        String sqlUpdateSach = "UPDATE SACH SET soLuong = soLuong - 1 WHERE maSach = ?";
        String sqlCheckSach = "SELECT soLuong FROM SACH WHERE maSach = ?";
        String sqlCheckDocGia = "SELECT blocked FROM DOCGIA WHERE maDocGia = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Kiểm tra độc giả
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckDocGia)) {
                pstmt.setString(1, muonTra.getDocGia().getMaDocGia());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getBoolean("blocked")) {
                        throw new IllegalStateException("Độc giả đang bị khóa, không thể mượn sách.");
                    }
                }
            }

            // 2. Kiểm tra sách
            int soLuong = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckSach)) {
                pstmt.setString(1, muonTra.getSach().getMaSach());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) soLuong = rs.getInt("soLuong");
                }
            }
            if (soLuong <= 0) throw new IllegalStateException("Sách đã hết, không thể mượn.");

            // 3. Thêm phiếu mượn
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertMuon)) {
                pstmt.setString(1, muonTra.getDocGia().getMaDocGia());
                pstmt.setString(2, muonTra.getSach().getMaSach());
                pstmt.setTimestamp(3, new Timestamp(muonTra.getNgayHenTra().getTime()));
                pstmt.setString(4, "Đang mượn");
                pstmt.setString(5, muonTra.getLoaiMuon());
                pstmt.executeUpdate();
            }

            // 4. Giảm số lượng sách
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateSach)) {
                pstmt.setString(1, muonTra.getSach().getMaSach());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Trả sách
    public boolean traSach(int maMuonTra) throws SQLException {
        String sqlGetMaSach = "SELECT maSach FROM MUONTRA WHERE maMuonTra = ?";
        String sqlUpdateMuon = "UPDATE MUONTRA SET ngayTraThucTe = NOW(), trangThai = 'Đã trả' WHERE maMuonTra = ?";
        String sqlUpdateSach = "UPDATE SACH SET soLuong = soLuong + 1 WHERE maSach = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String maSach = null;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlGetMaSach)) {
                pstmt.setInt(1, maMuonTra);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) maSach = rs.getString("maSach");
                }
            }

            if (maSach == null) throw new SQLException("Không tìm thấy phiếu mượn.");

            // Cập nhật trạng thái
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateMuon)) {
                pstmt.setInt(1, maMuonTra);
                pstmt.executeUpdate();
            }

            // Cập nhật số lượng sách
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateSach)) {
                pstmt.setString(1, maSach);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Gia hạn
    public boolean giaHan(int maMuonTra, Date ngayHenTraMoi) {
        String sql = "UPDATE MUONTRA SET ngayHenTra = ? WHERE maMuonTra = ? AND ngayTraThucTe IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, new Timestamp(ngayHenTraMoi.getTime()));
            pstmt.setInt(2, maMuonTra);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}