package com.yd.test.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.yd.test.plugin.util.Pair;

import java.util.List;

/**
 * Created by yangdan
 * on 2018/5/24.
 */
public class DoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass context = getPsiClassFromContext(e);
        OptionsDialog dialog = new OptionsDialog(context);
        dialog.show();

        if (dialog.isOK()) {
            writeJava(context, dialog.getAnnotationClassName(), dialog.getDatas());
        }
    }

    private void writeJava(PsiClass psiClass, String className, List<Pair<String, String>> datas) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                new IntDefGenerator(psiClass, className, datas).generate();
            }
        }.execute();
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        PsiClass context = getPsiClassFromContext(e);
        // 指定 插件是否启用
        e.getPresentation().setEnabled(context != null && !context.isEnum() /*&& !context.isInterface()*/);
    }

    // copy form ParcelableAction.java
    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null) {
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }
}
