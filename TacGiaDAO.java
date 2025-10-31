package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TacGiaDAO {

    // 1. Tìm tác giả theo TÊN
    public TacGia findTacGiaByTen(String tenTacGia) {
        String sql = "SELECT * FROM TACGIA WHERE tenTacGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tenTacGia);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new TacGia(
                    rs.getString("maTacGia"),
                    rs.getString("tenTacGia"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("chucDanh")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return null; // Không tìm thấy
    }

    // 2. Thêm một tác giả mới (private vì nó được gọi bởi findOrCreate)
    private TacGia addTacGia(String tenTacGia) {
        String newMaTacGia = generateNewMaTacGia();
        String sql = "INSERT INTO TACGIA (maTacGia, tenTacGia) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newMaTacGia);
            ps.setString(2, tenTacGia);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Trả về đối tượng Tác giả vừa được tạo
                return new TacGia(newMaTacGia, tenTacGia, null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
        return null; // Thêm thất bại
    }

    // 3. Hàm tạo Mã Tác Giả mới (ví dụ: TG001 -> TG002)
    public String generateNewMaTacGia() {
        String sql = "SELECT TOP 1 maTacGia FROM TACGIA ORDER BY maTacGia DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String newId = "TG001"; // ID mặc định nếu bảng trống

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("maTacGia");
                // Lấy phần số từ ID (ví dụ: TG005 -> 5)
                String trimmedLastId = lastId.trim();
                int nextIdNum = Integer.parseInt(lastId.substring(2)) + 1;
                // Format lại (ví dụ: 6 -> TG006)
                newId = String.format("TG%03d", nextIdNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return newId;
    }

    /**
     * HÀM QUAN TRỌNG: Tìm Tác Giả theo tên, nếu không có, tạo mới và trả về.
     */
    public TacGia findOrCreateTacGia(String tenTacGia) {
        // 1. Thử tìm
        TacGia tacGia = findTacGiaByTen(tenTacGia);
        
        // 2. Nếu tìm thấy, trả về ngay
        if (tacGia != null) {
            return tacGia;
        }
        
        // 3. Nếu không tìm thấy, tạo mới và trả về
        return addTacGia(tenTacGia);
    }
}
