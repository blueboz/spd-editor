package cn.boz.jb.plugin.idea.filetype;

import com.intellij.lang.xml.XMLLanguage;

public class SpdLanguage extends XMLLanguage {
    public static final SpdLanguage INSTANCE = new SpdLanguage();

    protected SpdLanguage() {
        super(XMLLanguage.INSTANCE,"Spd",new String[]{"application/spd", "text/spd"});
    }
}
