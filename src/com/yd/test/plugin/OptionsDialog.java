package com.yd.test.plugin;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiClass;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.ui.table.JBTable;
import com.yd.test.plugin.util.IHorizontalBox;
import com.yd.test.plugin.util.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.util.List;

/**
 * Created by yangdan
 * on 2018-05-24.
 */
public class OptionsDialog extends DialogWrapper implements ListSelectionListener, TableModelListener {

    private final LabeledComponent<JPanel> mContentComponent;

    private IntDefTableModel mTableModel;
    private JBTable mTable;
    private JBTextField mClassNameFiled;
    private PsiClass mClass;

    OptionsDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        mClass = psiClass;
        setTitle("IntDef Annotation Generation");

        mTableModel = new IntDefTableModel(psiClass);

        mTable = new JBTable(mTableModel);
        mTable.getColumnModel().setColumnMargin(0);
        mTable.getTableHeader().setReorderingAllowed(false);
        mTable.getTableHeader().setResizingAllowed(true);
        mTable.setCellSelectionEnabled(true);
        mTable.setColumnSelectionAllowed(false);
        mTable.setRowSelectionAllowed(true);
        mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable.getSelectionModel().addListSelectionListener(this);
        mTableModel.addTableModelListener(this);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(mTable).disableUpDownActions();
        decorator.setEditAction(null);
        JPanel panel = decorator.createPanel();

        mContentComponent = LabeledComponent.create(panel, "Constant values to generate");

        mClassNameFiled = new JBTextField("AnnotationClassName");
        setOKActionEnabled(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createNorthPanel() {
        JComponent northPanel = super.createNorthPanel();
        IHorizontalBox box = new IHorizontalBox();

        JBLabel label = new JBLabel("Annotation class name");
        box.add(label, "West");
        box.add(mClassNameFiled, "Center");
        if (northPanel == null) {
            return box;
        } else {
            VerticalBox verticalBox = new VerticalBox();
            verticalBox.add(northPanel);
            verticalBox.add(box);
            return verticalBox;
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mContentComponent;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String className = getAnnotationClassName();
        if (className == null || className.isEmpty()) {
            return new ValidationInfo("Annotation class name is empty");
        }
        PsiClass[] allInnerClasses = mClass.getAllInnerClasses();
        for (PsiClass item : allInnerClasses) {
            if (className.equals(item.getName())) {
                return new ValidationInfo("class or interface " + className + " already exists");
            }
        }
        if (getDatas().isEmpty()) {
            return new ValidationInfo("Constant Key and Value is empty");
        }
        try {
            mTableModel.checkAllNormal();
        } catch (Exception e) {
            return new ValidationInfo(e.getMessage());
        }
        return null;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // item selected
        TableCellEditor cellEditor = mTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
    }

    List<Pair<String, String>> getDatas() {
        return mTableModel.getDatas();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        switch (e.getType()) {
            case TableModelEvent.DELETE:
                setOKActionEnabled(!getDatas().isEmpty());
                break;
            case TableModelEvent.INSERT:
                setOKActionEnabled(true);
        }
    }

    String getAnnotationClassName() {
        return mClassNameFiled.getText();
    }
}
