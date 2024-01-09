package org.timofeevAS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Base32ConverterApp extends JFrame {
    private JButton chooseFileButton;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton aboutButton;
    private JLabel selectedFileLabel;
    private File selectedFile;

    public Base32ConverterApp() {
        setTitle("Base32 File Converter"); // Header title of window

        setSize(600, 200); // Fixed size of window
        setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Default settings

        initializeComponents();

        setLayout(new GridLayout(4, 1));

        add(new JLabel("1. Choose a File:")); // File choose widget
        add(chooseFileButton);

        add(new JLabel("2. Selected File:")); // Title of selected file
        add(selectedFileLabel);

        add(encodeButton);
        add(decodeButton);

        JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aboutPanel.add(aboutButton);
        add(aboutPanel);
    }

    // Initializing all components on mainLayout
    private void initializeComponents() {
        chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        selectedFileLabel = new JLabel("No file selected");

        encodeButton = new JButton("Encode");
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    selectedFileLabel.setText(selectedFile.getName() + " (Encoding in progress)");
                    setButtonsEnabled(false);
                    encodeFileInBackground();
                }
            }
        });

        decodeButton = new JButton("Decode");
        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    selectedFileLabel.setText(selectedFile.getName() + " (Decoding in progress)");
                    setButtonsEnabled(false);
                    decodeFileInBackground();
                }
            }
        });

        aboutButton = new JButton("?");
        aboutButton.setPreferredSize(new Dimension(35,35));
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        chooseFileButton.setEnabled(enabled);
        encodeButton.setEnabled(enabled);
        decodeButton.setEnabled(enabled);
    }

    private void encodeFileInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    FileTransformBase32.encodeFileToBase32(selectedFile);
                    selectedFileLabel.setText(selectedFile.getName() + " successfully encoded");
                } catch (IOException | IllegalArgumentException ex) {
                    selectedFileLabel.setText(selectedFile.getName() + " (Error encoding file)");
                    showErrorDialog(ex.getMessage());
                    ex.printStackTrace();
                }
                finally {
                    setButtonsEnabled(true);
                }
                return null;
            }
        };

        worker.execute();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "File Base32 Converter\nVersion 1.0\nAuthor: Alexandr Timofeev\nGitHub: timofeevAS\n" +
                        "Chosen file can be encode\\decode in Base32, with fictious extension .b32, which marks new file",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String errorMessage){
        JOptionPane.showMessageDialog(this,
                "Some errors here:\n"+errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void decodeFileInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    FileTransformBase32.decodeFileFromBase32(selectedFile);
                    selectedFileLabel.setText(selectedFile.getName() + " successfully decoded");
                } catch (IOException | IllegalArgumentException ex) {
                    selectedFileLabel.setText(selectedFile.getName() + " (Error encoding file)");
                    showErrorDialog(ex.getMessage());
                    ex.printStackTrace();
                }
                finally {
                    setButtonsEnabled(true);
                }
                return null;
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Base32ConverterApp().setVisible(true);
            }
        });
    }
}
