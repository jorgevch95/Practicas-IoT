package gui;

import javax.swing.table.DefaultTableModel;

public class GUITableModel extends DefaultTableModel {

    public GUITableModel(String[] columnNames) {
        super(0, columnNames.length);
        this.setColumnIdentifiers(columnNames);
    }

    public Integer[][] getData() {
        Integer[][] data = new Integer[this.getRowCount()][this.getColumnCount()];
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                data[i][j] = (Integer) this.getValueAt(i, j);// getValueAt(row,column);
            }
        }
        return data;
    }
}
