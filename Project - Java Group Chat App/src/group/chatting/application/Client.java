package group.chatting.application;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends JFrame implements ActionListener {
    private JTextField textField;
    private JTextPane chatArea;
    private JButton sendButton;
    private JButton addButton;
    private String userName;
    private BufferedWriter writer;

    public Client(String userName, BufferedWriter writer) {
        this.userName = userName;
        this.writer = writer;

        setTitle("ChatterHub");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon("E:\\Intellij\\Group Chatting Application\\src\\icons\\logo2.png");
        setIconImage(icon.getImage());

        JPanel topPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(129, 137, 98));
        sendButton.setForeground(Color.BLACK);
        sendButton.setFont(new Font("Arial", Font.BOLD, 20));
        sendButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        sendButton.addActionListener(this);

        topPanel.add(textField, BorderLayout.CENTER);
        topPanel.add(sendButton, BorderLayout.EAST);

        addButton = new JButton("+");
        addButton.setBackground(new Color(129, 137, 98));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 30));
        addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        addButton.addActionListener(this);
        topPanel.add(addButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        topPanel.setBackground(new Color(41, 38, 39));

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(41, 38, 39));
        chatArea.setForeground(Color.BLACK);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Enter your message: "), BorderLayout.WEST);
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String message = textField.getText();
            sendMessage(userName + ": " + message);
            textField.setText("");
        } else if (e.getSource() == addButton) {
            String newUserName = JOptionPane.showInputDialog("Enter your name:");
            if (newUserName != null && !newUserName.isEmpty()) {
                try {
                    Socket socket = new Socket("localhost", 2003);
                    BufferedWriter newWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    newWriter.write(newUserName);
                    newWriter.newLine();
                    newWriter.flush();

                    Client newClientGUI = new Client(newUserName, newWriter);
                    Thread receiveThread = new Thread(() -> {
                        try {
                            while (true) {
                                String message = reader.readLine();
                                if (message != null) {
                                    newClientGUI.displayMessage(message);
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    receiveThread.start();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void displayMessage(String message) {
        // Create a style for the blue box
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        // Light blue background color

        // Format current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = "[" + dateFormat.format(new Date()) + "] ";

        if (message.startsWith(userName + ":")) {
            StyleConstants.setBackground(attributeSet, new Color(240, 183, 54));
            appendToPane(chatArea, "You: " + timestamp + message.substring(userName.length() + 1) + "\n\n", attributeSet);
        } else {
            StyleConstants.setBackground(attributeSet, new Color(95, 134, 113));
            appendToPane(chatArea, timestamp + message + "\n\n", attributeSet);
        }
    }

    // Helper method to append text with specified style to JTextPane
    private void appendToPane(JTextPane tp, String msg, AttributeSet attributeSet) {
        tp.setEditable(true);
        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(attributeSet, false);
        tp.replaceSelection(msg);
        tp.setEditable(false);
    }

    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String userName = JOptionPane.showInputDialog("Enter your name:");
        if (userName != null && !userName.isEmpty()) {
            try {
                Socket socket = new Socket("localhost", 2003);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.write(userName);
                writer.newLine();
                writer.flush();

                Client clientGUI = new Client(userName, writer);
                Thread receiveThread = new Thread(() -> {
                    try {
                        while (true) {
                            String message = reader.readLine();
                            if (message != null) {
                                clientGUI.displayMessage(message);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                receiveThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

