package io2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class GUI extends JFrame {

    private FileManager fileManager;
    private JTextField pathField;
    private JTable table; // Declare the JTable at the class level


    public GUI() {
        // Initialize FileManager with the default path (e.g., user's home directory)
        fileManager = new FileManager();


        setTitle("File Manager");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a path bar at the top
        JPanel pathPanel = new JPanel(new BorderLayout());
        JLabel pathLabel = new JLabel("Path:");
        pathField = new JTextField(fileManager.getPath().toString());
        JButton goButton = new JButton("Go");
        JButton backButton = new JButton("←");

        // Add action listener to the go button to update the path and refresh the table
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePathAndRefreshTable();
            }
        });

        // Add action listener to the back button to update the path and go back a level in directory
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String parentPath = String.valueOf(fileManager.getPath().getParent());
                updatePathAndRefreshTable(parentPath);
            }
        });

        // Add action listener to the path bar to update the path and refresh the table on "Enter"
        pathField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePathAndRefreshTable();
            }
        });




        pathPanel.add(pathLabel, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(goButton, BorderLayout.EAST);
        pathPanel.add(backButton, BorderLayout.WEST);

        // Create a table with a DefaultTableModel make it so cells cannot be edited in the gui because it messes with doubleclicking
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel model = new DefaultTableModel();
        table.setModel(model);

        // Set table headers
        String[] headers = {"File Name", "Size (Bytes)", "Date Modified", "Directory"};
        model.setColumnIdentifiers(headers);

        // Populate the table with data from the ArrayList
        try {
            populateTableWithData(model);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading directory contents", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create buttons and place them at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton copyButton = new JButton("Copy File");
        JButton moveButton = new JButton("Move File");
        JButton deleteButton = new JButton("Delete File");
        JButton openButton = new JButton("Open File");
        JButton createDirectoryButton = new JButton("Create Directory");
        JButton deleteDirectoryButton = new JButton("Delete Directory");


        buttonPanel.add(copyButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(openButton);
        buttonPanel.add(createDirectoryButton);
        buttonPanel.add(deleteDirectoryButton);

        // Add ActionListener to the copy button
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyFile();
                updatePathAndRefreshTable();
            }
        });

        // Add ActionListener to the move button
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFile();
                updatePathAndRefreshTable();
            }
        });

        // Add ActionListener to the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFile();
                updatePathAndRefreshTable();
            }
        });

        // Add ActionListener to the open button
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
                updatePathAndRefreshTable();
            }
        });

        // Add ActionListener to the create directory button
        createDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createDirectory();
                updatePathAndRefreshTable();
            }
        });

        // Add ActionListener to the delete directory button
        deleteDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedDirectory();
                updatePathAndRefreshTable();
            }
        });

        //add MouseListener to table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    try {
                        if ((boolean) table.getValueAt(selectedRow, 3)) {
                            String fileName = (String) table.getValueAt(selectedRow, 0);
                            String sourcePath = String.valueOf(Paths.get(fileManager.getPath().resolve(fileName).toString()));
                            updatePathAndRefreshTable(sourcePath);
                        } else if (!(boolean) table.getValueAt(selectedRow, 3)) {
                            openFile();
                            updatePathAndRefreshTable();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GUI.this, "An error occurred", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Add components to the window
        add(pathPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void populateTableWithData(DefaultTableModel model) throws IOException {
        // Clear existing rows
        model.setRowCount(0);

        // Get the data from FileManager
        ArrayList<File> data = fileManager.listDirectoryContents();

        // Add each row to the table model
        for (File file : data) {
            model.addRow(new Object[]{
                    file.filePath.toString(),
                    file.fileSize,
                    file.lastModifiedTime,
                    file.isDirectory
            });
        }
    }


    private void updatePathAndRefreshTable() {
        try {
            // Update the path in FileManager
            fileManager.setPath(pathField.getText());

            // Refresh the table data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            populateTableWithData(model);
            pathField.setText(fileManager.getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid path or unable to read directory", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //overloaded method setting path outside of the pathField
    private void updatePathAndRefreshTable(String filePath) {
        try {
            // Update the path in FileManager
            fileManager.setPath(filePath);

            // Refresh the table data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            populateTableWithData(model);
            pathField.setText(fileManager.getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid path or unable to read directory", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyFile() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file to copy.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected file name from the table (assuming the path is in the first column)
        String fileName = (String) table.getValueAt(selectedRow, 0);

        // Construct the full path to the selected file
        Path sourcePath = fileManager.getPath().resolve(fileName);

        // Prompt the user for the target directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File targetDirectory = fileChooser.getSelectedFile();
            Path targetPath = targetDirectory.toPath().resolve(sourcePath.getFileName());

            try {
                Files.copy(sourcePath, targetPath);
                JOptionPane.showMessageDialog(this, "File copied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid path or unable to copy file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moveFile() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file to move.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected file name from the table (assuming the path is in the first column)
        String fileName = (String) table.getValueAt(selectedRow, 0);

        // Construct the full path to the selected file
        Path sourcePath = fileManager.getPath().resolve(fileName);

        // Prompt the user for the target directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File targetDirectory = fileChooser.getSelectedFile();
            Path targetPath = targetDirectory.toPath().resolve(sourcePath.getFileName());

            try {
                Files.move(sourcePath, targetPath);
                JOptionPane.showMessageDialog(this, "File moved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid path or unable to move file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteFile() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file to delete.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected file name from the table (assuming the path is in the first column)
        String fileName = (String) table.getValueAt(selectedRow, 0);

        // Construct the full path to the selected file
        Path sourcePath = fileManager.getPath().resolve(fileName);

        // Show confirmation dialog
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the file: " + fileName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            try {
                Files.delete(sourcePath);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Invalid path or unable to delete file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "File Deletion Cancelled", "Cancelled", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file to open.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected file name from the table (assuming the path is in the first column)
        String fileName = (String) table.getValueAt(selectedRow, 0);
        Path filePath = fileManager.getPath().resolve(fileName);

        // Check if the file is actually a file and not a directory
        if (Files.isDirectory(filePath)) {
            JOptionPane.showMessageDialog(this, fileName + " is a directory. Please select a file.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use the Desktop class to open the file
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Files.exists(filePath)) {
                desktop.open(filePath.toFile());
            } else {
                JOptionPane.showMessageDialog(this, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createDirectory() {
        Path filePath = fileManager.getPath();
        String input = JOptionPane.showInputDialog(this, "Enter the directory name:", "Input Required", JOptionPane.PLAIN_MESSAGE);

        if (input != null) { // Check if input is not null before trimming and processing
            input = input.trim();

            // Check if the input is a valid directory name
            boolean validFolderName = input.matches("^[^<>:\"/\\\\|?*\\x00-\\x1F]+$");

            if (validFolderName) {
                try {
                    Path newDirectory = filePath.resolve(input);
                    Files.createDirectory(newDirectory);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error creating directory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid directory name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No directory name provided", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedDirectory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1 || !(boolean) table.getValueAt(selectedRow, 3)) {
            JOptionPane.showMessageDialog(this, "Please select a directory to delete.", "No Directory Selected", JOptionPane.WARNING_MESSAGE);
        } else {
            // Get the selected directory name from the table (assuming the path is in the first column)
            String directoryName = (String) table.getValueAt(selectedRow, 0);

            // Construct the full path to the selected directory
            Path directoryPath = fileManager.getPath().resolve(directoryName);

            // Show confirmation dialog
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the directory: " + directoryName + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                try {
                    deleteDirectory(directoryPath);
                    JOptionPane.showMessageDialog(this, "Directory deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Unable to delete directory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Directory deletion canceled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void deleteDirectory(Path directoryPath) throws IOException {
        java.io.File[] allContents = directoryPath.toFile().listFiles(); //this is weird i think because cannot import java.io.File since i have a class File?
        if (allContents != null) {
            for (java.io.File file : allContents) { //this is weird i think because cannot import java.io.File since i have a class File?
                Path filePath = Paths.get(file.getPath());
                if (file.isDirectory()) {
                    deleteDirectory(filePath); // Recursive call for subdirectory
                } else {
                    Files.delete(filePath); // Delete the file
                }
            }
        }
        Files.delete(directoryPath); // Delete the directory itself
    }
}