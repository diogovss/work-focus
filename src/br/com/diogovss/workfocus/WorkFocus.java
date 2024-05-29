package br.com.diogovss.workfocus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WorkFocus {
    // Variável de controle para parar o trabalho
    private static volatile boolean running = true;
    private static volatile int remainingSeconds;
    private static final String WORK_ACTIVE = "Work in Progress...";
    private static final String WORK_STOPPED = "Work Stopped";
    
    public static void main(String[] args) {
        // Cria o frame principal
        JFrame frame = new JFrame("Work Focus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 230);

        // Cria os botões
        JButton moveMouseButton = new JButton("Init Work");
        JButton stopMouseButton = new JButton("Stop Work");
        stopMouseButton.setEnabled(false);

        // Cria o JLabel para mostrar o estado do trabalho
        JLabel statusLabel = new JLabel(WORK_STOPPED, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 16));

        // Cria o JTextField para entrada de tempo
        JTextField timeField = new JTextField("240", 5);
        JLabel timeLabel = new JLabel("Minutes:");
        
        // Cria o JLabel para mostrar o tempo restante
        JLabel countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Serif", Font.BOLD, 16));

        // Adiciona um ActionListener ao botão de iniciar
        moveMouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int minutes = Integer.parseInt(timeField.getText());
                    if (minutes > 0) {
                        remainingSeconds = minutes * 60;
                        running = true;
                        statusLabel.setText(WORK_ACTIVE);
                        statusLabel.setForeground(Color.RED);
                        moveMouseButton.setEnabled(false);
                        stopMouseButton.setEnabled(true);
                        timeField.setEnabled(false);
                        new Thread(() -> updateCursor(statusLabel, countdownLabel, moveMouseButton, stopMouseButton, timeField)).start();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please, type a number greater than zero.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please, type a valid number.");
                }
            }
        });

        // Adiciona um ActionListener ao botão de parar
        stopMouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
                statusLabel.setText(WORK_STOPPED);
                statusLabel.setForeground(Color.BLACK);
                countdownLabel.setText("");
                stopMouseButton.setEnabled(false);
                moveMouseButton.setEnabled(true);
                timeField.setEnabled(true);
            }
        });

        // Cria um painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(moveMouseButton);
        buttonPanel.add(stopMouseButton);

        // Cria um painel para a entrada de tempo
        JPanel timePanel = new JPanel();
        timePanel.add(timeLabel);
        timePanel.add(timeField);

        // Cria um painel principal com GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Adiciona o painel da entrada de tempo ao painel principal
        mainPanel.add(timePanel, gbc);

        // Configura o GridBagConstraints para o painel dos botões
        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        // Configura o GridBagConstraints para o statusLabel
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(statusLabel, gbc);

        // Configura o GridBagConstraints para o countdownLabel
        gbc.gridy = 3;
        mainPanel.add(countdownLabel, gbc);

        // Adiciona o painel principal ao frame
        frame.getContentPane().add(mainPanel);

        // Exibe a interface gráfica
        frame.setVisible(true);
    }

    // Método para atualizar o cursor
    public static void updateCursor(JLabel statusLabel, JLabel countdownLabel, JButton moveMouseButton, JButton stopMouseButton, JTextField timeField) {
        try {
            Robot robot = new Robot();
            
            while (running && remainingSeconds > 0) {
            	int x = 0;
            	int y = MouseInfo.getPointerInfo().getLocation().y;
            	
            	if (remainingSeconds % 2 == 0 ) {
            		x = MouseInfo.getPointerInfo().getLocation().x + 1;
            	}
            	else {
            		x = MouseInfo.getPointerInfo().getLocation().x - 1;
            	}
            	
                robot.mouseMove(x, y);
                updateCountdown(countdownLabel);
                Thread.sleep(1000);
                
                if (running) {
                    remainingSeconds--;
                }
            }

            // Atualiza o status quando o movimento do cursor parar
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(WORK_STOPPED);
                statusLabel.setForeground(Color.BLACK);
                countdownLabel.setText("");
                moveMouseButton.setEnabled(true);
                stopMouseButton.setEnabled(false);
                timeField.setEnabled(true);
                timeField.requestFocus();
            });
            
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar o contador regressivo
    public static void updateCountdown(JLabel countdownLabel) {
        SwingUtilities.invokeLater(() -> {
        	String message = "Remaining Time: " + remainingSeconds / 60 + " minutes";
        	
        	if (remainingSeconds <= 120) {
        		message = "Remaining Time: " + remainingSeconds + " seconds";
        		if (remainingSeconds <= 1) {
        			message = "Remaining Time: " + remainingSeconds + " second";	
            	}
        	}
            countdownLabel.setText(message);
        });
    }
}

