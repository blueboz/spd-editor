package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.floweditor.gui.utils.TranslateUtils;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.progress.ProgressManager;

import java.sql.Connection;
import java.util.List;

public class CopyEngineActionAllSqlAction extends CopyScriptBaseAction {

    @Override
    protected String engineActionSql(EngineAction action) {
        String id = action.getId();
        StringBuilder sqls = new StringBuilder();
        sqls.append(String.format("delete from ENGINE_ACTION where ID_='%s';", id));
        sqls.append("\n");
        sqls.append(String.format("delete from ENGINE_ACTIONINPUT where ACTIONID_='%s';", id));
        sqls.append("\n");
        sqls.append(String.format("delete from ENGINE_ACTIONOUTPUT where ACTIONID_='%s';", id));
        sqls.append("\n");
        String actionTemplate="INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_) " +
                "VALUES (%s, %s, %s, %s, %s, %s);\n";
        sqls.append(String.format(actionTemplate,
                TranslateUtils.translate(action.getId()),
                TranslateUtils.translate(action.getNamespace()),
                TranslateUtils.translate(action.getUrl()),
                TranslateUtils.translate(action.getWindowparam()),
                TranslateUtils.translate(action.getActionscript()),
                TranslateUtils.translate(action.getActionintercept())
        ));


        String actionInputTemplate = "INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_) " +
                "VALUES (%s, %s, %s, %s, %s);";
        String actionOutputTemplate = "INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_) VALUES " +
                "(%s, %s, %s, %s);";
        if(action.getInputs()==null||action.getOutputs()==null){
            ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                try {
                    List<EngineActionInput> engineActionInputs = DBUtils.getInstance().queryEngineActionInputIdMatch(currentAnAction.getProject(), action.getId());
                    action.setInputs(engineActionInputs);
                    List<EngineActionOutput> engineActionOutputs = DBUtils.getInstance().queryEngineActionOutputIdMatch(currentAnAction.getProject(), action.getId());
                    action.setOutputs(engineActionOutputs);

                } catch (Exception e) {
                    DBUtils.dbExceptionProcessor(e,currentAnAction.getProject());
                }
            }, "Loading...", true, currentAnAction.getProject());
        };
        for (EngineActionInput input : action.getInputs()) {
            String format = String.format(actionInputTemplate,
                    TranslateUtils.translate(input.getActionId()),
                    TranslateUtils.translate(input.getBeanId()),
                    TranslateUtils.translate(input.getClass_()),
                    TranslateUtils.translate(input.getFieldExpr()),
                    TranslateUtils.translate(input.getSource())
            );
            sqls.append(format);
            sqls.append("\n");
        }

        for (EngineActionOutput output : action.getOutputs()) {
            String format = String.format(actionOutputTemplate,
                    TranslateUtils.translate(output.getActionId()),
                    TranslateUtils.translate(output.getBeanId()),
                    TranslateUtils.translate(output.getFieldExpr()),
                    TranslateUtils.translate(output.getTarget())
            );
            sqls.append(format);
            sqls.append("\n");
        }

        return sqls.toString();
    }

    @Override
    protected String engineTaskSql(EngineTask engineTask) {
        return "not for this fun!";
    }
}
