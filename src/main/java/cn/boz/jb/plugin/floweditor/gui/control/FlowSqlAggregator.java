package cn.boz.jb.plugin.floweditor.gui.control;

/**
 * 可序列化为SQL的
 */
public interface FlowSqlAggregator {

    String toSql(String processId);
}
