package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SachDAO {

    // 1. Tạo Mã Sách mới (ví dụ: SA001 -> SA002)
    public String generateNewMaSach() throws Exception {
        String sql = "SELECT TOP 1 maSach FROM SACH ORDER BY LEN(maSach) DESC, maSach DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String newId = "SA001"; // ID mặc định nếu bảng trống

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("maSach");
                String trimmedLastId = lastId.trim();
                int nextIdNum = Integer.parseInt(lastId.substring(2)) + 1;
                newId = String.format("SA%03d", nextIdNum);
            }
        } catch (Exception e) {
            System.err.println("LỖI NGHIÊM TRỌNG TRONG generateNewMaSach:");
            e.printStackTrace(); 
            // Ném lỗi ra để báo cho người dùng
            throw new Exception("Lỗi khi tạo mã sách mới: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return newId;
    }

    // 2. Thêm Sách Mới (Sử dụng Transaction)
    public boolean addSach(Sach sach, List<TacGia> danhSachTacGia) {
        Connection conn = null;
        PreparedStatement psSach = null;
        PreparedStatement psLink = null;
        
        // Câu lệnh SQL cho bảng SACH
        String sqlSach = "INSERT INTO SACH (maSach, tenSach, nhaXuatBan, namXuatBan, soLuong, moTa, duongDanAnh, viTri, ngayThem, isArchived, conLai) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 0, ?)";
        
        // Câu lệnh SQL cho bảng liên kết SACH_TACGIA
        String sqlLink = "INSERT INTO SACH_TACGIA (maSach, maTacGia) VALUES (?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            // BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);

            // === BƯỚC A: Thêm vào bảng SACH ===
            psSach = conn.prepareStatement(sqlSach);
            psSach.setString(1, sach.getMaSach());
            psSach.setString(2, sach.getTenSach());
            psSach.setString(3, sach.getNhaXuatBan());
            psSach.setInt(4, sach.getNamXuatBan());
            psSach.setInt(5, sach.getSoLuong());
            psSach.setString(6, sach.getMoTa());
            psSach.setString(7, sach.getDuongDanAnh());
            psSach.setString(8, sach.getViTri());
            psSach.setInt(9, sach.getConLai()); // conLai = soLuong khi thêm mới
            
            psSach.executeUpdate();

            // === BƯỚC B: Thêm vào bảng SACH_TACGIA ===
            if (danhSachTacGia != null && !danhSachTacGia.isEmpty()) {
                psLink = conn.prepareStatement(sqlLink);
                
                for (TacGia tg : danhSachTacGia) {
                    psLink.setString(1, sach.getMaSach());
                    psLink.setString(2, tg.getMaTacGia());
                    psLink.addBatch(); // Thêm vào batch để thực thi 1 lần
                }
                psLink.executeBatch(); // Thực thi batch
            }

            // KẾT THÚC TRANSACTION (thành công)
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu có lỗi, ROLLBACK (hủy bỏ) mọi thay đổi
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return false;
        } finally {
            // Trả lại chế độ auto-commit
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Đóng tất cả resource
            DatabaseConnection.closeResource(psLink, conn); // psSach sẽ được đóng bởi psLink
            if(psSach != null) {
                try { psSach.close(); } catch (SQLException e) { /* Bỏ qua */ }
            }
        }
    }
    
    // 3. Lấy tất cả Sách (để hiện lên JTable)
    public List<Sach> getAllSach() {
        // Dùng Map để xử lý N-N (1 sách có nhiều tác giả)
        Map<String, Sach> sachMap = new LinkedHashMap<>();
        
        // Lấy Sách KÈM Tác Giả
        String sql = "SELECT s.*, tg.maTacGia, tg.tenTacGia "
                   + "FROM SACH s "
                   + "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach "
                   + "LEFT JOIN TACGIA tg ON st.maTacGia = tg.maTacGia "
                   + "WHERE s.isArchived = 0 "
                   + "ORDER BY s.maSach";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String maSach = rs.getString("maSach");
                Sach sach = sachMap.get(maSach);

                // Nếu sách chưa có trong Map, tạo mới và thêm vào
                if (sach == null) {
                    sach = new Sach();
                    sach.setMaSach(maSach);
                    sach.setTenSach(rs.getString("tenSach"));
                    sach.setNhaXuatBan(rs.getString("nhaXuatBan"));
                    sach.setNamXuatBan(rs.getInt("namXuatBan"));
                    sach.setSoLuong(rs.getInt("soLuong"));
                    sach.setMoTa(rs.getString("moTa"));
                    sach.setDuongDanAnh(rs.getString("duongDanAnh"));
                    sach.setNgayThem(rs.getTimestamp("ngayThem"));
                    sach.setViTri(rs.getString("viTri"));
                    sach.setConLai(rs.getInt("conLai"));
                    sach.setArchived(rs.getBoolean("isArchived"));
                    
                    sachMap.put(maSach, sach);
                }

                // Thêm Tác giả cho Sách (nếu có)
                String maTacGia = rs.getString("maTacGia");
                if (maTacGia != null) {
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(maTacGia);
                    tg.setTenTacGia(rs.getString("tenTacGia"));
                    sach.getDanhSachTacGia().add(tg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        
        return new ArrayList<>(sachMap.values());
    }
    // 4. Lấy một Sách (kèm tác giả) theo Mã Sách
    public Sach getSachByMa(String maSach) {
        Sach sach = null;
        String sql = "SELECT s.*, tg.maTacGia, tg.tenTacGia "
                   + "FROM SACH s "
                   + "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach "
                   + "LEFT JOIN TACGIA tg ON st.maTacGia = tg.maTacGia "
                   + "WHERE s.maSach = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maSach);
            rs = ps.executeQuery();

            while (rs.next()) {
                // Nếu sách chưa được tạo (chạy lần đầu)
                if (sach == null) {
                    sach = new Sach();
                    sach.setMaSach(rs.getString("maSach"));
                    sach.setTenSach(rs.getString("tenSach"));
                    sach.setNhaXuatBan(rs.getString("nhaXuatBan"));
                    sach.setNamXuatBan(rs.getInt("namXuatBan"));
                    sach.setSoLuong(rs.getInt("soLuong"));
                    sach.setMoTa(rs.getString("moTa"));
                    sach.setDuongDanAnh(rs.getString("duongDanAnh"));
                    sach.setNgayThem(rs.getTimestamp("ngayThem"));
                    sach.setViTri(rs.getString("viTri"));
                    sach.setConLai(rs.getInt("conLai"));
                    sach.setArchived(rs.getBoolean("isArchived"));
                }

                // Thêm Tác giả cho Sách (nếu có)
                String maTacGia = rs.getString("maTacGia");
                if (maTacGia != null) {
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(maTacGia);
                    tg.setTenTacGia(rs.getString("tenTacGia"));
                    sach.getDanhSachTacGia().add(tg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return sach; // Trả về sách (hoặc null nếu không tìm thấy)
    }

    // 5. Cập nhật Sách (Sử dụng Transaction)
    public boolean updateSach(Sach sach, List<TacGia> danhSachTacGia) {
        Connection conn = null;
        PreparedStatement psUpdateSach = null;
        PreparedStatement psDeleteLinks = null;
        PreparedStatement psInsertLinks = null;

        String sqlUpdateSach = "UPDATE SACH SET tenSach = ?, nhaXuatBan = ?, namXuatBan = ?, "
                            + "soLuong = ?, moTa = ?, duongDanAnh = ?, viTri = ?, conLai = ? "
                            + "WHERE maSach = ?";
        String sqlDeleteLinks = "DELETE FROM SACH_TACGIA WHERE maSach = ?";
        String sqlInsertLinks = "INSERT INTO SACH_TACGIA (maSach, maTacGia) VALUES (?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            // BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);

            // === BƯỚC A: Cập nhật bảng SACH ===
            psUpdateSach = conn.prepareStatement(sqlUpdateSach);
            psUpdateSach.setString(1, sach.getTenSach());
            psUpdateSach.setString(2, sach.getNhaXuatBan());
            psUpdateSach.setInt(3, sach.getNamXuatBan());
            psUpdateSach.setInt(4, sach.getSoLuong());
            psUpdateSach.setString(5, sach.getMoTa());
            psUpdateSach.setString(6, sach.getDuongDanAnh());
            psUpdateSach.setString(7, sach.getViTri());
            psUpdateSach.setInt(8, sach.getConLai());
            psUpdateSach.setString(9, sach.getMaSach());
            psUpdateSach.executeUpdate();

            // === BƯỚC B: Xóa tất cả liên kết Tác Giả cũ ===
            psDeleteLinks = conn.prepareStatement(sqlDeleteLinks);
            psDeleteLinks.setString(1, sach.getMaSach());
            psDeleteLinks.executeUpdate();

            // === BƯỚC C: Thêm lại liên kết Tác Giả mới ===
            if (danhSachTacGia != null && !danhSachTacGia.isEmpty()) {
                psInsertLinks = conn.prepareStatement(sqlInsertLinks);
                for (TacGia tg : danhSachTacGia) {
                    psInsertLinks.setString(1, sach.getMaSach());
                    psInsertLinks.setString(2, tg.getMaTacGia());
                    psInsertLinks.addBatch();
                }
                psInsertLinks.executeBatch();
            }

            // KẾT THÚC TRANSACTION (thành công)
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback nếu có lỗi
            } catch (SQLException e2) { e2.printStackTrace(); }
            return false;
        } finally {
            // Trả lại auto-commit và đóng resources
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (psUpdateSach != null) try { psUpdateSach.close(); } catch (SQLException e) { /* Bỏ qua */ }
            if (psDeleteLinks != null) try { psDeleteLinks.close(); } catch (SQLException e) { /* Bỏ qua */ }
            DatabaseConnection.closeResource(psInsertLinks, conn); // Đóng psInsertLinks và conn
        }
    }
    // 6. Xóa Mềm Sách (Soft Delete)
    // Cập nhật cột isArchived = 1
    public boolean softDeleteSach(String maSach) {
        String sql = "UPDATE SACH SET isArchived = 1 WHERE maSach = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maSach);
            
            int rowsAffected = ps.executeUpdate();
            
            // Trả về true nếu có 1 hàng bị ảnh hưởng (tức là update thành công)
            return (rowsAffected > 0); 

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Thất bại
        } finally {
            // Dùng hàm helper trong DatabaseConnection để đóng
            DatabaseConnection.closeResource(ps, conn);
        }
    }
}
