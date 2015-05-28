package edu.pitt.dbmi.birads.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class ReportChooserPanel {

	private BrokGui gui;
	private JPanel panel;
	private JButton topButton = new JButton("Top");
	private JTextField searchField = new JTextField();
	private JButton bottomButton = new JButton("Bottom");
	private JList<ReportWidget> reportList;

	public void initialize() {
		reportList = new JList<ReportWidget>();
		ReportChooserListModel reportChooserListModel = new ReportChooserListModel();
		reportChooserListModel.setTiesUsername(System.getProperty("ties.user.name"));
		reportChooserListModel.setTiesPassword(System.getProperty("ties.user.password"));
		reportChooserListModel.initialize();
		reportList.setModel(reportChooserListModel);
		reportList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		reportList.setVisibleRowCount(-1);
		reportList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gui.loadReportWiget(reportList.getSelectedValue());
			}
		});
		JScrollPane listScroller = new JScrollPane(reportList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		listScroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Birads Reports");
		label.setLabelFor(reportList);
		listPane.add(label);
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(searchField);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(topButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(bottomButton);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getPanel().add(buttonPane, BorderLayout.PAGE_START);
		getPanel().add(listPane, BorderLayout.CENTER);

	}

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public BrokGui getGui() {
		return gui;
	}

	public void setGui(BrokGui gui) {
		this.gui = gui;
	}

}
