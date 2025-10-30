/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.BoSuuTap;
import model.Sach;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author baonguyenn
 */
public class BoSuuTapDAO {
    public List<BoSuuTap> getAll() {
        List<BoSuuTap> ds = new ArrayList<>();
        String sql = "SELECT * FROM BoSuuTap";

        // SỬA: Dùng DatabaseConnection.getConnection() thay vì JDBCConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BoSuuTap bst = new BoSuuTap(
                    rs.getInt("MaBoSuuTap"),
                    rs.getString("TenBoSuuTap"),
                    rs.getString("MoTa"),
                    rs.getString("DuongDanAnh")
                );
                ds.add(bst);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Thêm bộ sưu tập mới
    public boolean insert(BoSuuTap bst) {
        String sql = "INSERT INTO BoSuuTap (TenBoSuuTap, MoTa, DuongDanAnh) VALUES (?, ?, ?)";

        // SỬA: Dùng DatabaseConnection.getConnection() thay vì JDBCConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bst.getTenBoSuuTap());
            stmt.setString(2, bst.getMoTa());
            stmt.setString(3, bst.getDuongDanAnh());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật thông tin bộ sưu tập
    public boolean update(BoSuuTap bst) {
        String sql = "UPDATE BoSuuTap SET TenBoSuuTap = ?, MoTa = ?, DuongDanAnh = ? WHERE MaBoSuuTap = ?";

        // SỬA: Dùng DatabaseConnection.getConnection() thay vì JDBCConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bst.getTenBoSuuTap());
            stmt.setString(2, bst.getMoTa());
            stmt.setString(3, bst.getDuongDanAnh());
            stmt.setInt(4, bst.getMaBoSuuTap());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa bộ sưu tập theo mã
    public boolean delete(int maBoSuuTap) {
        String sql = "DELETE FROM BoSuuTap WHERE MaBoSuuTap = ?";

        // SỬA: Dùng DatabaseConnection.getConnection() thay vì JDBCConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBoSuuTap);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Tìm kiếm bộ sưu tập theo tên
    public List<BoSuuTap> searchByName(String keyword) {
        List<BoSuuTap> ds = new ArrayList<>();
        String sql = "SELECT * FROM BoSuuTap WHERE TenBoSuuTap LIKE ?";

        // SỬA: Dùng DatabaseConnection.getConnection() thay vì JDBCConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                BoSuuTap bst = new BoSuuTap(
                    rs.getInt("MaBoSuuTap"),
                    rs.getString("TenBoSuuTap"),
                    rs.getString("MoTa"),
                    rs.getString("DuongDanAnh")
                );
                ds.add(bst);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }
}