package gui;

import integration.integration.Integration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private String[] columnNames;
    private GUITable table;
    private GUITableModel tableModel;
    private JPanel controllerPanel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton sendButton;
    private JButton predictButton;
    private GUIAddDialog addDialog;
    private GUIPredictDialog predictDialog;

    private Integration integration;

    public MainFrame(String[] columnNames, Integration integration) {
        super("DII Práctica 4");
        this.columnNames = columnNames;
        this.integration = integration;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 300);
        //this.pack();
        this.initGUI();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());
        this.tableModel = new GUITableModel(columnNames);
        this.table = new GUITable(tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(BorderLayout.CENTER, scrollPane);

        this.addDialog = new GUIAddDialog(this);
        this.predictDialog = new GUIPredictDialog(this);

        this.createControllerPanel();
    }

    private void createControllerPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.controllerPanel = new JPanel();
        this.controllerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.controllerPanel.setSize((int) (this.getWidth() * 0.2), this.getHeight());
        this.controllerPanel.setLayout(new GridBagLayout());
        this.initButtons();
        this.controllerPanel.add(this.addButton, gbc);
        this.controllerPanel.add(this.deleteButton, gbc);
        this.controllerPanel.add(this.sendButton, gbc);
        this.controllerPanel.add(this.predictButton, gbc);
        this.add(BorderLayout.EAST, this.controllerPanel);
    }

    private void initButtons() {
        this.addButton = new JButton("Add");
        this.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDialog.setVisible(true);
            }
        });
        this.deleteButton = new JButton("Delete");
        this.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                for (int i = 0; i < selected.length; i++) {
                    tableModel.removeRow(selected[i]);
                }
            }
        });
        this.sendButton = new JButton("Send");
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage(integration.sendTrainData(tableModel.getData(), columnNames));
            }
        });
        this.predictButton = new JButton("Predict");
        this.predictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                predictDialog.setVisible(true);
            }
        });
    }

    public void showMessage(String info){
        JOptionPane.showMessageDialog(this, info);
    }

    public void addRow(Integer[] values) {
        this.tableModel.addRow(values);
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public void sendPredict(Integer[] data){
        showMessage("Result: " + integration.predict(data, this.getColumnNames()));
    }
}
