package com.detai10.server.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Giao diện đồ họa chính (GUI) của ứng dụng UDP Server (SRV-01).
 * <p>
 * Tính năng chính:
 * 1. Bảng điều khiển cấu hình: Nhập Port lắng nghe (mặc định 9876), Khóa Vigenère bí mật, Nút Start / Stop Server.
 * 2. Đèn báo trạng thái trực quan: Badge LED hiển thị ONLINE (xanh lá) / OFFLINE (đỏ).
 * 3. Bảng JTable Lịch sử xử lý: Hiển thị đầy đủ thông tin giao dịch UDP.
 *    - Hỗ trợ nhấp đúp xem chi tiết toàn văn bản gốc & mã hóa trong hộp thoại riêng.
 *    - Nút làm mới dữ liệu và xóa trắng bảng lịch sử.
 * 4. Khung Console Log: Ghi vết thời gian thực mọi sự kiện nhận / phản hồi gói tin UDP kèm tự động cuộn.
 * 5. Thanh trạng thái phía dưới (Status Bar): Thống kê số gói tin đã xử lý và trạng thái hệ thống.
 * <p>
 * Kiến trúc phân tầng & Đa luồng:
 * - Tuân thủ Quy tắc 1.1 & 1.2: Không chặn EDT, cập nhật UI bọc trong {@code SwingUtilities.invokeLater()}.
 * - Tuân thủ Quy tắc 2.2: Tuyệt đối không dùng {@code setLayout(null)}, dùng Layout Managers tiêu chuẩn.
 * - Hỗ trợ cơ chế {@link ServerController} để gắn kết linh hoạt với luồng nền Worker mà không gây phụ thuộc cứng.
 * - Graceful Shutdown: Hộp thoại xác nhận đóng ứng dụng an toàn.
 */
public class ServerMainFrame extends JFrame {

    public static final int DEFAULT_PORT = 9876;
    public static final String DEFAULT_KEY = "KEYSECRET";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Bảng màu giao diện chuẩn (Design Tokens)
    private static final Color COLOR_PRIMARY = new Color(25, 118, 210);    // Xanh dương đậm
    private static final Color COLOR_SUCCESS = new Color(46, 125, 50);     // Xanh lá (Online)
    private static final Color COLOR_DANGER = new Color(211, 47, 47);      // Đỏ (Offline / Stop)
    private static final Color COLOR_BG_PANEL = new Color(248, 249, 250);   // Nền panel nhẹ
    private static final Color COLOR_TEXT_MUTED = new Color(108, 117, 125);// Chữ xám mờ

    // Phông chữ chuẩn hỗ trợ tiếng Việt UTF-8
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CONSOLE = new Font("Consolas", Font.PLAIN, 12);

    // Thành phần điều khiển cấu hình (North)
    private JTextField portField;
    private JTextField keyField;
    private JButton startButton;
    private JButton stopButton;
    private StatusBadge statusBadge;

    // Thành phần Tab 1: Lịch sử xử lý (JTable)
    private DefaultTableModel historyTableModel;
    private JTable historyTable;
    private JLabel totalRecordsLabel;
    private final List<RecordDetailData> recordDataStore = new ArrayList<>();

    // Thành phần Tab 2: Console Log
    private JTextArea logTextArea;
    private JLabel logLinesLabel;
    private JCheckBox autoScrollCheckBox;

    // Thanh trạng thái phía dưới (South)
    private JLabel statusMessageLabel;
    private JLabel statsLabel;

    // Đếm số lượng
    private final AtomicInteger processedPacketCount = new AtomicInteger(0);
    private final AtomicInteger logLineCount = new AtomicInteger(0);
    private volatile boolean isServerRunning = false;

    // Interface mở rộng cho controller kết nối luồng nền
    private ServerController serverController;

    /**
     * Interface trừu tượng hóa bộ điều khiển Server, cho phép cắm ghép luồng UDP nền linh hoạt.
     */
    public interface ServerController {
        void start(int port, String key) throws Exception;
        void stop() throws Exception;
        boolean isRunning();
    }

    public ServerMainFrame() {
        initWindowProperties();
        initComponents();
    }

    /**
     * Thiết lập bộ điều khiển Server tùy chọn từ bên ngoài (phục vụ kết nối UdpServerWorker khi ghép nối).
     */
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public ServerController getServerController() {
        return serverController;
    }

    private void initWindowProperties() {
        setTitle("Vigen UDP Server — Hệ Thống Quản Trị & Giám Sát Mạng (Đề tài 10)");
        setSize(980, 680);
        setMinimumSize(new Dimension(820, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 8));
        rootPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        rootPanel.setBackground(Color.WHITE);

        // 1. Vùng Header & Bảng điều khiển cấu hình (North)
        rootPanel.add(createHeaderAndControlPanel(), BorderLayout.NORTH);

        // 2. Vùng Trung tâm: Tab Lịch sử & Tab Console Log (Center)
        rootPanel.add(createCenterTabbedPane(), BorderLayout.CENTER);

        // 3. Vùng Chân trang: Thanh trạng thái hệ thống (South)
        rootPanel.add(createStatusBarPanel(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel createHeaderAndControlPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setOpaque(false);

        // Banner tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("MÁY CHỦ QUẢN TRỊ TRUYỀN NHẬN UDP & MÃ HÓA VIGENÈRE");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_PRIMARY);

        JLabel subTitleLabel = new JLabel("Đề tài 10: Xử lý gói tin UDP, mã hóa Vigenère bảo toàn ký tự & lưu vết CSDL");
        subTitleLabel.setFont(FONT_REGULAR);
        subTitleLabel.setForeground(COLOR_TEXT_MUTED);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);

        // Thanh công cụ cấu hình Server
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(COLOR_BG_PANEL);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Cổng lắng nghe
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel portLabel = new JLabel("Cổng (Port):");
        portLabel.setFont(FONT_HEADER);
        controlPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        portField = new JTextField(String.valueOf(DEFAULT_PORT), 5);
        portField.setFont(FONT_REGULAR);
        portField.setToolTipText("Cổng UDP Server lắng nghe (1024 - 65535)");
        controlPanel.add(portField, gbc);

        // Khóa Vigenère
        gbc.gridx = 2;
        JLabel keyLabel = new JLabel("Khóa Vigenère:");
        keyLabel.setFont(FONT_HEADER);
        controlPanel.add(keyLabel, gbc);

        gbc.gridx = 3;
        keyField = new JTextField(DEFAULT_KEY, 10);
        keyField.setFont(FONT_REGULAR);
        keyField.setToolTipText("Khóa bí mật mã hóa / giải mã Vigenère trên Server");
        controlPanel.add(keyField, gbc);

        // Nút Bắt đầu
        gbc.gridx = 4;
        startButton = new JButton("▶ Khởi động (Start)");
        startButton.setFont(FONT_HEADER);
        startButton.setBackground(COLOR_PRIMARY);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startServer());
        controlPanel.add(startButton, gbc);

        // Nút Dừng
        gbc.gridx = 5;
        stopButton = new JButton("⏹ Dừng (Stop)");
        stopButton.setFont(FONT_HEADER);
        stopButton.setBackground(new Color(230, 230, 230));
        stopButton.setForeground(Color.DARK_GRAY);
        stopButton.setEnabled(false);
        stopButton.setFocusPainted(false);
        stopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        stopButton.addActionListener(e -> stopServer());
        controlPanel.add(stopButton, gbc);

        // Đèn báo trạng thái (LED Badge)
        gbc.gridx = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        statusBadge = new StatusBadge(false, 0);
        controlPanel.add(statusBadge, gbc);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(controlPanel, BorderLayout.CENTER);
        return container;
    }

    private JTabbedPane createCenterTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_HEADER);

        // Tab 1: Lịch sử xử lý (JTable)
        tabbedPane.addTab("📋 Lịch sử giao dịch", createHistoryPanel());

        // Tab 2: Console Log thời gian thực
        tabbedPane.addTab("🖥 Nhật ký mạng (Console Log)", createConsoleLogPanel());

        return tabbedPane;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        String[] columnNames = {
                "STT", "Thời gian", "IP Client", "Tên File", "Chế độ",
                "Nội dung gốc (trích đoạn)", "Nội dung mã hóa", "Vị trí HELLO"
        };

        historyTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(historyTableModel);
        historyTable.setFont(FONT_REGULAR);
        historyTable.setRowHeight(26);
        historyTable.getTableHeader().setFont(FONT_HEADER);
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(210);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(210);
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(95);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && historyTable.getSelectedRow() != -1) {
                    showRecordDetailsDialog(historyTable.getSelectedRow());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Toolbar phía dưới
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftActions.setOpaque(false);

        JButton clearButton = new JButton("🗑 Xóa lịch sử hiển thị");
        clearButton.setFont(FONT_REGULAR);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearHistoryRows());
        leftActions.add(clearButton);

        JLabel tipLabel = new JLabel("(Nhấp đúp vào dòng để xem chi tiết toàn văn bản)");
        tipLabel.setFont(FONT_REGULAR);
        tipLabel.setForeground(COLOR_TEXT_MUTED);
        leftActions.add(tipLabel);

        totalRecordsLabel = new JLabel("Tổng số bản ghi: 0");
        totalRecordsLabel.setFont(FONT_HEADER);
        totalRecordsLabel.setForeground(COLOR_PRIMARY);

        toolbar.add(leftActions, BorderLayout.WEST);
        toolbar.add(totalRecordsLabel, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createConsoleLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        logTextArea = new JTextArea();
        logTextArea.setFont(FONT_CONSOLE);
        logTextArea.setEditable(false);
        logTextArea.setBackground(new Color(245, 247, 250));
        logTextArea.setForeground(new Color(33, 37, 41));
        logTextArea.setLineWrap(false);

        DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftControls.setOpaque(false);

        JButton clearLogBtn = new JButton("🧹 Xóa nhật ký");
        clearLogBtn.setFont(FONT_REGULAR);
        clearLogBtn.setFocusPainted(false);
        clearLogBtn.addActionListener(e -> {
            logTextArea.setText("");
            logLineCount.set(0);
            updateLogLinesDisplay();
        });
        leftControls.add(clearLogBtn);

        JButton copyLogBtn = new JButton("📋 Sao chép toàn bộ");
        copyLogBtn.setFont(FONT_REGULAR);
        copyLogBtn.setFocusPainted(false);
        copyLogBtn.addActionListener(e -> {
            String text = logTextArea.getText();
            if (text != null && !text.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "Đã sao chép toàn bộ log vào Clipboard!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        leftControls.add(copyLogBtn);

        autoScrollCheckBox = new JCheckBox("Tự động cuộn xuống cuối (Auto-scroll)", true);
        autoScrollCheckBox.setFont(FONT_REGULAR);
        autoScrollCheckBox.setOpaque(false);
        autoScrollCheckBox.addActionListener(e -> {
            DefaultCaret c = (DefaultCaret) logTextArea.getCaret();
            c.setUpdatePolicy(autoScrollCheckBox.isSelected() ? DefaultCaret.ALWAYS_UPDATE : DefaultCaret.NEVER_UPDATE);
        });
        leftControls.add(autoScrollCheckBox);

        logLinesLabel = new JLabel("Số dòng log: 0");
        logLinesLabel.setFont(FONT_HEADER);
        logLinesLabel.setForeground(COLOR_TEXT_MUTED);

        toolbar.add(leftControls, BorderLayout.WEST);
        toolbar.add(logLinesLabel, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusBarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
                new EmptyBorder(6, 4, 2, 4)
        ));
        panel.setOpaque(false);

        statusMessageLabel = new JLabel("Sẵn sàng khởi động Server.");
        statusMessageLabel.setFont(FONT_REGULAR);
        statusMessageLabel.setForeground(Color.DARK_GRAY);

        statsLabel = new JLabel("Đã xử lý: 0 gói tin | Trạng thái: Sẵn sàng");
        statsLabel.setFont(FONT_REGULAR);
        statsLabel.setForeground(COLOR_TEXT_MUTED);

        panel.add(statusMessageLabel, BorderLayout.WEST);
        panel.add(statsLabel, BorderLayout.EAST);
        return panel;
    }

    // ==================== Các tác vụ Nghiệp vụ & Đa luồng ====================

    /**
     * Khởi động Server: Kiểm tra cổng hợp lệ, chuyển đổi trạng thái giao diện và gọi controller nếu có.
     */
    public void startServer() {
        String portText = portField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1024 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Cổng lắng nghe không hợp lệ! Vui lòng nhập số nguyên từ 1024 đến 65535.",
                    "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            return;
        }

        String key = keyField.getText().trim();
        if (key.isEmpty()) {
            key = DEFAULT_KEY;
            keyField.setText(DEFAULT_KEY);
        }

        int finalPort = port;
        String finalKey = key;

        // Vô hiệu hóa controls
        startButton.setEnabled(false);
        portField.setEnabled(false);
        keyField.setEnabled(false);
        statusMessageLabel.setText("Đang khởi động Server trên cổng " + finalPort + "...");

        new SwingWorker<Boolean, Void>() {
            private String errorMsg;

            @Override
            protected Boolean doInBackground() {
                try {
                    if (serverController != null) {
                        serverController.start(finalPort, finalKey);
                    }
                    return true;
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        isServerRunning = true;
                        stopButton.setEnabled(true);
                        stopButton.setBackground(COLOR_DANGER);
                        stopButton.setForeground(Color.WHITE);
                        statusBadge.setStatus(true, finalPort);
                        statusMessageLabel.setText("Server đang ONLINE và lắng nghe trên cổng " + finalPort);
                        appendLog("[INFO] Server bat dau lang nghe thanh cong tren port " + finalPort + " voi khoa '" + finalKey + "'");
                    } else {
                        revertToStoppedState();
                        JOptionPane.showMessageDialog(ServerMainFrame.this,
                                "Không thể khởi động Server: " + errorMsg,
                                "Lỗi khởi động", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    revertToStoppedState();
                }
            }
        }.execute();
    }

    /**
     * Dừng Server: Dừng luồng nền và cập nhật lại giao diện.
     */
    public void stopServer() {
        stopButton.setEnabled(false);
        statusMessageLabel.setText("Đang dừng Server...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    if (serverController != null) {
                        serverController.stop();
                    }
                } catch (Exception ex) {
                    appendLog("[WARN] Lỗi khi dừng controller: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                isServerRunning = false;
                revertToStoppedState();
                statusMessageLabel.setText("Server đã dừng hoạt động.");
                appendLog("[INFO] Server da dung.");
            }
        }.execute();
    }

    private void revertToStoppedState() {
        startButton.setEnabled(true);
        startButton.setBackground(COLOR_PRIMARY);
        stopButton.setEnabled(false);
        stopButton.setBackground(new Color(230, 230, 230));
        stopButton.setForeground(Color.DARK_GRAY);
        portField.setEnabled(true);
        keyField.setEnabled(true);
        statusBadge.setStatus(false, 0);
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    public void appendLog(String message) {
        String timestamp = TIME_FORMAT.format(new Date());
        logTextArea.append("[" + timestamp + "] " + message + "\n");
        logLineCount.incrementAndGet();
        updateLogLinesDisplay();
    }

    private void updateLogLinesDisplay() {
        logLinesLabel.setText("Số dòng log: " + logLineCount.get());
    }

    /**
     * Thêm một dòng giao dịch vào bảng JTable Lịch sử.
     */
    public void addHistoryRow(String clientIp, String fileName, String fileMode,
                              String originalContent, String encryptedContent, String helloPositions) {
        int stt = historyTableModel.getRowCount() + 1;
        String timeStr = TIME_FORMAT.format(new Date());

        String originalShort = truncateText(originalContent, 35);
        String encryptedShort = truncateText(encryptedContent, 35);

        recordDataStore.add(new RecordDetailData(stt, timeStr, clientIp, fileName, fileMode,
                originalContent, encryptedContent, helloPositions));

        historyTableModel.addRow(new Object[]{
                stt,
                timeStr,
                clientIp,
                fileName,
                fileMode,
                originalShort,
                encryptedShort,
                helloPositions
        });

        int processed = processedPacketCount.incrementAndGet();
        statsLabel.setText("Đã xử lý: " + processed + " gói tin");
        totalRecordsLabel.setText("Tổng số bản ghi: " + historyTableModel.getRowCount());
    }

    public void clearHistoryRows() {
        historyTableModel.setRowCount(0);
        recordDataStore.clear();
        totalRecordsLabel.setText("Tổng số bản ghi: 0");
        appendLog("[INFO] Đã xóa toàn bộ lịch sử hiển thị.");
    }

    private void showRecordDetailsDialog(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= historyTableModel.getRowCount()) {
            return;
        }

        RecordDetailData data;
        if (rowIndex < recordDataStore.size()) {
            data = recordDataStore.get(rowIndex);
        } else {
            data = new RecordDetailData(
                    rowIndex + 1,
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 1)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 2)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 3)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 4)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 5)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 6)),
                    String.valueOf(historyTableModel.getValueAt(rowIndex, 7))
            );
        }

        JDialog dialog = new JDialog(this, "Chi tiết giao dịch UDP (Bản ghi #" + data.stt + ")", true);
        dialog.setSize(650, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin gói tin"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 6, 3, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; infoPanel.add(new JLabel("Thời gian: " + data.time), g);
        g.gridx = 1; infoPanel.add(new JLabel("Địa chỉ IP: " + data.clientIp), g);
        g.gridx = 0; g.gridy = 1; infoPanel.add(new JLabel("Tên file: " + data.fileName), g);
        g.gridx = 1; infoPanel.add(new JLabel("Chế độ: " + data.mode), g);
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        infoPanel.add(new JLabel("Vị trí từ khóa 'HELLO': " + data.positions), g);

        panel.add(infoPanel, BorderLayout.NORTH);

        JTabbedPane contentTabs = new JTabbedPane();
        contentTabs.setFont(FONT_REGULAR);

        JTextArea originalArea = new JTextArea(data.originalContent);
        originalArea.setFont(FONT_CONSOLE);
        originalArea.setEditable(false);
        originalArea.setLineWrap(true);
        originalArea.setWrapStyleWord(true);

        JTextArea encryptedArea = new JTextArea(data.encryptedContent);
        encryptedArea.setFont(FONT_CONSOLE);
        encryptedArea.setEditable(false);
        encryptedArea.setLineWrap(true);
        encryptedArea.setWrapStyleWord(true);

        contentTabs.addTab("Văn bản gốc", new JScrollPane(originalArea));
        contentTabs.addTab("Văn bản mã hóa Vigenère", new JScrollPane(encryptedArea));
        panel.add(contentTabs, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.setFont(FONT_REGULAR);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đóng ứng dụng Server không?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (isServerRunning) {
                stopServer();
            }
            dispose();
            System.exit(0);
        }
    }

    private static String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String clean = text.replace('\n', ' ').replace('\r', ' ');
        if (clean.length() <= maxLength) {
            return clean;
        }
        return clean.substring(0, maxLength) + "...";
    }

    // ==================== Cấu trúc lưu trữ chi tiết dòng ====================

    private static class RecordDetailData {
        final int stt;
        final String time;
        final String clientIp;
        final String fileName;
        final String mode;
        final String originalContent;
        final String encryptedContent;
        final String positions;

        RecordDetailData(int stt, String time, String clientIp, String fileName, String mode,
                         String originalContent, String encryptedContent, String positions) {
            this.stt = stt;
            this.time = time;
            this.clientIp = clientIp;
            this.fileName = fileName;
            this.mode = mode;
            this.originalContent = originalContent;
            this.encryptedContent = encryptedContent;
            this.positions = positions;
        }
    }

    // ==================== Component đồ họa phụ trợ: StatusBadge ====================

    public static class StatusBadge extends JPanel {
        private boolean online;
        private int port;
        private final JLabel label;

        public StatusBadge(boolean online, int port) {
            super(new FlowLayout(FlowLayout.CENTER, 6, 4));
            this.online = online;
            this.port = port;
            setOpaque(false);

            label = new JLabel();
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            updateBadge();
            add(label);
        }

        public void setStatus(boolean online, int port) {
            this.online = online;
            this.port = port;
            updateBadge();
            repaint();
        }

        public boolean isOnline() {
            return online;
        }

        private void updateBadge() {
            if (online) {
                label.setText("● ONLINE (Cổng " + port + ")");
                label.setForeground(new Color(27, 94, 32));
            } else {
                label.setText("● OFFLINE");
                label.setForeground(new Color(183, 28, 28));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (online) {
                g2.setColor(new Color(232, 245, 233));
            } else {
                g2.setColor(new Color(255, 235, 238));
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            if (online) {
                g2.setColor(new Color(165, 214, 167));
            } else {
                g2.setColor(new Color(239, 154, 154));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Math.max(d.width, 140), 28);
        }
    }
}
