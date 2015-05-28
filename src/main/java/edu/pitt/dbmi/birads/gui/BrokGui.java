package edu.pitt.dbmi.birads.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class BrokGui {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Brok");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BrokGui brokGui = new BrokGui();
		brokGui.composePanels();
		JPanel mainPanel = brokGui.getMainPanel();
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JPanel mainPanel = new JPanel();
	private JPanel textPanel = new JPanel();
	private JPanel fileChooserPanel = new JPanel();
	private JPanel biradsPanel = new JPanel();

	private JTextPane textPane;
	private JScrollPane paneScrollPane;

	public BrokGui() {
		;
	}

	public void composePanels() {
		buildMainPanel();

	}

	private void buildMainPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 5;
		gbc.ipady = 5;

		GridBagLayout gridBagLayout = new GridBagLayout();
		mainPanel.setLayout(gridBagLayout);
		Border border = BorderFactory.createTitledBorder("Report Text");
		textPanel.setBorder(border);
		mainPanel.add(buildTextPanel(), gbc);

		gbc.gridx = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		ReportChooserPanel fileChooser = new ReportChooserPanel();
		fileChooser.setGui(this);
		fileChooser.initialize();
		fileChooserPanel = fileChooser.getPanel();

		// border = BorderFactory.createTitledBorder("Report Chooser");
		// fileChooserPanel.setBorder(border);
		mainPanel.add(fileChooserPanel, gbc);

		// Birds
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		BiradsSummaryPanel biradsSummary = new BiradsSummaryPanel();
		biradsSummary.inititialize();
		biradsPanel = biradsSummary.getPanel();
		border = BorderFactory.createTitledBorder("Birads");
		biradsPanel.setBorder(border);
		mainPanel.add(biradsPanel, gbc);

		mainPanel.setPreferredSize(new Dimension(1200, 900));
	}

	public void loadReportWiget(ReportWidget fileWidget) {
		textPane.setText(fileWidget.getReport().getBody());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BrokGui.this.paneScrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}

	private JPanel buildTextPanel() {
		JPanel p = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 5;
		gbc.ipady = 5;
		GridBagLayout gridBagLayout = new GridBagLayout();
		p.setLayout(gridBagLayout);
		paneScrollPane = new JScrollPane(createTextPane());
		paneScrollPane.setPreferredSize(new Dimension(250, 155));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));
		p.add(paneScrollPane, gbc);
		return p;
	}

	private JTextPane createTextPane() {
		textPane = new JTextPane();
		return textPane;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

}