import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HouseholdBudget extends JFrame implements ActionListener {
    private JButton startButton, stopButton, clearButton, carButton, jointIncomeButton, childSupportButton, gezButton;
    private JTextField filePathTextField, changesTextField, fileContentTextField;
    private File fileToMonitor;
    private boolean monitoring;
    private long lastModified;

    public HouseholdBudget() {
        setTitle("Haushaltskasse");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar tbar = new JToolBar();
        tbar.setSize(230, 20);

        // Panel für Dateiaktionen
        JPanel filePanel = new JPanel(new BorderLayout());

        // Panel für Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3));

        // Buttons erstellen
        startButton = new JButton("Start Monitoring");
        stopButton = new JButton("Stop Monitoring");
        clearButton = new JButton("Clear");
        carButton = new JButton("Auto");
        jointIncomeButton = new JButton("Gehalt R+M");
        childSupportButton = new JButton("Kindergeld");
        gezButton = new JButton("GEZ + TuS");

        // Buttons zum Panel hinzufügen
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(carButton);
        buttonPanel.add(jointIncomeButton);
        buttonPanel.add(childSupportButton);
        buttonPanel.add(gezButton);

        // Buttons für Toolbar
        JButton openFileButton = new JButton("Open File");
        openFileButton.addActionListener(this);
        tbar.add(openFileButton);

        JButton createFileButton = new JButton("Create File");
        createFileButton.addActionListener(this);
        tbar.add(createFileButton);

        JButton saveFileButton = new JButton("Save File");
        saveFileButton.addActionListener(this);
        tbar.add(saveFileButton);

        // Action Listener für die Buttons hinzufügen
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        clearButton.addActionListener(this);
        carButton.addActionListener(this);
        jointIncomeButton.addActionListener(this);
        childSupportButton.addActionListener(this);
        gezButton.addActionListener(this);

        // Textfeld für Dateipfad
        filePathTextField = new JTextField();
        filePathTextField.setEditable(false);

        // Textfeld für Änderungen
        changesTextField = new JTextField();
        changesTextField.setEditable(false);

        // Textfeld für Dateiinhalt
        fileContentTextField = new JTextField();
        fileContentTextField.setEditable(false);

        // Textfelder zum Panel hinzufügen
        filePanel.add(filePathTextField, BorderLayout.NORTH);
        filePanel.add(buttonPanel, BorderLayout.CENTER);
        filePanel.add(fileContentTextField, BorderLayout.SOUTH);
        add(tbar, BorderLayout.NORTH); // or any other suitable position
        add(filePanel, BorderLayout.CENTER);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Booking booking = new Booking();

        if (e.getActionCommand().equals("Open File")) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV und Excel-Dateien", "csv", "xls", "xlsx");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileToMonitor = fileChooser.getSelectedFile();
                filePathTextField.setText(fileToMonitor.getAbsolutePath());
                displayFileContent(fileToMonitor);
            }
        } else if (e.getActionCommand().equals("Create File")) {
            // Handle creating a new file
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File newFile = fileChooser.getSelectedFile();
                // Append .csv extension if not already present
                if (!newFile.getName().endsWith(".csv")) {
                    newFile = new File(newFile.getAbsolutePath() + ".csv");
                }
                try {
                    if (newFile.createNewFile()) {
                        JOptionPane.showMessageDialog(null, "New file created: " + newFile.getAbsolutePath());
                        // You can now write to the new file if needed
                    } else {
                        JOptionPane.showMessageDialog(null, "File already exists.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error creating new file.");
                }
            }
        } else if (e.getSource() == stopButton) {
            stopMonitoring();
        } else if (e.getSource() == clearButton) {
            changesTextField.setText("");
        } else if (e.getSource() == carButton) {
            String csvFormatted = "Car," + booking.getCar() + "€\n";
            JOptionPane.showMessageDialog(null, csvFormatted);
        } else if (e.getSource() == jointIncomeButton) {
            String csvFormatted = "Joint Income," + booking.getJoint_income() + "€ ohne Kindergeld," +
                    booking.getBank_balance() + "€ mit 500€ Kindergeld\n";
            JOptionPane.showMessageDialog(null, csvFormatted);
        } else if (e.getSource() == childSupportButton) {
            String csvFormatted = "Child Support," + booking.getChildSupport() + "€\n";
            JOptionPane.showMessageDialog(null, csvFormatted);
        } else if (e.getSource() == gezButton) {
            String csvFormatted = "GEZ + TuS," + booking.getGezTus() + "€\n";
            JOptionPane.showMessageDialog(null, csvFormatted);
        }
        
        }
    

    private void startMonitoring() {
        if (!monitoring) {
            Thread monitorThread = new Thread(() -> {
                try {
                    monitoring = true;
                    lastModified = fileToMonitor.lastModified();
                    while (monitoring) {
                        Thread.sleep(1000); // Check every second
                        long newModified = fileToMonitor.lastModified();
                        if (newModified != lastModified) {
                            changesTextField.setText("File has been modified.");
                            displayFileContent(fileToMonitor); // Update file content
                            lastModified = newModified;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            monitorThread.start();
        }
    }

    private void stopMonitoring() {
        monitoring = false;
        changesTextField.setText("");
    }

    private void displayFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            fileContentTextField.setText(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HouseholdBudget fileChangeMonitor = new HouseholdBudget();
            fileChangeMonitor.setVisible(true);
        });
    }
}
