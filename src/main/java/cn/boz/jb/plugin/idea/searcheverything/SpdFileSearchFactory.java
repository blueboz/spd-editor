package cn.boz.jb.plugin.idea.searcheverything;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

/**
 * 检索器
 */
public class SpdFileSearchFactory implements SearchEverywhereContributorFactory {

    @Override
    public @NotNull SearchEverywhereContributor createContributor(@NotNull AnActionEvent initEvent) {
        return new SearchEverywhereContributor() {
            @Override
            public @NotNull String getSearchProviderId() {
                return "Spd";
            }

            @Override
            public @NotNull @Nls String getGroupName() {
                return "Spd";
            }

            @Override
            public int getSortWeight() {
                return 0;
            }

            @Override
            public boolean showInFindResults() {
                return true;
            }

            @Override
            public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor consumer) {
                //检索东西，怎么检索，看看别人怎么写
            }

            @Override
            public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
                return false;
            }

            @Override
            public @NotNull ListCellRenderer getElementsRenderer() {
                return new ListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        return null;
                    }
                };
            }

            @Override
            public @Nullable Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
                return null;
            }
        };
    }

}
