package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class GUIAddDialog extends JDialog {
    private MainFrame mainFrame;
    private String[] columnNames;
    private JFormattedTextField[] textFields;
    private JButton addButton;
    private NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());

    public GUIAddDialog(MainFrame mainFrame) {
        super(mainFrame, "Add data", true);
        this.mainFrame = mainFrame;
        this.columnNames = this.mainFrame.getColumnNames();
        this.formatter.setValueClass(Integer.class);
        this.formatter.setAllowsInvalid(false);
        this.initButtons();
        this.initGUI();
        //this.setSize(400, 200);
        this.pack();
        this.setLocationRelativeTo(mainFrame);
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());

        // panel center, parameters
        JPanel panelCenter = new JPanel();
        this.textFields = new JFormattedTextField[this.columnNames.length];
        for (int i = 0; i < this.columnNames.length; i++) {
            JPanel panelTemp = new JPanel();
            JLabel label = new JLabel(this.columnNames[i] + ": ");
            panelTemp.add(label);
            this.textFields[i] = new JFormattedTextField(this.formatter);
            this.textFields[i].setColumns(7);
            label.setLabelFor(this.textFields[i]);
            panelTemp.add(this.textFields[i]);
            panelCenter.add(panelTemp);
        }
        this.add(BorderLayout.CENTER, panelCenter);


        JPanel panelSouth = new JPanel();
        panelSouth.setBorder(new EmptyBorder(20, 10, 20, 10));
        panelSouth.add(this.addButton);
        this.add(BorderLayout.SOUTH, panelSouth);
    }

    private void initButtons() {
        this.addButton = new JButton("Add");
        this.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer[] values = new Integer[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    values[i] = (Integer) textFields[i].getValue();
                }
                mainFrame.addRow(values);
            }
        });
    }
}
