package cn.boz.jb.plugin.idea.codeinsight;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * `CompletionParameters` 类是 IntelliJ IDEA 代码完成框架的组成部分之一，用于在代码完成过程中传递上下文信息和反馈用户操作。
 *
 * 在 `com.intellij.codeInsight.completion.CompletionContributor` 的 `contributeCompletion` 方法中，通过 `CompletionParameters` 类获取有关自动完成的上下文信息，包括：
 *
 * 1. `getPosition()`：获取正在编辑的位置信息，通常是光标所在的位置。
 * 2. `getOriginalFile()`：获取正在编辑的文件。
 * 3. `getInvocationCount()`：获取完成操作被调用的次数（如用户多次按下 Ctrl+Space）。
 * 4. `getCharFilter()`：获取字符过滤器，该过滤器将决定哪些字符可以触发代码完成。
 * 5. `getOffsetMap()`：获取与代码完成相关的偏移量映射，这个映射将跟踪用户操作并在代码完成时更新。
 * 6. `getInvocationLocation()`：获取代码完成的位置，通常是正在编辑的文件的特定行或列。
 * 通过这些上下文信息，代码完成框架可以准确地确定用户正在编辑的位置和上下文，并为其提供相应的代码补全建议。
 * 需要注意的是，`CompletionParameters` 是不可变的，一旦创建就不能更改其内容。因此，在使用它时应该注意保护好它的值。
 */
public class MyCompletionContributor extends CompletionContributor {

    public MyCompletionContributor() {
        // 注册代码补全功能
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(JavaLanguage.INSTANCE),
                new JavaCompletionProvider());
    }

    private class JavaCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
            // 添加代码补全建议
            String prefix = parameters.getPosition().getText();
            // 根据编辑器中的输入条件，从数据库/网络等外部来源获取建议列表
            List<String> suggestions = getSuggestionsByPrefix(prefix);

            // 将建议列表添加到代码补全结果集中
            for (String suggestion : suggestions) {
                resultSet.addElement(LookupElementBuilder.create(suggestion));
            }
        }

    }

    private List<String> getSuggestionsByPrefix(String prefix) {

        List<String> suggestions = new ArrayList<>();

        // 使用JDBC连接数据库查询数据表
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/test", "user", "password");
             Statement statement = connection.createStatement()) {
            String query = "SELECT name FROM users WHERE name LIKE '" + prefix + "%'";
            ResultSet resultSet = statement.executeQuery(query);
            // 根据前缀从数据表中筛选建议
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                suggestions.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

}

