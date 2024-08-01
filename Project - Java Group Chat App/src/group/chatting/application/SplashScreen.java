package group.chatting.application;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setSize(528, 500);
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove window decorations
        setLayout(new BorderLayout());

        ImageIcon splashIcon = new ImageIcon("E:\\Intellij\\Group Chatting Application\\src\\icons\\banner.png");
        JLabel splashLabel = new JLabel(splashIcon);
        add(splashLabel, BorderLayout.CENTER);

        // Show the splash screen for 2 seconds before closing
        int duration = 2000;
        Timer timer = new Timer(duration, e -> {
            setVisible(false);
            dispose(); // Close the splash screen
            showMainGUI();
        });
        timer.setRepeats(false); // Only run once
        timer.start();
    }

    private void showMainGUI() {
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

    public static void main(String[] args) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setVisible(true);
    }
}
