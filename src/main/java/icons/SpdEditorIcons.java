package icons;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IconManager;

import javax.swing.Icon;

public class SpdEditorIcons {

    public static Icon BOM_16_ICON = icon("/icons/bom16.svg");
    public static Icon BOM_ICON = icon("/icons/bom.svg");
    public static Icon WARN_ICON = icon("/icons/warn.svg");
    public static Icon WARN_64_ICON = icon("/icons/warn64.svg");
    public static Icon FLOW_16_ICON = icon("/icons/flow.svg");
    public static Icon FLOW_GRAY_16_ICON = icon("/icons/flowgray.svg");
    public static Icon REF_16_ICON = icon("/icons/ref.svg");


    static Icon icon(String path) {
        Icon icon = IconLoader.getIcon(path, SpdEditorIcons.class);

        return icon;
    }
}
