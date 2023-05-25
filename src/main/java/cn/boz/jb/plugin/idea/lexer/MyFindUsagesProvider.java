package cn.boz.jb.plugin.idea.lexer;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyFindUsagesProvider implements FindUsagesProvider {


    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {

        return true;
    }

    @Override
    public @Nullable String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public @Nls @NotNull String getType(@NotNull PsiElement psiElement) {
        return "simple property";
    }

    @Override
    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public @Nls @NotNull String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        return "hi";
    }
}
