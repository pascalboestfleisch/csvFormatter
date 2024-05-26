import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HouseholdBudget extends JFrame implements ActionListener {
    private JButton startButton, stopButton, clearButton, carButton, jointIncomeButton, childSupportButton, gezButton;
    private JTextField filePathTextField, changesTextField;
    private JTextArea fileContentTextArea;
    private File fileToMonitor;
    private boolean monitoring;
    private long lastModified;
    private StringBuilder accumulatedContent;
    private boolean headersWritten;

    public HouseholdBudget() {
        setTitle("Haushaltskasse");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        accumulatedContent = new StringBuilder();
        headersWritten = false;

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
        fileContentTextArea = new JTextArea();
        fileContentTextArea.setEditable(false);
        fileContentTextArea.setLineWrap(true);
        fileContentTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(fileContentTextArea);
        scrollPane.setPreferredSize(new Dimension(480, 200));

        // Textfelder zum Panel hinzufügen
        filePanel.add(filePathTextField, BorderLayout.NORTH);
        filePanel.add(buttonPanel, BorderLayout.CENTER);
        filePanel.add(scrollPane, BorderLayout.SOUTH);
        add(tbar, BorderLayout.NORTH);
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
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File newFile = fileChooser.getSelectedFile();
                if (!newFile.getName().endsWith(".csv")) {
                    newFile = new File(newFile.getAbsolutePath() + ".csv");
                }
                try {
                    if (newFile.createNewFile()) {
                        JOptionPane.showMessageDialog(null, "New file created: " + newFile.getAbsolutePath());
                        fileToMonitor = newFile;
                        filePathTextField.setText(newFile.getAbsolutePath());
                        accumulatedContent = new StringBuilder();
                        headersWritten = false;
                    } else {
                        JOptionPane.showMessageDialog(null, "File already exists.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error creating new file.");
                }
            }
        } else if (e.getActionCommand().equals("Save File")) {
            // TODO Logik um Dateien zu speichern
        } else if (e.getSource() == clearButton) {
            changesTextField.setText("");
            accumulatedContent = new StringBuilder();
            headersWritten = false;
        } else if (e.getSource() == carButton || e.getSource() == jointIncomeButton
                || e.getSource() == childSupportButton || e.getSource() == gezButton) {
            String[] headers = { "Car", "Joint Income", "Child Support", "GEZ + TuS" };
            String[] values = { "", "", "", "" };

            if (headersWritten) {
                String[] existingContent = accumulatedContent.toString().split("\n");
                if (existingContent.length > 1) {
                    String[] currentValues = existingContent[1].split(";");
                    for (int i = 0; i < currentValues.length; i++) {
                        values[i] = currentValues[i];
                    }
                }
            }

            if (e.getSource() == carButton) {
                values[0] = booking.getCar() + "€";
            } else if (e.getSource() == jointIncomeButton) {
                values[1] = booking.getJoint_income() + "€ ohne Kindergeld";
            } else if (e.getSource() == childSupportButton) {
                values[2] = booking.getChildSupport() + "€";
            } else if (e.getSource() == gezButton) {
                values[3] = booking.getGezTus() + "€";
            }

            accumulatedContent.setLength(0);
            accumulatedContent.append(String.join(";", headers)).append("\n");
            accumulatedContent.append(String.join(";", values)).append("\n");

            headersWritten = true;

            if (fileToMonitor != null) {
                writeFileContent(fileToMonitor, accumulatedContent.toString());
                displayFileContent(fileToMonitor);
            }
        }
    }

    private void writeFileContent(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) { // Overwrite mode
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            fileContentTextArea.setText(content.toString());
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