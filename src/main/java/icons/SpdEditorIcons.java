package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public class SpdEditorIcons {

    public static Icon BOM_16_ICON = icon("/icons/bom16.svg");
    public static Icon BOM_ICON = icon("/icons/bom.svg");
    public static Icon WARN_ICON = icon("/icons/warn.svg");
    public static Icon WARN_64_ICON = icon("/icons/warn64.svg");
    public static Icon FLOW_16_ICON = icon("/icons/flow.svg");
    public static Icon FLOW_GRAY_16_ICON = icon("/icons/flowgray.svg");
    public static Icon REF_16_ICON = icon("/icons/ref.svg");
    public static Icon ACTION_16_ICON = icon("/icons/action16.svg");
    public static Icon ACTION_32_ICON = icon("/icons/action32.svg");
    public static Icon MENU_16_ICON = icon("/icons/menu.svg");

    public static Icon COMPARE_16_ICON = icon("/icons/compare.svg");
    public static Icon PROCESS_16_ICON = icon("/icons/process.svg");
    public static Icon ACTION_SCRIPT_16_ICON = icon("/icons/action.svg");
    public static Icon GEAR_16_ICON = icon("/icons/gear.svg");
    public static Icon DATABASE_16_ICON = icon("/icons/database.svg");
    public static Icon CODE_16_ICON = icon("/icons/code.svg");
    public static Icon ZIP_16_ICON = icon("/icons/zip.svg");
    public static Icon JAVASCRIPT_16_ICON = icon("/icons/javascript.svg");
    public static Icon REFER_16_ICON = icon("/icons/refer.svg");
    public static Icon SEARCH_ITEM_16_ICON = icon("/icons/searchitem.svg");

    static Icon icon(String path) {
        Icon icon = IconLoader.getIcon(path, SpdEditorIcons.class);

        return icon;
    }
}
