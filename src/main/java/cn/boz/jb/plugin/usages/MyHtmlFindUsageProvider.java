package cn.boz.jb.plugin.usages;

import com.intellij.lang.HelpID;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyHtmlFindUsageProvider implements FindUsagesProvider {
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        System.out.println(psiElement);
        return false;
    }

    @Override
    public @Nullable @NonNls String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @Override
    public @Nls @NotNull String getType(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public @Nls @NotNull String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        return null;
    }
}
