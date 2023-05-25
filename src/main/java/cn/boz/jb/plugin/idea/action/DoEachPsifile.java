package cn.boz.jb.plugin.idea.action;

import com.intellij.psi.PsiFile;

public interface DoEachPsifile {

    /**
     * 返回true终止后续遍历
     *
     * @param psiFile
     * @return
     */
    public boolean doPsiFile(PsiFile psiFile);
}