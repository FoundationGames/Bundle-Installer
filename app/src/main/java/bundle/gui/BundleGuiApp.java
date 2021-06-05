package bundle.gui;

import bundle.download.DownloadException;
import bundle.installer.BundleInstaller;
import com.google.common.collect.ImmutableList;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BundleGuiApp extends JFrame {
    private final BundleInstaller installer;
    private final CardLayout cards = new CardLayout();
    private final JPanel main;

    private final JLabel finishLabel = new JLabel();
    private List<DownloadException> finishErrors = new ArrayList<>();

    private static final String INSTALL_PANEL = "install";
    private static final String FINISH_INSTALL_PANEL = "finish_install";

    public BundleGuiApp(BundleInstaller installer) {
        super(installer.installerProperties.getProperty("window_title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int w = Integer.parseInt(installer.installerProperties.getProperty("width"));
        int h = Integer.parseInt(installer.installerProperties.getProperty("height"));
        boolean resizable = installer.installerProperties.getProperty("resizable").equalsIgnoreCase("true");
        setSize(w, h);
        setResizable(resizable);
        this.installer = installer;

        main = new JPanel(cards);
        main.add(installPanel(), INSTALL_PANEL);
        main.add(finishInstallPanel(), FINISH_INSTALL_PANEL);
        setContentPane(main);
    }

    private JPanel installPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JPanel installSelection = new JPanel();
                ImmutableList<String> names = this.installer.installerConfig.configNames;
                JComboBox<String> installs = new JComboBox<>(names.toArray(new String[names.size()]));
                installs.addActionListener(action -> this.installer.selectedInstall = (String)((JComboBox<String>)action.getSource()).getSelectedItem());
                installSelection.add(installs);
            panel.add(installSelection);

            JPanel dirSelection = new JPanel();
                JTextField filePath = new JTextField(this.installer.gameDir.toString());
                filePath.addActionListener(action -> {
                    this.installer.gameDir = Paths.get(((JTextField)action.getSource()).getText());
                    System.out.println("TEXT FIELD CHANGED");
                });
                dirSelection.add(filePath);
                JButton changeDir = new JButton("Browse ...");
                changeDir.addActionListener(action -> changeGameDir());
                dirSelection.add(changeDir);
            panel.add(dirSelection);

            JPanel installButtons = new JPanel();
                installButtons.setLayout(new FlowLayout());
                JButton cancel = new JButton("Download as Modpack");
                installButtons.add(cancel);
                JButton installMcFolder = new JButton("Install for Vanilla MC");
                installMcFolder.addActionListener(action -> {
                    try {
                        this.finishErrors = this.installer.install();
                        this.finishLabel.setText("Finished Installing "+installer.selectedInstall+ (this.finishErrors.size() > 0 ? " with errors!" : "!"));
                        this.cards.show(main, FINISH_INSTALL_PANEL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                installButtons.add(installMcFolder);
            panel.add(installButtons);
        return panel;
    }

    private JPanel finishInstallPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
            JPanel text = new JPanel();
                text.add(this.finishLabel);
            panel.add(text);
            JPanel errors = new JPanel();
                // TODO: Fix this
                List<String> es = new ArrayList<>();
                for (DownloadException ex : finishErrors) {
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) {
                        msg += " - "+ex.getCause();
                    }
                    es.add(msg);
                }
                JList<String> errorList = new JList<>(es.toArray(new String[es.size()]));
                errorList.setLayoutOrientation(JList.VERTICAL);
                JScrollPane pane = new JScrollPane();
                pane.setViewportView(errorList);
                errors.add(pane);
            panel.add(errors);
            JPanel doneArea = new JPanel();
                JButton done = new JButton("Done");
                done.addActionListener(action -> {
                    this.setVisible(false);
                    System.exit(0);
                });
                doneArea.add(done);
            panel.add(doneArea);
        return panel;
    }

    public void open() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setVisible(true);
    }

    public void changeGameDir() {
        JFileChooser fileSelect = new JFileChooser();
        fileSelect.setCurrentDirectory(this.installer.gameDir.toFile());
        fileSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileSelect.setAcceptAllFileFilterUsed(false);
        fileSelect.setDialogTitle("Select Game Directory (.minecraft Folder)");
        if (fileSelect.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            this.installer.gameDir = fileSelect.getSelectedFile().toPath();
        }
    }
}
