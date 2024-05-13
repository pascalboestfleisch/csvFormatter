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

        // Panel für Reiter
        JTabbedPane tabbedPane = new JTabbedPane();

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

        // Datei-Reiter hinzufügen
        tabbedPane.addTab("File Actions", filePanel);

        // Dateidialog-Reiter hinzufügen
        JPanel dialogPanel = new JPanel();
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        dialogPanel.add(browseButton);
        tabbedPane.addTab("File Dialog", dialogPanel);

        // Änderungen-Reiter hinzufügen
        tabbedPane.addTab("Changes", changesTextField);

        add(tabbedPane, BorderLayout.NORTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Booking booking = new Booking();

        if (e.getActionCommand().equals("Browse")) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV und Excel-Dateien", "csv", "xls", "xlsx");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileToMonitor = fileChooser.getSelectedFile();
                filePathTextField.setText(fileToMonitor.getAbsolutePath());
                displayFileContent(fileToMonitor);
            }
        } else if (e.getSource() == startButton) {
            if (fileToMonitor != null) {
                startMonitoring();
            } else {
                JOptionPane.showMessageDialog(null, "Bitte eine CSV oder xls-Datei auswählen");
            }
        } else if (e.getSource() == stopButton) {
            stopMonitoring();
        } else if (e.getSource() == clearButton) {
            changesTextField.setText("");
        } else if (e.getSource() == carButton) {
            JOptionPane.showMessageDialog(null, booking.getCar() + "€");
        } else if (e.getSource() == jointIncomeButton) {
            JOptionPane.showMessageDialog(null, booking.getJoint_income() + "€ ohne Kindergeld\n" +
                    booking.getBank_balance() + "€ mit 500€ Kindergeld");
        } else if (e.getSource() == childSupportButton) {
            JOptionPane.showMessageDialog(null, booking.getChildSupport() + "€");
        } else if (e.getSource() == gezButton) {
            JOptionPane.showMessageDialog(null, booking.getGezTus() + "€");
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
