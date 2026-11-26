package com.detai10.client.ui;

import com.detai10.client.net.UdpClientService;
import com.detai10.core.protocol.UdpProtocol;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.SocketTimeoutException;

/**
 * Màn hình Kết nối ban đầu của Client (UI-01).
 * Nhập IP/Port Server, gửi gói PING kiểm tra qua {@link UdpClientService} với timeout 3 giây.
 */
public class ConnectFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0x19, 0x76, 0xD2);
    private static final Color SUCCESS_COLOR = new Color(0x38, 0x8E, 0x3C);
    private static final Color WARNING_COLOR = new Color(0xF5, 0x7C, 0x00);
    private static final Color ERROR_COLOR = new Color(0xD3, 0x2F, 0x2F);
    private static final Color BG_COLOR = new Color(0xF5, 0xF5, 0xF5);

    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);

    private final JTextField ipField = new JTextField("127.0.0.1", 18);
    private final JTextField portField = new JTextField(String.valueOf(UdpProtocol.DEFAULT_PORT), 18);
    private final JButton connectButton = new JButton("Kết nối");
    private final JButton exitButton = new JButton("Thoát");
    private final JLabel statusLabel = new JLabel("Nhập IP và Cổng Server để kiểm tra kết nối (PING).");

    private final UdpClientService clientService;

    public ConnectFrame() {
        this(new UdpClientService());
    }

    public ConnectFrame(UdpClientService clientService) {
        this.clientService = clientService;

        setTitle("Vigen UDP Client — Kết nối Máy chủ");
        setSize(420, 260);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        attachEventHandlers();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 16, 14, 16));
        root.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("Vigen UDP Client — Kết nối Máy chủ");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        root.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel ipLabel = new JLabel("IP Máy chủ:");
        ipLabel.setFont(UI_FONT);
        formPanel.add(ipLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        ipField.setFont(UI_FONT);
        formPanel.add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel portLabel = new JLabel("Cổng (Port):");
        portLabel.setFont(UI_FONT);
        formPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        portField.setFont(UI_FONT);
        formPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(statusLabel, gbc);

        root.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(BG_COLOR);

        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        connectButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        connectButton.setOpaque(true);
        connectButton.setBackground(PRIMARY_COLOR);
        connectButton.setForeground(Color.WHITE);
        connectButton.setBorder(new EmptyBorder(7, 18, 7, 18));
        connectButton.setFocusPainted(false);

        exitButton.setFont(UI_FONT);

        buttonPanel.add(exitButton);
        buttonPanel.add(connectButton);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(connectButton);
    }

    private void attachEventHandlers() {
        connectButton.addActionListener(e -> performPingConnect());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void performPingConnect() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ IP của Server.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cổng (Port) phải là số nguyên từ 1 đến 65535.",
                    "Sai định dạng Port", JOptionPane.ERROR_MESSAGE);
            return;
        }

        connectButton.setEnabled(false);
        ipField.setEnabled(false);
        portField.setEnabled(false);
        statusLabel.setText("Đang gửi PING tới " + ip + ":" + port + "... (chờ tối đa 3 giây)");
        statusLabel.setForeground(WARNING_COLOR);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return clientService.ping(ip, port);
            }

            @Override
            protected void done() {
                try {
                    boolean pongReceived = get();
                    if (pongReceived) {
                        statusLabel.setText("Kết nối Server thành công!");
                        statusLabel.setForeground(SUCCESS_COLOR);
                        JOptionPane.showMessageDialog(ConnectFrame.this,
                                "Kết nối Server thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        ClientMainFrame mainFrame = new ClientMainFrame(ip, port, clientService);
                        mainFrame.setVisible(true);
                        dispose();
                    } else {
                        showFailure("Server phản hồi không đúng định dạng PONG.");
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof SocketTimeoutException) {
                        showFailure("Quá thời gian chờ (3 giây): Server không phản hồi gói PONG.\nVui lòng kiểm tra Server đã bật chưa hoặc đúng IP/Port.");
                    } else {
                        showFailure("Lỗi kết nối UDP: " + cause.getMessage());
                    }
                }
            }
        };
        worker.execute();
    }

    private void showFailure(String message) {
        statusLabel.setText("Kết nối thất bại. Vui lòng thử lại.");
        statusLabel.setForeground(ERROR_COLOR);
        connectButton.setEnabled(true);
        ipField.setEnabled(true);
        portField.setEnabled(true);
        JOptionPane.showMessageDialog(this, message, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
    }
}
