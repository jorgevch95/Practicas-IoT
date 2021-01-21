package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Arrays;

public class GUIPredictDialog extends JDialog{
    private MainFrame mainFrame;
    private String[] columnNames;
    private JFormattedTextField[] textFields;
    private JButton predictButton;
    private NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());

    public GUIPredictDialog(MainFrame mainFrame) {
        super(mainFrame, "Predict", true);
        this.mainFrame = mainFrame;
        this.columnNames = Arrays.copyOfRange(this.mainFrame.getColumnNames(),0,this.mainFrame.getColumnNames().length-1);
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
        panelSouth.add(this.predictButton);
        this.add(BorderLayout.SOUTH, panelSouth);
    }

    private void initButtons() {
        this.predictButton = new JButton("Predict");
        this.predictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer[] values = new Integer[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    values[i] = (Integer) textFields[i].getValue();
                }
                sendPredict(values);
            }
        });
    }

    private void sendPredict(Integer[] values){
        mainFrame.sendPredict(values);
    }
}
