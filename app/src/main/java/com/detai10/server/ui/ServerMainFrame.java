package com.detai10.server.ui;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.server.worker.UdpServerWorker;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Giao diện Quản trị Server (SRV-01, SRV-03).
 * Tuân thủ Quy tắc 2 & Quy tắc 7: Không dùng setLayout(null), cập nhật UI qua SwingUtilities.invokeLater().
 */
public class ServerMainFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0x19, 0x76, 0xD2);
    private static final Color SUCCESS_COLOR = new Color(0x38, 0x8E, 0x3C);
    private static final Color ERROR_COLOR = new Color(0xD3, 0x2F, 0x2F);
    private static final Color BG_COLOR = new Color(0xF5, 0xF5, 0xF5);

    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font LOG_FONT = new Font("Consolas", Font.PLAIN, 12);

    private final JTextField portField = new JTextField(String.valueOf(UdpProtocol.DEFAULT_PORT), 8);
    private final JTextField keyField = new JTextField(VigenereCipher.DEFAULT_KEY, 16);
    private final JButton startButton = new JButton("Khởi động (Start)");
    private final JButton stopButton = new JButton("Dừng (Stop)");
    private final JLabel statusLabel = new JLabel("● OFFLINE — Đã dừng");

    private final DefaultTableModel historyTableModel;
    private final JTable historyTable;
    private final JTextArea consoleArea = new JTextArea();

    private final DatabaseHelper dbHelper;
    private UdpServerWorker worker;

    public ServerMainFrame() {
        this.dbHelper = DatabaseHelper.getInstance();

        setTitle("Vigen UDP Server — Quản trị & Giám sát (Đề tài 10)");
        setSize(950, 650);
        setMinimumSize(new java.awt.Dimension(800, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"ID", "Thời gian", "IP Client", "Tên File", "Chế độ", "Nội dung gốc", "Nội dung mã hóa", "Vị trí HELLO"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);

        initComponents();
        attachEventHandlers();
        loadInitialHistory();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(BG_COLOR);

        // 1. Thanh cấu hình Server (North)
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình Server UDP & Khóa Vigenère"));
        configPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Server Control");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        configPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel portLabel = new JLabel("Cổng UDP:");
        portLabel.setFont(UI_FONT);
        portLabel.setMinimumSize(portLabel.getPreferredSize());
        configPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        portField.setFont(UI_FONT);
        portField.setMinimumSize(portField.getPreferredSize());
        configPanel.add(portField, gbc);

        gbc.gridx = 2;
        JLabel keyLabel = new JLabel("Khóa Vigenère:");
        keyLabel.setFont(UI_FONT);
        keyLabel.setMinimumSize(keyLabel.getPreferredSize());
        configPanel.add(keyLabel, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        keyField.setFont(UI_FONT);
        configPanel.add(keyField, gbc);

        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        startButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        startButton.setOpaque(true);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(new EmptyBorder(6, 14, 6, 14));
        startButton.setFocusPainted(false);
        configPanel.add(startButton, gbc);

        gbc.gridx = 5;
        stopButton.setFont(UI_FONT);
        stopButton.setEnabled(false);
        configPanel.add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(ERROR_COLOR);
        configPanel.add(statusLabel, gbc);

        root.add(configPanel, BorderLayout.NORTH);

        // 2. Khu vực Tab Lịch sử & Console Log (Center)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UI_FONT);

        historyTable.setFont(UI_FONT);
        historyTable.setRowHeight(24);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("Lịch sử giao dịch (CSDL)", new JScrollPane(historyTable));

        consoleArea.setFont(LOG_FONT);
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);
        consoleArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        tabbedPane.addTab("Console Log chi tiết", new JScrollPane(consoleArea));

        root.add(tabbedPane, BorderLayout.CENTER);

        // 3. Thanh nút chức năng phụ (South)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footerPanel.setBackground(BG_COLOR);

        JButton refreshBtn = new JButton("Làm mới bảng");
        refreshBtn.setFont(UI_FONT);
        refreshBtn.addActionListener(e -> loadInitialHistory());

        JButton clearLogBtn = new JButton("Xóa console log");
        clearLogBtn.setFont(UI_FONT);
        clearLogBtn.addActionListener(e -> consoleArea.setText(""));

        footerPanel.add(refreshBtn);
        footerPanel.add(clearLogBtn);
        root.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void attachEventHandlers() {
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        ServerMainFrame.this,
                        "Bạn có chắc chắn muốn đóng Server UDP không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (worker != null) {
                        worker.stop();
                    }
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cổng UDP phải là số nguyên từ 1 đến 65535.",
                    "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String normalizedKey;
        try {
            normalizedKey = VigenereCipher.normalizeKey(keyField.getText());
            keyField.setText(normalizedKey);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Khóa Vigenère không hợp lệ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        worker = new UdpServerWorker(
                port,
                normalizedKey,
                new File("server_storage"),
                dbHelper,
                this::appendLogAsync,
                this::prependRecordAsync
        );

        try {
            worker.start();
            updateRunningState(true, port);
        } catch (SocketException ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở cổng UDP " + port + ": " + ex.getMessage(),
                    "Lỗi khởi động Server", JOptionPane.ERROR_MESSAGE);
            updateRunningState(false, port);
        }
    }

    private void stopServer() {
        if (worker != null) {
            worker.stop();
        }
        updateRunningState(false, 0);
    }

    private void updateRunningState(boolean running, int port) {
        portField.setEnabled(!running);
        keyField.setEnabled(!running);
        startButton.setEnabled(!running);
        startButton.setBackground(running ? new Color(0xB0, 0xBE, 0xC5) : PRIMARY_COLOR);
        stopButton.setEnabled(running);

        if (running) {
            statusLabel.setText("● ONLINE — Đang lắng nghe trên cổng " + port);
            statusLabel.setForeground(SUCCESS_COLOR);
        } else {
            statusLabel.setText("● OFFLINE — Đã dừng");
            statusLabel.setForeground(ERROR_COLOR);
        }
    }

    private void loadInitialHistory() {
        List<HistoryRecord> records = dbHelper.getRecentHistory(100);
        SwingUtilities.invokeLater(() -> {
            historyTableModel.setRowCount(0);
            for (HistoryRecord rec : records) {
                historyTableModel.addRow(toTableRow(rec));
            }
        });
    }

    public boolean isServerRunning() {
        return worker != null && worker.isRunning();
    }

    public void appendLog(String message) {
        appendLogAsync(message);
    }

    public void clearHistoryRows() {
        SwingUtilities.invokeLater(() -> historyTableModel.setRowCount(0));
    }

    private void appendLogAsync(String message) {
        SwingUtilities.invokeLater(() -> consoleArea.append(message + "\n"));
    }

    private void prependRecordAsync(HistoryRecord record) {
        SwingUtilities.invokeLater(() -> historyTableModel.insertRow(0, toTableRow(record)));
    }

    private Object[] toTableRow(HistoryRecord rec) {
        String timeStr = rec.getCreatedAt() != null
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rec.getCreatedAt())
                : "Mới";
        return new Object[]{
                rec.getId(),
                timeStr,
                rec.getClientIp(),
                rec.getFileName(),
                rec.getIoMode(),
                truncate(rec.getOriginalContent(), 40),
                truncate(rec.getEncryptedContent(), 40),
                rec.getHelloPositions()
        };
    }

    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
