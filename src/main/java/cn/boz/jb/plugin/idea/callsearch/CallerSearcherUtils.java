package cn.boz.jb.plugin.idea.callsearch;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;

public class CallerSearcherUtils {

    public static Boolean isCheck(Object value) {
        if (value instanceof EngineTask) {
            return ((EngineTask) value).isChecked();
        } else if (value instanceof EngineAction) {
            return ((EngineAction) value).isChecked();
        } else if (value instanceof XfundsBatch) {
            return ((XfundsBatch) value).isChecked();
        }
        throw new RuntimeException("unsupport type" + value);
    }

}
