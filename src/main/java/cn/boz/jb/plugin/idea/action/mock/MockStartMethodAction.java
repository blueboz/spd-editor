package cn.boz.jb.plugin.idea.action.mock;

import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import cn.boz.jb.plugin.idea.utils.MinimizeUtils;
import cn.boz.jb.plugin.idea.utils.MockUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
            List<JTextPane> textAreaList = new ArrayList<>();
            List<String> argClassNames = new ArrayList<String>();
            List<String> reqBody = new ArrayList<String>();
            JTextField urlField = new JTextField();
            urlField.setText(SpdEditorNormState.getInstance(anActionEvent.getProject()).mockbase);

            urlField.setColumns(50);
//            urlField.setPreferredSize(new Dimension(500,0));

            formBuilder.addLabeledComponent("url", urlField, 1, true);

            for (int i = 0; i < parameterList.getParametersCount(); i++) {
                PsiParameter parameter = parameterList.getParameter(i);
                PsiType type = parameter.getType();
                String pname = parameter.getName();
                String pType = type.getCanonicalText();

                JTextPane info = new JTextPane();
                info.setPreferredSize(new Dimension(400, 200));

                info.setEditable(true);
                info.setVisible(true);
//                info.setFont(new Font("微软雅黑", Font.ITALIC, 12));
                info.setAutoscrolls(true);
//                info.setSelectedTextColor(new JBColor(new Color(237, 237, 237),new Color(124, 124, 124)));
//                info.setSelectionColor(new JBColor(new Color(237, 237, 237),new Color(124, 124, 124)));
//                info.setBackground(new JBColor(new Color(237, 237, 237),new Color(124, 124, 124)));


                JBScrollPane jScrollPane = new JBScrollPane(info);
                jScrollPane.setPreferredSize(new Dimension(400, 200));
                textAreaList.add(info);
                UndoManager undoManager = new UndoManager();
                info.getDocument().addUndoableEditListener(undoManager);


                info.addKeyListener(new KeyAdapter() {

                    private boolean ctrlPressing=false;

                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        if(KeyEvent.VK_CONTROL==keyEvent.getKeyCode()){
                            ctrlPressing=true;
                        }
                        if(ctrlPressing&&keyEvent.getKeyCode()==KeyEvent.VK_Z){
                            if(undoManager.canUndo()){
                                undoManager.undo();
                            }
                        }
                        if(ctrlPressing&&keyEvent.getKeyCode()==KeyEvent.VK_Y){
                            if(undoManager.canRedo()){
                                undoManager.redo();
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent keyEvent) {
                        if(KeyEvent.VK_CONTROL==keyEvent.getKeyCode()){
                            ctrlPressing=false;
                        }
                        if(KeyEvent.VK_ESCAPE==keyEvent.getKeyCode()){
                            info.setFocusable(false);
                        }
                    }
                });

                info.getStyledDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        new Thread(() -> {
                            restyle((StyledDocument) e.getDocument());
                        }).start();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        new Thread(() -> {
                            restyle((StyledDocument) e.getDocument());
                        }).start();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
                formBuilder.addLabeledComponent(pType + " " + pname, jScrollPane, 1, true);
                argClassNames.add(pType);
            }
            req.put("argsClass", argClassNames);
            req.put("requestBody", reqBody);

//            JButton okBtn = new JButton("Post",AllIcons.Actions.Execute);


//            formBuilder.addComponent(okBtn, 1);

//            formBuilder.setHorizontalGap(5);
//            formBuilder.setVerticalGap(5);
            formBuilder.setFormLeftIndent(5);

            JPanel formPanel = formBuilder.getPanel();


            JPanel formWrapper = new JPanel();

            JBScrollPane formScroll = new JBScrollPane(formPanel);
            formScroll.setMinimumSize(new Dimension(600, 0));

            if(parameterList.getParametersCount()>0){
                formWrapper.setPreferredSize(new Dimension(600,400));
            }
            JBPopup popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(formWrapper, null)
                    .setProject(anActionEvent.getProject())
                    .setResizable(true)
                    .setMovable(true)
                    .setRequestFocus(true)
                    .setFocusable(true)
                    .setTitle("MethodInvoker")
                    .setCancelOnOtherWindowOpen(true)
                    .createPopup();

            ActionManager instance = ActionManager.getInstance();
            ActionGroup actionGroup = new ActionGroup() {
                private AnAction[] actions;

                {
                    actions = new AnAction[]{
                            new AnAction() {
                                {
                                    getTemplatePresentation().setText("Minimize");
                                    getTemplatePresentation().setIcon(AllIcons.Actions.MoveToBottomLeft);
                                }

                                private boolean vis = true;

                                @Override
                                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                    vis = false;
                                    formWrapper.setPreferredSize(null);

                                    anActionEvent.getPresentation().setEnabledAndVisible(false);
//                                    getTemplatePresentation().setVisible(false);
                                    MinimizeUtils.minimize(popup, formWrapper, project, "mock:"+classQualifiedName+"#"+methodName,false);
                                }

                                @Override
                                public void update(@NotNull AnActionEvent e) {
                                    if (!vis) {
                                        e.getPresentation().setEnabledAndVisible(false);
                                    }

                                }
                            },
                            new AnAction() {
                                {
                                    getTemplatePresentation().setText("Execute");
                                    getTemplatePresentation().setIcon(AllIcons.Actions.Execute);
                                }

                                @Override
                                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                    reqBody.clear();
                                    for (JTextPane jTextArea : textAreaList) {
                                        String text = jTextArea.getText();
                                        reqBody.add(text);
                                    }
                                    MockUtils.httpPostRequest(anActionEvent, JSON.toJSONString(req), urlField.getText());
                                    if (!popup.isDisposed()) {
                                        popup.dispose();
                                    }
                                }
                            },
                            new AnAction() {
                                {
                                    getTemplatePresentation().setText("Format");
                                    getTemplatePresentation().setIcon(SpdEditorIcons.FORMAT_16_ICON);
                                }

                                @Override
                                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                    for (JTextPane jTextPane : textAreaList) {
                                        String text = jTextPane.getText();
                                        try {
                                            JSONObject jsonObject = JSONObject.parseObject(text);
                                            String s = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat);
                                            jTextPane.setText(s);
                                        } catch (Exception e) {

                                        }

                                    }
                                }
                            }
                    };
                }

                @Override
                public @NotNull AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
                    return actions;
                }
            };

            ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
            formWrapper.setLayout(new BorderLayout());
            formWrapper.add(formScroll, BorderLayout.CENTER);
            formWrapper.add(spd_tb.getComponent(), BorderLayout.SOUTH);


            popup.showCenteredInCurrentWindow(anActionEvent.getProject());

//            okBtn.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//
//                }
//            });


            return;
        }


    }

    private static SimpleAttributeSet quoteStyle;
    private static SimpleAttributeSet numberic;

    private static SimpleAttributeSet fieldStyle;

    public static void setColorSchemaOfLight() {
        quoteStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(quoteStyle, new Color(0, 98, 122));

        numberic = new SimpleAttributeSet();
        StyleConstants.setForeground(numberic, new Color(23, 80, 235));

        fieldStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(fieldStyle, new Color(135, 16, 148));
    }

    public static void setColorSchemaOfDark() {
        quoteStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(quoteStyle, new Color(48, 204, 243));

        numberic = new SimpleAttributeSet();
        StyleConstants.setForeground(numberic, new Color(108, 148, 255));

        fieldStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(fieldStyle, new Color(233, 91, 248));
    }

    static {
        setColorSchemaOfLight();
    }

    public static void restyle(StyledDocument styledDocument) {
        synchronized (MockStartMethodAction.class) {
            try {
                String script = styledDocument.getText(styledDocument.getStartPosition().getOffset(), styledDocument.getEndPosition().getOffset());
                char[] chars = script.toCharArray();
                int strstart = -1;
                //false key true value
                boolean keyvmode = false;
                int numstart = -1;
                for (int i = 0; i < chars.length; i++) {
                    //json串
                    char c = chars[i];
                    if ('"' == c) {
                        if (strstart == -1) {
                            strstart = i;

                        } else {
                            int strend = i;

                            if (keyvmode) {
                                styledDocument.setCharacterAttributes(strstart, strend - strstart + 1, fieldStyle, false);
                                keyvmode = false;
                            } else {
                                styledDocument.setCharacterAttributes(strstart, strend - strstart + 1, quoteStyle, false);

                            }
                            strstart = -1;

                        }
                    }
                    if ('\r' == c || '\n' == c) {
                        keyvmode = false;
                        if (strstart != -1) {
                            //字符串强制结束
                            int strend = i;

                            if (keyvmode) {
                                styledDocument.setCharacterAttributes(strstart, strend - strstart + 1, fieldStyle, false);
                                keyvmode = false;
                            } else {
                                styledDocument.setCharacterAttributes(strstart, strend - strstart + 1, quoteStyle, false);
                            }
                        }
                        strstart = -1;

                    }
                    if (':' == c) {
                        keyvmode = true;
                    }
                    if (c >= '0' && c <= '9') {
                        if (numstart == -1 && strstart == -1) {
                            numstart = i;
                        }

                    } else {
                        //数字结束
                        if (numstart != -1) {
                            int numend = i;
                            styledDocument.setCharacterAttributes(numstart, numend - numstart + 1, numberic, false);
                            numstart = -1;

                        }
                    }


                }


            } catch (BadLocationException e) {
                e.printStackTrace();
            }


        }

    }
    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
                return;
            }
        }
        e.getPresentation().setVisible(false);

    }

}

