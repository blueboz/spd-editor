package cn.boz.jb.plugin.idea.action.mock;

import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import cn.boz.jb.plugin.idea.utils.MinimizeUtils;
import cn.boz.jb.plugin.idea.utils.MockUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.alibaba.fastjson.JSON;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于导航到指定文件
 */
public class MockStartMethodAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);

        FileType fileType = psiFile.getFileType();
        PsiElement element = psiFile.findElementAt(offset);
        if (fileType instanceof JavaFileType) {
            //判断是不是Dao接口，如果是Dao 接口，name
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (containingMethod == null) {

                NotificationUtils.warnning("Mockstartmethod", "mock fail!method is null", project);
                return;
            }
            PsiClass containingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            String classQualifiedName = containingClass.getQualifiedName();
            String methodName = containingMethod.getName();

            PsiParameterList parameterList = containingMethod.getParameterList();
            Map<String, Object> req = new HashMap<>();
            req.put("beanName", classQualifiedName);
            req.put("methodName", methodName);

            //弹出框，输入参数
            FormBuilder formBuilder = FormBuilder.createFormBuilder();
            List<JTextArea> textAreaList = new ArrayList<>();
            List<String> argClassNames = new ArrayList<String>();
            List<String> reqBody = new ArrayList<String>();
            JTextField urlField = new JTextField();
            urlField.setText(SpdEditorNormState.getInstance(anActionEvent.getProject()).mockbase);
            urlField.setColumns(50);

            formBuilder.addLabeledComponent("url", urlField);
            for (int i = 0; i < parameterList.getParametersCount(); i++) {
                PsiParameter parameter = parameterList.getParameter(i);
                PsiType type = parameter.getType();
                String pname = parameter.getName();
                String pType = type.getCanonicalText();
                JTextArea jTextArea = new JTextArea();
                jTextArea.setAutoscrolls(true);
                jTextArea.setLineWrap(true);
                jTextArea.setRows(3);
                jTextArea.setColumns(50);
                textAreaList.add(jTextArea);
                formBuilder.addLabeledComponent(pname + ":" + pType, jTextArea);
                argClassNames.add(pType);
            }
            req.put("argsClass", argClassNames);
            req.put("requestBody", reqBody);

            JButton okBtn = new JButton("Go");


            formBuilder.addComponentToRightColumn(okBtn);

            JPanel formPanel = formBuilder.getPanel();

            ActionManager instance = ActionManager.getInstance();
            ActionGroup actionGroup=new ActionGroup() {
                @Override
                public @NotNull AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
                    return new AnAction[]{
                            new AnAction() {
                                {
                                    getTemplatePresentation().setIcon(AllIcons.Actions.MoveToBottomLeft);
                                }
                                @Override
                                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                    MinimizeUtils.minimize( formPanel,project,"mockStart");
                                }
                            }
                    };
                }
            };
            ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(formPanel,BorderLayout.CENTER);
            contentPanel.add(spd_tb.getComponent(), BorderLayout.SOUTH);

            JBScrollPane jbScrollPane = new JBScrollPane(contentPanel);



            JBPopup popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(jbScrollPane, null)
                    .setProject(anActionEvent.getProject())
                    .setMovable(true)
                    .setRequestFocus(true)
                    .setFocusable(true)
                    .setTitle("MethodInvoker")
                    .setCancelOnOtherWindowOpen(true)
                    .createPopup();
            popup.showCenteredInCurrentWindow(anActionEvent.getProject());

            okBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (JTextArea jTextArea : textAreaList) {
                        String text = jTextArea.getText();
                        reqBody.add(text);
                    }
                    MockUtils.httpPostRequest(anActionEvent, JSON.toJSONString(req), urlField.getText());
                    if(!popup.isDisposed()){
                        popup.dispose();
                    }
                }
            });


            return;
        }


    }




//    @Override
//    public void update(AnActionEvent e) {

//        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
//        boolean b = editor != null && psiFile != null;
//        if (b) {
//            int offset = editor.getCaretModel().getOffset();
//
//            PsiElement element = psiFile.findElementAt(offset);
//
//            FileType fileType = psiFile.getFileType();
//            if (fileType instanceof JavaFileType) {
//                PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
//                if(containingMethod!=null){
//                    e.getPresentation().setEnabled(true);
//                    e.getPresentation().setVisible(true);
//                }
//
//            }
//        }

//    }


}

