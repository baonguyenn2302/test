package QuanLyThuVien;

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
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager; 
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer; 
import java.text.SimpleDateFormat;
public class MainFrame extends JFrame {
    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;

    private JTable docGiaTable;
    private DefaultTableModel docGiaTableModel;
    private JTextField docGiaSearchField;

    private JTable sachTable;
    private DefaultTableModel sachTableModel;
    private JTextField sachSearchField;
    private SachDAO sachDAO;
    private JTable tacGiaTable;
    private DefaultTableModel tacGiaTableModel;
    private JTextField tacGiaSearchField;

    private JList<String> boSuuTapList;
    private DefaultListModel<String> boSuuTapListModel;
    private JTable sachTrongBSTTable;
    private DefaultTableModel sachTrongBSTTableModel;

    private JTable muonTraTable;
    private DefaultTableModel muonTraTableModel;
    private JComboBox<String> cbFilterTrangThaiMuonTra;

    private final String currentUsername = "admin";

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());
        
        sachDAO = new SachDAO();
        // 1. Model cho Sách (ĐÃ SỬA CỘT)
        String[] sachColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Còn lại", "Vị trí", "Ngày Thêm"};
        sachTableModel = new DefaultTableModel(sachColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 2. Model cho Độc Giả
        String[] docGiaColumns = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"};
        docGiaTableModel = new DefaultTableModel(docGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 3. Model cho Tác Giả
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

    private void showNotImplementedMessage() {
        JOptionPane.showMessageDialog(this, "Tính năng này chưa được triển khai (logic đã bị xóa).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {
        showNotImplementedMessage();
    }

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
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setFocusPainted(false);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(200, 40));

        logoutButton.addActionListener(this::btnDangXuatActionPerformed);
        menuPanel.add(logoutButton);

        menuPanel.add(new JLabel());
    }

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
                loadSachData();
                break;
            case "Quản lý Độc giả":
                newPanel = createDocGiaManagementPanel();
                break;
            case "Quản lý Tác giả":
                newPanel = createTacGiaManagementPanel(); 
                break;
            case "Bộ sưu tập":
                newPanel = createBoSuuTapManagementPanel(); 
                break;
            case "Quản lý Mượn/Trả":
                newPanel = createMuonTraManagementPanel(); 
                break;
            case "Báo cáo & Thống kê":
                newPanel.add(new JLabel("<< BÁO CÁO & THỐNG KÊ >>", SwingConstants.CENTER));
                break;
            case "Đổi mật khẩu":
                newPanel = createChangePasswordPanel(); 
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
        
        sachTable = new JTable(sachTableModel);
        sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sachTable.setRowHeight(25);
        
        // ĐỘ RỘNG CỘT (ĐÃ SỬA)
        sachTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã Sách
        sachTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên Sách
        sachTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Tác giả
        sachTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Số Lượng
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Còn lại
        sachTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Vị trí
        sachTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Ngày Thêm
        
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
        
        // === SỬA LỖI QUAN TRỌNG NHẤT LÀ Ở ĐÂY ===
        searchButton.addActionListener(e -> showNotImplementedMessage());
        viewAllButton.addActionListener(e -> showNotImplementedMessage());
        
        // DÒNG NÀY ĐÃ ĐƯỢC SỬA:
        addButton.addActionListener(e -> {
            try {
                themSach();
            } catch (Exception ex) {
                System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }); // <-- Sửa từ showNotImplementedMessage()
        
        editButton.addActionListener(e -> suaSach());
        deleteButton.addActionListener(e -> xoaSach());
        
        return panel;
    }

    // === CÁC HÀM LOGIC SÁCH ===
    
    // HÀM NÀY ĐÃ ĐƯỢC SỬA (trong các hướng dẫn trước)
    private void themSach() throws Exception {
        // Bước 1: Lấy mã sách mới nhất từ CSDL
        String newMaSach = sachDAO.generateNewMaSach();

        // Bước 2: Tạo và hiển thị Dialog
        SachEditDialog dialog = new SachEditDialog(this, newMaSach);
        dialog.setVisible(true);

        // Bước 3: (Sau khi dialog đóng lại) Kiểm tra xem có lưu thành công không
        if (dialog.isSaveSuccess()) {
            // Nếu thành công, TẢI LẠI DỮ LIỆU BẢNG
            loadSachData(); 
            JOptionPane.showMessageDialog(this, "Đã thêm sách thành công!");
        }
    }
    // HÀM MỚI DÀNH CHO NÚT SỬA SÁCH
    private void suaSach() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = sachTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một cuốn sách để sửa.", 
                "Chưa chọn sách", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy Mã Sách từ hàng đã chọn (Cột 0)
        String maSach = sachTableModel.getValueAt(selectedRow, 0).toString();

        // 3. Dùng DAO để lấy TOÀN BỘ thông tin sách (bao gồm tác giả)
        Sach sachToEdit = sachDAO.getSachByMa(maSach);

        if (sachToEdit == null) {
            JOptionPane.showMessageDialog(this, "Không thể tìm thấy dữ liệu sách (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Mở Dialog ở chế độ SỬA (dùng Constructor mới)
        SachEditDialog dialog = new SachEditDialog(this, sachToEdit);
        dialog.setVisible(true);

        // 5. Nếu lưu thành công, tải lại bảng
        if (dialog.isSaveSuccess()) {
            loadSachData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật sách thành công!");
        }
    }
    // ... (Các hàm logic khác chưa triển khai) ...
    // HÀM MỚI DÀNH CHO NÚT XÓA SÁCH
    private void xoaSach() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = sachTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một cuốn sách để xóa.", 
                "Chưa chọn sách", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy thông tin sách từ JTable
        String maSach = sachTableModel.getValueAt(selectedRow, 0).toString();
        String tenSach = sachTableModel.getValueAt(selectedRow, 1).toString();

        // 3. Hiển thị hộp thoại xác nhận (Confirm Dialog)
        String confirmMsg = "Bạn có chắc chắn muốn xóa (lưu trữ) sách:\n"
                          + tenSach + " (Mã: " + maSach + ")\n"
                          + "Sách sẽ bị ẩn đi, không xóa vĩnh viễn.";
                          
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "Xác nhận Xóa Mềm", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        // 4. Xử lý nếu người dùng đồng ý (chọn Yes)
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi DAO để thực hiện xóa mềm
            boolean success = sachDAO.softDeleteSach(maSach);
            
            if (success) {
                // Tải lại bảng (cuốn sách sẽ biến mất vì isArchived=1)
                loadSachData(); 
                JOptionPane.showMessageDialog(this, "Đã xóa (lưu trữ) sách thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sách thất bại (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Nếu người dùng chọn "No" hoặc đóng dialog, không làm gì cả.
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
        
        searchButton.addActionListener(e -> showNotImplementedMessage());
        viewAllButton.addActionListener(e -> showNotImplementedMessage());
        addButton.addActionListener(e -> showNotImplementedMessage());
        editButton.addActionListener(e -> showNotImplementedMessage());
        deleteButton.addActionListener(e -> showNotImplementedMessage());
        blockButton.addActionListener(e -> showNotImplementedMessage());
        return panel;
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
        
        addButton.addActionListener(e -> showNotImplementedMessage());
        editButton.addActionListener(e -> showNotImplementedMessage());
        deleteButton.addActionListener(e -> showNotImplementedMessage());

        return panel;
    }

    // Quản lý Bộ sưu tập
    private JPanel createBoSuuTapManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
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
        
        addButton.addActionListener(e -> showNotImplementedMessage());
        editButton.addActionListener(e -> showNotImplementedMessage());
        deleteButton.addActionListener(e -> showNotImplementedMessage());
        
        boSuuTapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sachTrongBSTTableModel.setRowCount(0);
            }
        });

        return panel;
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
        
        addButton.addActionListener(e -> showNotImplementedMessage());
        traSachButton.addActionListener(e -> showNotImplementedMessage());
        giaHanButton.addActionListener(e -> showNotImplementedMessage());
        
        return panel;
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

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(oldPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(oldPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(newPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(newPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(confirmPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(confirmPassField, gbc); 

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(changeButton, gbc);
        
        changeButton.addActionListener(e -> showNotImplementedMessage());

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
    // TẠO HÀM MỚI NÀY (hoặc viết đè lên hàm cũ)
    private void loadSachData() {
        // Xóa dữ liệu cũ
        sachTableModel.setRowCount(0);

        // Lấy danh sách Sách (đã bao gồm Tác giả) từ DAO
        List<Sach> danhSach = sachDAO.getAllSach();

        // Định dạng ngày (Nếu cột ngayThem của bạn là DATETIME)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Sach s : danhSach) {
            // Xử lý cột "Còn lại"
            String conLaiStr = (s.getConLai() > 0) ? String.valueOf(s.getConLai()) : "Hết";

            // Xử lý cột "Tác giả" (từ List<TacGia>)
            String tacGiaDisplay = s.getTenTacGiaDisplay(); // Dùng hàm có sẵn trong Sach.java

            Object[] row = new Object[] {
                s.getMaSach(),
                s.getTenSach(),
                tacGiaDisplay,
                s.getNhaXuatBan(),
                s.getNamXuatBan() == 0 ? "N/A" : s.getNamXuatBan(), // Tránh hiển thị số 0
                s.getSoLuong(),
                conLaiStr,
                s.getViTri(),
                s.getNgayThem() != null ? sdf.format(s.getNgayThem()) : "N/A"
            };

            sachTableModel.addRow(row);
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
