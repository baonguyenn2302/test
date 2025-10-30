/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Sach;
import model.TacGia;
import util.DatabaseConnection;
import java.sql.*; // Giữ nguyên import SQL
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date; // SỬA: Import cụ thể java.util.Date để tránh lỗi Ambiguous

/**
 *
 * @author baonguyenn
 */
public class SachDAO {

    // Lớp AuthorInfo dùng cho tab "Duyệt Tác Giả"
    public static class AuthorInfo {
        public String authorName;
        public int bookCount;
        public AuthorInfo(String authorName, int bookCount) {
            this.authorName = authorName;
            this.bookCount = bookCount;
        }
    }

    //  Ánh xạ 1 hàng ResultSet thành đối tượng Sách
    private Sach mapRowToSach(ResultSet rs) throws SQLException {
        Sach s = new Sach();
        s.setMaSach(rs.getString("maSach"));
        s.setTenSach(rs.getString("tenSach"));
        s.setNhaXuatBan(rs.getString("nhaXuatBan"));
        s.setNamXuatBan(rs.getInt("namXuatBan"));
        s.setSoLuong(rs.getInt("soLuong"));
        s.setMoTa(rs.getString("moTa"));
        s.setDuongDanAnh(rs.getString("duongDanAnh"));
        s.setViTri(rs.getString("viTri"));
        Timestamp ts = rs.getTimestamp("ngayThem");
        if (ts != null) s.setNgayThem(new Date(ts.getTime()));

        s.setDanhSachTacGia(new ArrayList<>());
        return s;
    }

    // Helper dùng chung: chạy SQL và gom nhóm tác giả theo sách (many-to-many)
    protected List<Sach> getSachInternal(String sql, Object... params) {
        Map<String, Sach> sachMap = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String maSach = rs.getString("maSach");
                    Sach sach = sachMap.get(maSach);

                    if (sach == null) {
                        sach = mapRowToSach(rs);
                        sachMap.put(maSach, sach);
                    }

                    // Nếu có thông tin tác giả từ JOIN thì thêm
                    String maTacGia = rs.getString("maTacGia_join");
                    if (maTacGia != null) {
                        TacGia tg = new TacGia();
                        tg.setMaTacGia(maTacGia);
                        tg.setTenTacGia(rs.getString("tenTacGia_join"));
                        sach.getDanhSachTacGia().add(tg);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(sachMap.values());
    }

    //  Lấy tất cả sách (còn hoạt động)
    public List<Sach> getAllSach() {
        String sql = "SELECT s.*, t.maTacGia AS maTacGia_join, t.tenTacGia AS tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "WHERE s.isArchived = 0 " +
                     "ORDER BY s.tenSach, t.tenTacGia";
        return getSachInternal(sql);
    }

    // Lấy sách theo mã
    public Sach getSachByMaSach(String maSach) {
        String sql = "SELECT s.*, t.maTacGia AS maTacGia_join, t.tenTacGia AS tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "WHERE s.maSach = ? AND s.isArchived = 0 " +
                     "ORDER BY t.tenTacGia";
        List<Sach> results = getSachInternal(sql, maSach);
        return results.isEmpty() ? null : results.get(0);
    }

    // Lấy sách theo tác giả
    public List<Sach> getSachByTacGia(String maTacGia) {
        String sql = "SELECT s.*, t.maTacGia AS maTacGia_join, t.tenTacGia AS tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "WHERE s.maSach IN (SELECT maSach FROM SACH_TACGIA WHERE maTacGia = ?) " +
                     "AND s.isArchived = 0 " +
                     "ORDER BY s.tenSach, t.tenTacGia";
        return getSachInternal(sql, maTacGia);
    }

    // Thêm sách mới (transaction)
    public boolean themSach(Sach sach) {
        // CẢI THIỆN: Thay NOW() (MySQL) bằng GETDATE() (SQL Server)
        String sqlSach = "INSERT INTO SACH(maSach, tenSach, nhaXuatBan, namXuatBan, soLuong, moTa, duongDanAnh, viTri, ngayThem, isArchived) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 0)"; 
        String sqlSachTacGia = "INSERT INTO SACH_TACGIA(maSach, maTacGia) VALUES(?, ?)";

        Connection conn = null; // Khai báo conn bên ngoài try-with-resources để dùng trong catch
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm Sách
            try (PreparedStatement pstmtSach = conn.prepareStatement(sqlSach)) {
                pstmtSach.setString(1, sach.getMaSach());
                pstmtSach.setString(2, sach.getTenSach());
                pstmtSach.setString(3, sach.getNhaXuatBan());
                if (sach.getNamXuatBan() > 0)
                    pstmtSach.setInt(4, sach.getNamXuatBan());
                else
                    pstmtSach.setNull(4, Types.INTEGER);
                pstmtSach.setInt(5, sach.getSoLuong());
                pstmtSach.setString(6, sach.getMoTa());
                pstmtSach.setString(7, sach.getDuongDanAnh());
                pstmtSach.setString(8, sach.getViTri());
                pstmtSach.executeUpdate();
            }

            // 2. Thêm tác giả
            if (sach.getDanhSachTacGia() != null && !sach.getDanhSachTacGia().isEmpty()) {
                try (PreparedStatement pstmtTG = conn.prepareStatement(sqlSachTacGia)) {
                    for (TacGia tg : sach.getDanhSachTacGia()) {
                        pstmtTG.setString(1, sach.getMaSach());
                        pstmtTG.setString(2, tg.getMaTacGia());
                        pstmtTG.addBatch();
                    }
                    pstmtTG.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Sửa thông tin sách
    public boolean suaSach(Sach sach) {
        String sqlSach = "UPDATE SACH SET tenSach = ?, nhaXuatBan = ?, namXuatBan = ?, soLuong = ?, moTa = ?, duongDanAnh = ?, viTri = ? WHERE maSach = ?";
        String sqlDeleteLinks = "DELETE FROM SACH_TACGIA WHERE maSach = ?";
        String sqlInsertLinks = "INSERT INTO SACH_TACGIA(maSach, maTacGia) VALUES(?, ?)";

        Connection conn = null; // Khai báo conn bên ngoài try-with-resources để dùng trong catch/finally
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật sách
            try (PreparedStatement pstmtSach = conn.prepareStatement(sqlSach)) {
                pstmtSach.setString(1, sach.getTenSach());
                pstmtSach.setString(2, sach.getNhaXuatBan());
                if (sach.getNamXuatBan() > 0)
                    pstmtSach.setInt(3, sach.getNamXuatBan());
                else
                    pstmtSach.setNull(3, Types.INTEGER);
                pstmtSach.setInt(4, sach.getSoLuong());
                pstmtSach.setString(5, sach.getMoTa());
                pstmtSach.setString(6, sach.getDuongDanAnh());
                pstmtSach.setString(7, sach.getViTri());
                pstmtSach.setString(8, sach.getMaSach());
                pstmtSach.executeUpdate();
            }

            // Xóa link tác giả cũ
            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteLinks)) {
                pstmtDelete.setString(1, sach.getMaSach());
                pstmtDelete.executeUpdate();
            }

            // Thêm link tác giả mới
            if (sach.getDanhSachTacGia() != null && !sach.getDanhSachTacGia().isEmpty()) {
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertLinks)) {
                    for (TacGia tg : sach.getDanhSachTacGia()) {
                        pstmtInsert.setString(1, sach.getMaSach());
                        pstmtInsert.setString(2, tg.getMaTacGia());
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Xóa sách (đánh dấu isArchived = 1)
    public boolean xoaSach(String maSach) {
        String sql = "UPDATE SACH SET isArchived = 1 WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSach);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm nâng cao
    public List<Sach> timKiemSachNangCao(String keyword, String searchType) {
        String kwLike = "%" + keyword.trim() + "%";

        String sqlBase = "SELECT s.*, t.maTacGia AS maTacGia_join, t.tenTacGia AS tenTacGia_join " +
                         "FROM SACH s " +
                         "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                         "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia ";
        StringBuilder sqlWhere = new StringBuilder();
        List<Object> params = new ArrayList<>();

        try {
            switch (searchType) {
                case "Nhan đề":
                    sqlWhere.append("WHERE UPPER(s.tenSach) LIKE ? AND s.isArchived = 0");
                    params.add(kwLike.toUpperCase());
                    break;
                case "Tác giả":
                    sqlWhere.append("WHERE s.maSach IN (SELECT st.maSach FROM SACH_TACGIA st JOIN TACGIA t2 ON st.maTacGia = t2.maTacGia WHERE UPPER(t2.tenTacGia) LIKE ?) AND s.isArchived = 0");
                    params.add(kwLike.toUpperCase());
                    break;
                case "Năm xuất bản":
                    int nam = Integer.parseInt(keyword);
                    sqlWhere.append("WHERE s.namXuatBan = ? AND s.isArchived = 0");
                    params.add(nam);
                    break;
                default:
                    sqlWhere.append("WHERE s.isArchived = 0 AND (UPPER(s.tenSach) LIKE ? OR s.nhaXuatBan LIKE ? OR s.maSach LIKE ? OR s.viTri LIKE ?)");
                    params.add(kwLike.toUpperCase());
                    params.add(kwLike);
                    params.add(kwLike);
                    params.add(kwLike);
                    break;
            }

            String sqlOrder = " ORDER BY s.tenSach, t.tenTacGia";
            return getSachInternal(sqlBase + sqlWhere + sqlOrder, params.toArray());

        } catch (NumberFormatException e) {
            System.err.println("⚠️ Lỗi: Năm xuất bản phải là số!");
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}