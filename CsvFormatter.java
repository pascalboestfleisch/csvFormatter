import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class CsvFormatter extends JFrame implements ActionListener {
    private JButton clearButton, carButton, jointIncomeButton, childSupportButton, gezButton;
    private JTextField filePathTextField, changesTextField;
    private JTextArea fileContentTextArea;
    private File fileToMonitor;
    private StringBuilder builtContent;
    private boolean headersWritten;

    public CsvFormatter() {
        setTitle("Csv Formatter");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        builtContent = new StringBuilder();
        headersWritten = false;

        // Toolbar
        JToolBar tbar = new JToolBar();
        tbar.setSize(230, 20);

        // Panel for file actions
        JPanel filePanel = new JPanel(new BorderLayout());

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3));

        // create new buttons
        clearButton = new JButton("Clear");
        carButton = new JButton("Auto");
        jointIncomeButton = new JButton("Gehalt R+M");
        childSupportButton = new JButton("Kindergeld");
        gezButton = new JButton("GEZ + TuS");

        // put buttons in panels
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

        // Action Listener for buttons
        clearButton.addActionListener(this);
        carButton.addActionListener(this);
        jointIncomeButton.addActionListener(this);
        childSupportButton.addActionListener(this);
        gezButton.addActionListener(this);

        // Textfield for filepath
        filePathTextField = new JTextField();
        filePathTextField.setEditable(false);

        // Textfield for changes
        changesTextField = new JTextField();
        changesTextField.setEditable(false);

        // Textfield for file content
        fileContentTextArea = new JTextArea();
        fileContentTextArea.setEditable(false);
        fileContentTextArea.setLineWrap(true);
        fileContentTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(fileContentTextArea);
        scrollPane.setPreferredSize(new Dimension(480, 200));

        // add textfield to panels
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
                        builtContent = new StringBuilder();
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
            saveFile();
        } else if (e.getSource() == clearButton) {
            changesTextField.setText("");
            builtContent = new StringBuilder();
            headersWritten = false;
            if (fileToMonitor != null) {
                clearFileContent(fileToMonitor);
                displayFileContent(fileToMonitor);
            }
        } else if (e.getSource() == carButton || e.getSource() == jointIncomeButton
                || e.getSource() == childSupportButton || e.getSource() == gezButton) {
            String[] headers = { "Car", "Joint Income", "Child Support", "GEZ + TuS" };
            String[] values = { "", "", "", "" };

            if (headersWritten) {
                String[] existingContent = builtContent.toString().split("\n");
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

            builtContent.setLength(0);
            builtContent.append(String.join(";", headers)).append("\n");
            builtContent.append(String.join(";", values)).append("\n");

            headersWritten = true;

            if (fileToMonitor != null) {
                writeFileContent(fileToMonitor, builtContent.toString());
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

    private void saveFile() {
        if (fileToMonitor != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToMonitor))) {
                writer.write(builtContent.toString());
                JOptionPane.showMessageDialog(this, "File saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file to save or content is empty.");
        }
    }

    private void clearFileContent(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(""); // Write an empty string to clear the file
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error clearing file.");
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
            CsvFormatter fileChangeMonitor = new CsvFormatter();
            fileChangeMonitor.setVisible(true);
        });
    }
}
