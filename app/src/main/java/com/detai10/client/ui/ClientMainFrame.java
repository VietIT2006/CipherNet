package com.detai10.client.ui;

import com.detai10.client.net.UdpClientService;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.core.search.KeywordFinder;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Màn hình làm việc chính của Client (UI-02, UI-03).
 * Cho phép nhập nội dung, chọn chế độ TEXT/BINARY, gửi gói DATA qua {@link SwingWorker}
 * và tự động highlight các vị trí từ khóa "HELLO" trả về từ Server.
 */
public class ClientMainFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0x19, 0x76, 0xD2);
    private static final Color SUCCESS_COLOR = new Color(0x38, 0x8E, 0x3C);
    private static final Color WARNING_COLOR = new Color(0xF5, 0x7C, 0x00);
    private static final Color ERROR_COLOR = new Color(0xD3, 0x2F, 0x2F);
    private static final Color BG_COLOR = new Color(0xF5, 0xF5, 0xF5);
    private static final Color HIGHLIGHT_COLOR = new Color(0xFF, 0xF5, 0x9D);

    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 13);

    private final String serverIp;
    private final int serverPort;
    private final UdpClientService clientService;

    private final JTextField fileNameField = new JTextField("data.txt", 22);
    private final JRadioButton textModeRadio = new JRadioButton("Văn bản (TEXT)", true);
    private final JRadioButton binaryModeRadio = new JRadioButton("Nhị phân (BINARY)");
    private final JTextArea contentArea = new JTextArea();
    private final JLabel counterLabel = new JLabel("0 ký tự | 0 / 30000 bytes UTF-8");
    private final JButton sendButton = new JButton("Gửi dữ liệu tới Server");
    private final JButton loadFileButton = new JButton("Chọn file từ máy...");
    private final JButton clearButton = new JButton("Xóa trắng");
    private final JButton changeServerButton = new JButton("Đổi Server");
    private final JTextArea resultArea = new JTextArea(4, 40);
    private final JLabel statusLabel = new JLabel("Sẵn sàng gửi dữ liệu.");

    public ClientMainFrame(String serverIp, int serverPort, UdpClientService clientService) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientService = clientService != null ? clientService : new UdpClientService();

        setTitle("Vigen UDP Client — Truyền & Phân tích dữ liệu (" + serverIp + ":" + serverPort + ")");
        setSize(850, 600);
        setMinimumSize(new Dimension(720, 500));
        setLocationRelativeTo(null);

        initComponents();
        attachEventHandlers();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(BG_COLOR);

        // 1. Phần trên: Thông tin kết nối & Cấu hình file (North)
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin kết nối & Cấu hình File"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel connLabel = new JLabel("Server đang kết nối: " + serverIp + ":" + serverPort);
        connLabel.setFont(HEADER_FONT);
        connLabel.setForeground(SUCCESS_COLOR);
        topPanel.add(connLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        changeServerButton.setFont(UI_FONT);
        topPanel.add(changeServerButton, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel fileLabel = new JLabel("Tên file trên Server:");
        fileLabel.setFont(UI_FONT);
        fileLabel.setMinimumSize(fileLabel.getPreferredSize());
        topPanel.add(fileLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fileNameField.setFont(UI_FONT);
        topPanel.add(fileNameField, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(textModeRadio);
        modeGroup.add(binaryModeRadio);
        textModeRadio.setFont(UI_FONT);
        textModeRadio.setBackground(Color.WHITE);
        binaryModeRadio.setFont(UI_FONT);
        binaryModeRadio.setBackground(Color.WHITE);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(textModeRadio);
        radioPanel.add(binaryModeRadio);
        gbc.gridwidth = 2;
        topPanel.add(radioPanel, gbc);

        root.add(topPanel, BorderLayout.NORTH);

        // 2. Phần giữa: Soạn thảo văn bản & Thanh công cụ (Center)
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Nội dung văn bản nguồn (UTF-8)"));

        contentArea.setFont(MONO_FONT);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        JPanel toolBar = new JPanel(new BorderLayout(8, 0));
        toolBar.setBackground(Color.WHITE);
        toolBar.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        leftTools.setBackground(Color.WHITE);
        loadFileButton.setFont(UI_FONT);
        clearButton.setFont(UI_FONT);
        counterLabel.setFont(UI_FONT);
        counterLabel.setForeground(Color.DARK_GRAY);
        leftTools.add(loadFileButton);
        leftTools.add(clearButton);
        leftTools.add(counterLabel);

        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        sendButton.setOpaque(true);
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new EmptyBorder(8, 18, 8, 18));
        sendButton.setFocusPainted(false);

        toolBar.add(leftTools, BorderLayout.CENTER);
        toolBar.add(sendButton, BorderLayout.EAST);
        centerPanel.add(toolBar, BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);

        // 3. Phần dưới: Kết quả phản hồi từ Server (South)
        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Kết quả xử lý & Vị trí từ khóa \"HELLO\""));

        resultArea.setFont(MONO_FONT);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        root.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void attachEventHandlers() {
        textModeRadio.addActionListener(e -> {
            if ("data.dat".equals(fileNameField.getText().trim())) {
                fileNameField.setText("data.txt");
            }
        });
        binaryModeRadio.addActionListener(e -> {
            if ("data.txt".equals(fileNameField.getText().trim())) {
                fileNameField.setText("data.dat");
            }
        });

        contentArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCounterAndClearHighlights();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCounterAndClearHighlights();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCounterAndClearHighlights();
            }
        });

        loadFileButton.addActionListener(e -> chooseFileFromDisk());
        clearButton.addActionListener(e -> {
            contentArea.setText("");
            resultArea.setText("");
            contentArea.getHighlighter().removeAllHighlights();
        });
        changeServerButton.addActionListener(e -> {
            new ConnectFrame(clientService).setVisible(true);
            dispose();
        });
        sendButton.addActionListener(e -> sendDataToServer());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        ClientMainFrame.this,
                        "Bạn có chắc chắn muốn thoát ứng dụng Client không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private void updateCounterAndClearHighlights() {
        contentArea.getHighlighter().removeAllHighlights();
        String text = contentArea.getText();
        int bytes = text.getBytes(StandardCharsets.UTF_8).length;
        counterLabel.setText(text.length() + " ký tự | " + bytes + " / " + UdpProtocol.MAX_PAYLOAD_BYTES + " bytes UTF-8");
        counterLabel.setForeground(bytes > UdpProtocol.MAX_PAYLOAD_BYTES ? ERROR_COLOR : Color.DARK_GRAY);
    }

    static boolean isFileSizeAllowed(File file) {
        return file != null && file.isFile() && file.length() <= UdpProtocol.MAX_PAYLOAD_BYTES;
    }

    private void chooseFileFromDisk() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (!isFileSizeAllowed(selected)) {
                JOptionPane.showMessageDialog(this,
                        "Kích thước file (" + selected.length() + " bytes) vượt quá giới hạn an toàn "
                                + UdpProtocol.MAX_PAYLOAD_BYTES + " bytes!",
                        "File quá lớn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String content = Files.readString(selected.toPath(), StandardCharsets.UTF_8);
                contentArea.setText(content);
                fileNameField.setText(selected.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể đọc file: " + ex.getMessage(),
                        "Lỗi đọc file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendDataToServer() {
        String rawFileName = fileNameField.getText();
        String content = contentArea.getText();
        FileMode mode = binaryModeRadio.isSelected() ? FileMode.BINARY : FileMode.TEXT;

        if (content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nội dung văn bản không được để trống!",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String safeFileName;
        try {
            safeFileName = UdpProtocol.sanitizeFileName(rawFileName);
            UdpProtocol.createDataMessage(mode, safeFileName, content);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setSendingState(true);

        SwingWorker<UdpProtocol.ResponsePayload, Void> worker = new SwingWorker<>() {
            @Override
            protected UdpProtocol.ResponsePayload doInBackground() throws Exception {
                return clientService.sendData(serverIp, serverPort, mode, safeFileName, content);
            }

            @Override
            protected void done() {
                setSendingState(false);
                try {
                    UdpProtocol.ResponsePayload response = get();
                    if (response.isError()) {
                        resultArea.setText("LỖI TỪ SERVER:\n" + response.getMessage());
                        statusLabel.setText("Server báo lỗi xử lý.");
                        statusLabel.setForeground(ERROR_COLOR);
                        return;
                    }

                    resultArea.setText("Trạng thái: " + response.getStatus()
                            + "\nThông điệp Server: " + response.getMessage()
                            + "\nChế độ lưu: " + mode.name() + " | File: " + safeFileName);

                    highlightHelloKeywords(content);
                    statusLabel.setText("Hoàn tất nhận phản hồi từ Server.");
                    statusLabel.setForeground(response.isFound() ? SUCCESS_COLOR : WARNING_COLOR);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    String msg = (cause instanceof SocketTimeoutException)
                            ? "Hết thời gian chờ (5 giây): Server không phản hồi gói RESULT."
                            : "Lỗi truyền nhận UDP: " + cause.getMessage();
                    resultArea.setText(msg);
                    statusLabel.setText(msg);
                    statusLabel.setForeground(ERROR_COLOR);
                    JOptionPane.showMessageDialog(ClientMainFrame.this, msg,
                            "Lỗi mạng UDP", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    void setSendingState(boolean sending) {
        sendButton.setEnabled(!sending);
        loadFileButton.setEnabled(!sending);
        clearButton.setEnabled(!sending);
        changeServerButton.setEnabled(!sending);
        contentArea.setEditable(!sending);
        fileNameField.setEnabled(!sending);
        if (sending) {
            statusLabel.setText("Đang gửi gói DATA và chờ phản hồi từ Server...");
            statusLabel.setForeground(WARNING_COLOR);
        }
    }

    void highlightHelloKeywords(String text) {
        Highlighter highlighter = contentArea.getHighlighter();
        highlighter.removeAllHighlights();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR);

        List<Integer> indices = KeywordFinder.findHelloIndices(text);
        for (int index : indices) {
            try {
                highlighter.addHighlight(index, index + KeywordFinder.DEFAULT_KEYWORD.length(), painter);
            } catch (BadLocationException ignored) {
            }
        }
    }
}
