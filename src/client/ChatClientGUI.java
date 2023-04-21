package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientGUI extends JFrame {
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;
    private String name;

    public ChatClientGUI(String name, String serverName, int port) {
        super(name);
        this.name = name;

        try {
            socket = new Socket(serverName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());
            out.println(name);
        } catch (IOException e) {
            System.err.println("Error connecting to chat server: " + e.getMessage());
            System.exit(1);
        }

        initUI();
        receiveMessages();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        panel.add(chatScrollPane, BorderLayout.CENTER);

        messageField = new JTextField(30);
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(messageField, BorderLayout.SOUTH);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton, BorderLayout.EAST);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        messageField.setText("");
    }

    private void receiveMessages() {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner in = null;
                try {
                    in = new Scanner(socket.getInputStream());
                } catch (IOException e) {
                    System.err.println("Error getting input stream: " + e.getMessage());
                    System.exit(1);
                }

                while (in.hasNextLine()) {
                    String message = in.nextLine();
                    chatArea.append(message + "\n");
                }
            }
        });
        receiveThread.start();
    }
}
