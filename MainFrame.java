/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

// === CÁC LỚP CẦN THIẾT TỪ CÁC PACKAGE KHÁC ===
import QuanLyThuVien.SachDetailDialog; 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Sach;         
import dao.SachDAO;        

import model.DocGia;       
import dao.DocGiaDAO;      
import QuanLyThuVien.DocGiaEditDialog; 

import model.TacGia;       
import dao.TacGiaDAO;      
import QuanLyThuVien.TacGiaEditDialog; 

import model.BoSuuTap;     
import dao.BoSuuTapDAO;    
import QuanLyThuVien.BoSuuTapEditDialog;

import model.MuonTra;      
import dao.MuonTraDAO;     
import QuanLyThuVien.MuonSachDialog; 

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager; 
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer; 
/**
 *
 * @author baonguyenn
 */

public class MainFrame extends JFrame {
    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;

    private JTable docGiaTable;
    private DefaultTableModel docGiaTableModel;
    private DocGiaDAO docGiaDAO;
    private JTextField docGiaSearchField;

    private JTable sachTable;
    private DefaultTableModel sachTableModel;
    private SachDAO sachDAO;
    private JTextField sachSearchField;

    private JTable tacGiaTable;
    private DefaultTableModel tacGiaTableModel;
    private TacGiaDAO tacGiaDAO;
    private JTextField tacGiaSearchField;

    private JList<BoSuuTap> boSuuTapList; 
    private DefaultListModel<BoSuuTap> boSuuTapListModel; 
    private JTable sachTrongBSTTable;
    private DefaultTableModel sachTrongBSTTableModel;
    private BoSuuTapDAO boSuuTapDAO;

    private JTable muonTraTable;
    private DefaultTableModel muonTraTableModel;
    private MuonTraDAO muonTraDAO;
    private JComboBox<String> cbFilterTrangThaiMuonTra; // ComboBox lọc

    private SimpleDateFormat sdfAdmin = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfMuonTra = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final String currentUsername = "admin";

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());

        sachDAO = new SachDAO();
        docGiaDAO = new DocGiaDAO();
        tacGiaDAO = new TacGiaDAO();
        boSuuTapDAO = new BoSuuTapDAO();
        muonTraDAO = new MuonTraDAO();
        
        // 1. Model cho Sách
        String[] sachColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Vị trí", "Ngày Thêm"};
        sachTableModel = new DefaultTableModel(sachColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 2. Model cho Độc Giả
        String[] docGiaColumns = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"};
        docGiaTableModel = new DefaultTableModel(docGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 3. Model cho Tác Giả (ĐÃ SỬA: Bỏ "Trình độ chuyên môn")
        String[] tacGiaColumns = {"Mã Tác Giả", "Tên Tác Giả", "Email", "SĐT", "Chức Danh"};
        tacGiaTableModel = new DefaultTableModel(tacGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 4. Model cho Mượn Trả
        String[] muonTraColumns = {"Mã Mượn", "Độc Giả", "Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Trạng Thái", "Loại Mượn"};
        muonTraTableModel = new DefaultTableModel(muonTraColumns, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 5. Models cho Bộ Sưu Tập
        boSuuTapListModel = new DefaultListModel<>();
        String[] sachBSTColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"};
        sachTrongBSTTableModel = new DefaultTableModel(sachBSTColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // Header
        headerLabel = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN | Chào, " + this.currentUsername);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBackground(new Color(30, 144, 255)); headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true); headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setPreferredSize(new Dimension(getWidth(), 60));
        add(headerLabel, BorderLayout.NORTH);

        createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        mainContentPanel = new JPanel(); mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 240, 240));
        add(mainContentPanel, BorderLayout.CENTER);

        showPanel("Tổng quan");
        setLocationRelativeTo(null);
    }

    // Xử lý Đăng xuất
    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất không?",
            "Xác nhận Đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }

    // Tạo Menu
    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(220, getHeight()));
        menuPanel.setLayout(new GridLayout(0, 1, 0, 10));

        String[] menuItems = {
            "Tổng quan",
            "Quản lý Sách",
            "Quản lý Độc giả",
            "Quản lý Tác giả",
            "Bộ sưu tập",
            "Quản lý Mượn/Trả", 
            "Báo cáo & Thống kê",
            "Đổi mật khẩu"
        };

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(buttonFont);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(70, 70, 70));
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(200, 40));

            button.addActionListener(e -> showPanel(item));
            menuPanel.add(button);
        }

        JButton logoutButton = new JButton("ĐĂNG XUẤT");
        logoutButton.setFont(buttonFont);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(178, 34, 34)); // Màu đỏ
        logoutButton.setFocusPainted(false);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(200, 40));

        logoutButton.addActionListener(this::btnDangXuatActionPerformed);
        menuPanel.add(logoutButton);

        menuPanel.add(new JLabel());
    }

    // Hiển thị Panel
    private void showPanel(String panelName) {
        mainContentPanel.removeAll();
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.setBackground(Color.WHITE);

        switch (panelName) {
            case "Tổng quan":
                newPanel.add(new JLabel("<< DASHBOARD >>", SwingConstants.CENTER));
                break;
            case "Quản lý Sách":
                newPanel = createBookManagementPanel();
                break;
            case "Quản lý Độc giả":
                newPanel = createDocGiaManagementPanel();
                break;
            case "Quản lý Tác giả":
                newPanel = createTacGiaManagementPanel(); // Placeholder
                break;
            case "Bộ sưu tập":
                newPanel = createBoSuuTapManagementPanel(); // Placeholder
                break;
            case "Quản lý Mượn/Trả":
                newPanel = createMuonTraManagementPanel(); // Placeholder
                break;
            case "Báo cáo & Thống kê":
                newPanel.add(new JLabel("<< BÁO CÁO & THỐNG KÊ >>", SwingConstants.CENTER));
                break;
            case "Đổi mật khẩu":
                newPanel = createChangePasswordPanel(); // Hoàn thành logic bị cắt
                break;
            default:
                newPanel.add(new JLabel(panelName + " chưa có.", SwingConstants.CENTER));
        }
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Quản lý Sách
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ SÁCH");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        sachSearchField = new JTextField(25);
        JButton searchButton = new JButton("Tìm");
        JButton viewAllButton = new JButton("Xem tất cả");
        searchPanel.add(sachSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Vị trí", "Ngày Thêm"};
        
        sachTable = new JTable(sachTableModel);
        sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sachTable.setRowHeight(25);
        sachTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sachTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        sachTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        sachTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        JTableHeader header = sachTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(sachTable));
        JScrollPane scrollPane = new JScrollPane(sachTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Sách");
        JButton editButton = new JButton("Sửa Sách");
        JButton deleteButton = new JButton("Xóa Sách");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadSachData();
        searchButton.addActionListener(e -> timKiemSach());
        viewAllButton.addActionListener(e -> loadSachData());
        addButton.addActionListener(e -> themSach());
        editButton.addActionListener(e -> suaSach());
        deleteButton.addActionListener(e -> xoaSach());
        
        sachTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    String maSach = table.getValueAt(row, 0).toString();
                    Sach sach = sachDAO.getSachByMaSach(maSach);
                    if (sach != null) {
                        // Cần đảm bảo SachDetailDialog được tạo đúng
                        // SachDetailDialog detailDialog = new SachDetailDialog(MainFrame.this, sach); 
                        // detailDialog.setVisible(true);
                    }
                }
            }
        });
        return panel;
    }
    private void loadSachData() {
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.getAllSach();
            for (Sach s : danhSach) {
                String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                sachTableModel.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(),
                    s.getNhaXuatBan(), s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A",
                    s.getSoLuong(), s.getViTri(), ngayThemStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void timKiemSach() {
        String keyword = sachSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.timKiemSachNangCao(keyword, "Tất cả");
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Sach s : danhSach) {
                    String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                    sachTableModel.addRow(new Object[]{
                        s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(),
                        s.getNhaXuatBan(), s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A",
                        s.getSoLuong(), s.getViTri(), ngayThemStr
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void themSach() {
        // Cần đảm bảo SachEditDialog được tạo đúng
        // SachEditDialog dialog = new SachEditDialog(this, sachDAO, null);
        // dialog.setVisible(true);
        // if (dialog.isSaveSuccess()) loadSachData();
        JOptionPane.showMessageDialog(this, "Tính năng Thêm Sách chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    private void suaSach() {
        int row = sachTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        // ... logic sửa sách ...
        JOptionPane.showMessageDialog(this, "Tính năng Sửa Sách chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    private void xoaSach() {
        int row = sachTable.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(this, "Chọn sách để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        String ma = sachTableModel.getValueAt(row, 0).toString();
        String ten = sachTableModel.getValueAt(row, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách '" + ten + "' (Mã: " + ma + ")?\n"
                + "CẢNH BÁO: Hành động này sẽ ẩn sách khỏi hệ thống nhưng vẫn giữ lịch sử mượn trả.", 
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (sachDAO.xoaSach(ma)) { 
                    JOptionPane.showMessageDialog(this, "Xóa (ẩn) sách thành công!");
                    loadSachData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Quản lý Độc giả
    private JPanel createDocGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm (tên, email, sdt, mã):"));
        docGiaSearchField = new JTextField(25);
        JButton searchButton = new JButton("Tìm");
        JButton viewAllButton = new JButton("Xem tất cả");
        searchPanel.add(docGiaSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Dùng docGiaTableModel đã định nghĩa ở constructor
        docGiaTable = new JTable(docGiaTableModel);
        docGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        docGiaTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(docGiaTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Độc Giả");
        JButton editButton = new JButton("Sửa Thông Tin");
        JButton deleteButton = new JButton("Xóa Độc Giả");
        JButton blockButton = new JButton("Khóa / Mở khóa");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(blockButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadDocGiaData();
        searchButton.addActionListener(e -> timKiemDocGia());
        viewAllButton.addActionListener(e -> loadDocGiaData());
        addButton.addActionListener(e -> themDocGia());
        editButton.addActionListener(e -> suaDocGia());
        deleteButton.addActionListener(e -> xoaDocGia());
        blockButton.addActionListener(e -> khoaMoKhoaDocGia());
        return panel;
    }
    private void loadDocGiaData() {
        try {
            docGiaTableModel.setRowCount(0);
            List<DocGia> danhSach = docGiaDAO.getAllDocGia();
            SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy");
            for (DocGia dg : danhSach) {
                String trangThai = dg.getTrangThai();
                String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : "";
                docGiaTableModel.addRow(new Object[]{
                    dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(),
                    dg.getSdt(), dg.getDiaChi(), trangThai
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu độc giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void timKiemDocGia() {
        String keyword = docGiaSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            docGiaTableModel.setRowCount(0);
            List<DocGia> danhSach = docGiaDAO.timKiemDocGia(keyword);
            SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy");
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
            for (DocGia dg : danhSach) {
                String trangThai = dg.getTrangThai();
                String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : "";
                docGiaTableModel.addRow(new Object[]{
                    dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(),
                    dg.getSdt(), dg.getDiaChi(), trangThai
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void themDocGia() {
        // Cần đảm bảo DocGiaEditDialog được tạo đúng
        // DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, null);
        // dialog.setVisible(true);
        // if (dialog.isSaveSuccess()) loadDocGiaData();
        JOptionPane.showMessageDialog(this, "Tính năng Thêm Độc Giả chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void suaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ... logic sửa độc giả ...
        JOptionPane.showMessageDialog(this, "Tính năng Sửa Độc Giả chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    private void xoaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = docGiaTableModel.getValueAt(row, 0).toString();
        String ten = docGiaTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa độc giả '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (docGiaDAO.xoaDocGia(ma)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDocGiaData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (Độc giả đang mượn sách?).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void khoaMoKhoaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để Khóa/Mở.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = docGiaTableModel.getValueAt(row, 0).toString();
        String ten = docGiaTableModel.getValueAt(row, 1).toString();
        String status = docGiaTableModel.getValueAt(row, 6).toString();
        boolean seBiKhoa = status.equals("Hoạt động");
        String action = seBiKhoa ? "KHÓA" : "MỞ KHÓA";
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn " + action + " tài khoản '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (docGiaDAO.khoaMoKhoaDocGia(ma, seBiKhoa)) {
                    JOptionPane.showMessageDialog(this, action + " thành công!");
                    loadDocGiaData();
                } else {
                    JOptionPane.showMessageDialog(this, action + " thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi " + action + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Quản lý Tác giả
    private JPanel createTacGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("QUẢN LÝ TÁC GIẢ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        tacGiaTable = new JTable(tacGiaTableModel);
        tacGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tacGiaTable.setRowHeight(25);
        
        JTableHeader header = tacGiaTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(tacGiaTable));
        JScrollPane scrollPane = new JScrollPane(tacGiaTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Tác Giả");
        JButton editButton = new JButton("Sửa Tác Giả");
        JButton deleteButton = new JButton("Xóa Tác Giả");
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadTacGiaData();
        
        addButton.addActionListener(e -> themTacGia());
        editButton.addActionListener(e -> suaTacGia());
        deleteButton.addActionListener(e -> xoaTacGia());

        return panel;
    }

    private void loadTacGiaData() {
         try {
            tacGiaTableModel.setRowCount(0);
            List<TacGia> danhSach = tacGiaDAO.getAllTacGia();
            for (TacGia tg : danhSach) {
                // ĐÃ SỬA: Bỏ tg.getTrinhDoChuyenMon()
                tacGiaTableModel.addRow(new Object[]{
                    tg.getMaTacGia(), tg.getTenTacGia(), tg.getEmail(), 
                    tg.getSdt(), tg.getChucDanh()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Tác Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themTacGia() {
        // Cần đảm bảo TacGiaEditDialog được tạo đúng
        // TacGiaEditDialog dialog = new TacGiaEditDialog(this, tacGiaDAO, null);
        // dialog.setVisible(true);
        // if (dialog.isSaveSuccess()) loadTacGiaData();
        JOptionPane.showMessageDialog(this, "Tính năng Thêm Tác Giả chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void suaTacGia() {
        int row = tacGiaTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn tác giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        // ... logic sửa tác giả ...
        JOptionPane.showMessageDialog(this, "Tính năng Sửa Tác Giả chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void xoaTacGia() {
        int row = tacGiaTable.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(this, "Chọn tác giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        String ma = tacGiaTableModel.getValueAt(row, 0).toString();
        String ten = tacGiaTableModel.getValueAt(row, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa tác giả '" + ten + "' (Mã: " + ma + ")?\n"
                + "CẢNH BÁO: Thao tác này có thể ảnh hưởng đến các sách liên quan.",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tacGiaDAO.xoaTacGia(ma)) { 
                    JOptionPane.showMessageDialog(this, "Xóa tác giả thành công!");
                    loadTacGiaData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (Tác giả có thể còn sách liên kết).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Tác Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Quản lý Bộ sưu tập
    private JPanel createBoSuuTapManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Chia panel làm đôi: List BST bên trái, Sách trong BST bên phải
        boSuuTapList = new JList<>(boSuuTapListModel);
        boSuuTapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(boSuuTapList);
        listScrollPane.setPreferredSize(new Dimension(300, 0));
        
        sachTrongBSTTable = new JTable(sachTrongBSTTableModel);
        sachTrongBSTTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sachTrongBSTTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(sachTrongBSTTable);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, tableScrollPane);
        splitPane.setDividerLocation(300);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm BST");
        JButton editButton = new JButton("Sửa BST");
        JButton deleteButton = new JButton("Xóa BST");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadBoSuuTapData();
        
        addButton.addActionListener(e -> themBoSuuTap());
        editButton.addActionListener(e -> suaBoSuuTap());
        deleteButton.addActionListener(e -> xoaBoSuuTap());
        
        boSuuTapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
                if (selectedBST != null) {
                    loadSachTrongBST(selectedBST.getMaBoSuuTap());
                } else {
                    sachTrongBSTTableModel.setRowCount(0);
                }
            }
        });

        return panel;
    }
    
    private void loadBoSuuTapData() {
         try {
            boSuuTapListModel.clear();
            List<BoSuuTap> danhSach = boSuuTapDAO.getAll();
            for (BoSuuTap bst : danhSach) {
                boSuuTapListModel.addElement(bst);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Bộ Sưu Tập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSachTrongBST(int maBoSuuTap) {
        try {
            sachTrongBSTTableModel.setRowCount(0);
            List<Sach> danhSachSach = boSuuTapDAO.getSachByBoSuuTap(maBoSuuTap);
            for (Sach s : danhSachSach) {
                sachTrongBSTTableModel.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(), s.getNamXuatBan()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Sách trong BST: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themBoSuuTap() {
        // Cần đảm bảo BoSuuTapEditDialog được tạo đúng
        // BoSuuTapEditDialog dialog = new BoSuuTapEditDialog(this, boSuuTapDAO, null);
        // dialog.setVisible(true);
        // if (dialog.isSaveSuccess()) loadBoSuuTapData();
        JOptionPane.showMessageDialog(this, "Tính năng Thêm Bộ Sưu Tập chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void suaBoSuuTap() {
        BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
        if (selectedBST == null) { JOptionPane.showMessageDialog(this, "Chọn bộ sưu tập để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        
        // ... logic sửa Bộ Sưu Tập ...
        JOptionPane.showMessageDialog(this, "Tính năng Sửa Bộ Sưu Tập chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void xoaBoSuuTap() {
        BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
        if (selectedBST == null) { JOptionPane.showMessageDialog(this, "Chọn bộ sưu tập để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa Bộ sưu tập '" + selectedBST.getTenBoSuuTap() + "'?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (boSuuTapDAO.delete(selectedBST.getMaBoSuuTap())) { 
                    JOptionPane.showMessageDialog(this, "Xóa Bộ sưu tập thành công!");
                    loadBoSuuTapData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Bộ sưu tập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Quản lý Mượn trả
    private JPanel createMuonTraManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("QUẢN LÝ MƯỢN TRẢ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        muonTraTable = new JTable(muonTraTableModel);
        muonTraTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        muonTraTable.setRowHeight(25);
        
        JTableHeader header = muonTraTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(muonTraTable));
        JScrollPane scrollPane = new JScrollPane(muonTraTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Tạo Phiếu Mượn");
        JButton traSachButton = new JButton("Đánh Dấu Đã Trả");
        JButton giaHanButton = new JButton("Gia Hạn");
        
        controlPanel.add(addButton);
        controlPanel.add(traSachButton);
        controlPanel.add(giaHanButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadMuonTraData(null);
        
        addButton.addActionListener(e -> themMuonTra());
        traSachButton.addActionListener(e -> traSach());
        giaHanButton.addActionListener(e -> giaHanMuonTra());
        
        return panel;
    }
    
    private void loadMuonTraData(String trangThaiFilter) {
        try {
            muonTraTableModel.setRowCount(0);
            List<MuonTra> danhSach = muonTraDAO.getAllMuonTra(trangThaiFilter);
            for (MuonTra mt : danhSach) {
                String ngayTraStr = mt.getNgayTraThucTe() != null ? sdfMuonTra.format(mt.getNgayTraThucTe()) : "Chưa trả";
                muonTraTableModel.addRow(new Object[]{
                    mt.getMaMuonTra(), 
                    mt.getDocGia().getHoTen() + " (" + mt.getDocGia().getMaDocGia() + ")", 
                    mt.getSach().getTenSach() + " (" + mt.getSach().getMaSach() + ")",
                    sdfMuonTra.format(mt.getNgayMuon()),
                    sdfMuonTra.format(mt.getNgayHenTra()),
                    ngayTraStr,
                    mt.getTrangThaiMuonTra(),
                    mt.getLoaiMuon()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Mượn/Trả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themMuonTra() {
        // Cần đảm bảo MuonSachDialog được tạo đúng
        // MuonSachDialog dialog = new MuonSachDialog(this, muonTraDAO, docGiaDAO, sachDAO);
        // dialog.setVisible(true);
        // if (dialog.isSaveSuccess()) {
        //     loadMuonTraData(null);
        //     loadSachData();
        // }
        JOptionPane.showMessageDialog(this, "Tính năng Tạo Phiếu Mượn chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void traSach() {
        int row = muonTraTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn phiếu mượn để đánh dấu trả sách.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        
        int maMuonTra = (int) muonTraTableModel.getValueAt(row, 0);
        String trangThai = muonTraTableModel.getValueAt(row, 6).toString();
        
        if (trangThai.equals("Đã trả")) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn này đã được trả trước đó.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đánh dấu ĐÃ TRẢ cho phiếu mượn #" + maMuonTra + "?",
                "Xác nhận Trả sách", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (muonTraDAO.traSach(maMuonTra)) {
                    JOptionPane.showMessageDialog(this, "Đánh dấu trả sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadMuonTraData(null); 
                    loadSachData();    
                } else {
                     JOptionPane.showMessageDialog(this, "Đánh dấu trả sách thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Lỗi khi đánh dấu trả sách: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
            }
        }
    }
    
    private void giaHanMuonTra() {
         int row = muonTraTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn phiếu mượn để gia hạn.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        
        // ... logic gia hạn ...
        JOptionPane.showMessageDialog(this, "Tính năng Gia Hạn chưa được triển khai hoàn chỉnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // Hàm đổi Mật khẩu
    private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 3),
                "ĐỔI MẬT KHẨU TÀI KHOẢN ", 
                javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 36), 
                new Color(30, 144, 255)
            )
        ));
        formPanel.setPreferredSize(new Dimension(650, 350));

        JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
        oldPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField oldPassField = new JPasswordField(15);
        oldPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        newPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField newPassField = new JPasswordField(15);
        newPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel confirmPassLabel = new JLabel("Xác nhận MK mới:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField confirmPassField = new JPasswordField(15);
        confirmPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        final JButton changeButton = new JButton("ĐỔI MẬT KHẨU");
        changeButton.setBackground(new Color(30, 144, 255));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Hàng 1: Mật khẩu cũ
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(oldPassLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(oldPassField, gbc);

        // Hàng 2: Mật khẩu mới
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(newPassLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(newPassField, gbc);

        // Hàng 3: Xác nhận mật khẩu mới
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(confirmPassField, gbc); 

        // Hàng 4: Nút Đổi mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Dàn ra 2 cột
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(changeButton, gbc);
        
        changeButton.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Tính năng Đổi mật khẩu chưa được triển khai hoàn chỉnh."));

        panel.add(formPanel, new GridBagConstraints());
        return panel;
    }
    
    // CUSTOM RENDERER CHO HEADER CỦA JTABLE
    private class HeaderRenderer implements TableCellRenderer {
        final TableCellRenderer defaultRenderer;
        public HeaderRenderer(JTable table) {
            this.defaultRenderer = table.getTableHeader().getDefaultRenderer();
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER); 
            label.setBackground(new Color(173, 216, 230)); 
            label.setForeground(Color.BLACK);
            return label;
        }
    }


    // Hàm main
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Không thể thiết lập Nimbus L&F. Sử dụng mặc định.");
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}