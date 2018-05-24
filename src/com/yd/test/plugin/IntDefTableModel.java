package com.yd.test.plugin;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.util.ui.EditableModel;
import com.yd.test.plugin.util.Pair;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by yangdan
 * on 2018-05-23.
 */
public class IntDefTableModel extends AbstractTableModel implements EditableModel {

    private PsiField[] allFields;
    private List<Pair<String, String>> mDatas = new ArrayList<>();

    private int mMaxValue = 0x0;

    IntDefTableModel(PsiClass mClass) {
        allFields = mClass.getAllFields();
    }

    @Override
    public int getRowCount() {
        return mDatas.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Constant Name" : "Constant Value(int)";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        Pair<String, String> pair = mDatas.get(rowIndex);
        return columnIndex == 0 ? pair.first : pair.second;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Pair<String, String> pair = mDatas.get(rowIndex);
        if (columnIndex == 0) {
            pair.first = String.valueOf(aValue);
        } else {
            pair.second = String.valueOf(aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void removeRow(int row) {
        mDatas.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public void addRow() {
        String value = Integer.toHexString(mMaxValue).toLowerCase();
        if (!value.startsWith("0x")) {
            value = "0x" + value;
        }
        String key = "KEY_OF_" + value.toUpperCase();
        Pair<String, String> pair = new Pair<>(key, value);
        mMaxValue++;
        mDatas.add(pair);
        int row = mDatas.size() - 1;
        fireTableRowsInserted(row, row);
    }

    @Override
    public void exchangeRows(int i, int i1) {
    }

    @Override
    public boolean canExchangeRows(int i, int i1) {
        return false;
    }

    List<Pair<String, String>> getDatas() {
        return mDatas;
    }

    void checkAllNormal() throws Exception {
        int len = mDatas.size();
        for (int i = 0; i < len - 1; i++) {
            Pair<String, String> first = mDatas.get(i);
            checkEmpty(first);
            checkFieldName(first.first);
            checkFormat(first.second);
            for (int j = i + 1; j < len; j++) {
                Pair<String, String> second = mDatas.get(j);
                if (Objects.equals(first.first, second.first)) {
                    throw new Exception("Both same name " + first.first);
                }
                if (Objects.equals(first.second, second.second)) {
                    throw new Exception("Both same value " + first.second);
                }
            }
        }
        Pair<String, String> end = mDatas.get(len - 1);
        checkEmpty(end);
        checkFieldName(end.first);
        checkFormat(end.second);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkFormat(String value) throws Exception {
        try {
            Integer.decode(value);
        } catch (NumberFormatException e) {
            throw new Exception(value + " can not format to int");
        }
    }

    private void checkEmpty(Pair<String, String> pair) throws Exception {
        if (pair.first == null || pair.first.isEmpty()) {
            throw new Exception("Empty Name");
        }
        if (pair.second == null || pair.second.isEmpty()) {
            throw new Exception("Empty Value");
        }
    }

    private void checkFieldName(String name) throws Exception {
        for (PsiField field : allFields) {
            if (name.equals(field.getName())) {
                throw new Exception("Field name " + name + " already exists");
            }
        }
    }
}
