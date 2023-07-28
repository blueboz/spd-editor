package cn.boz.jb.plugin.idea.filetype;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;

public class CodeGenLanguage extends Language {
    public static final CodeGenLanguage INSTANCE = new CodeGenLanguage();

    protected CodeGenLanguage(String ID, String... mimeTypes) {
        super(INSTANCE, ID, mimeTypes);
    }

    private CodeGenLanguage() {
        super("CodeGen", new String[]{"application/json", "application/vnd.api+json", "application/hal+json", "application/ld+json"});
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    public boolean hasPermissiveStrings() {
        return false;
    }
}

