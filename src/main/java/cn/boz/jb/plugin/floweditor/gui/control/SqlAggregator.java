package cn.boz.jb.plugin.floweditor.gui.control;

/**
 * 可序列化为SQL的
 */
public interface SqlAggregator {

    String toSql(String processId);

    default String getStringOrNull(String str) {
        if (str == null) {
            return "null";
        } else {
            return "'" + str + "'";
        }
    }
}
