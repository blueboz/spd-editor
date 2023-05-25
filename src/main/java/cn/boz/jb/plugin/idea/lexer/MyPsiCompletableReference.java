package cn.boz.jb.plugin.idea.lexer;

import com.intellij.model.SymbolResolveResult;
import com.intellij.model.psi.PsiSymbolReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.CharPattern;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ObjectPattern;
import com.intellij.patterns.StringPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MyPsiCompletableReference extends PsiReferenceContributor implements PsiSymbolReference {
    @Override
    public @NotNull PsiElement getElement() {
        return null;
    }

    @Override
    public @NotNull TextRange getRangeInElement() {
        return null;
    }

    @Override
    public @NotNull Collection<? extends SymbolResolveResult> resolveReference() {
        return null;
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {

    }
}
