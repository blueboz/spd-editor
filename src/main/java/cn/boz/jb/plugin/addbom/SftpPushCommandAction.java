package cn.boz.jb.plugin.addbom;

import cn.boz.jb.plugin.idea.action.GotoScriptAction;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SftpPushCommandAction extends AnAction implements ClipboardOwner {

    @Override
    public void update(@NotNull AnActionEvent e) {


    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile[] virtualFiles = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        List<String[]> filemapper = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            String path = virtualFile.getPath();
            String convert = convert(path);
            if (convert != null) {
                if(virtualFile.isDirectory()){
                    filemapper.add(new String[]{path+"/*", convert});
                }else{
                    filemapper.add(new String[]{path, convert});
                }
            }
        }
        ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<String>("env"
                , "21.96.110.12"
                , "21.96.110.28"
                , "21.96.35.33"
                , "21.96.35.34"
                , "21.96.22.19"
                , "21.96.22.20"
                , "21.96.35.45"
                , "21.96.35.46"
        ) {
            @Override
            public @Nullable PopupStep onChosen(String selectedValue, boolean finalChoice) {
                if (finalChoice) {
                    return doFinalStep(() -> doRun(selectedValue));
                }
                return PopupStep.FINAL_CHOICE;

            }

            private void doRun(String selectedValue) {
                EventQueue.invokeLater(() -> {
                    String template = "scp -P 18122 %s a-dkzjjy-usr@%s:%s\n";
                    StringBuilder sb = new StringBuilder();
                    for (String[] fm : filemapper) {
                        sb.append(String.format(template,fm[0],selectedValue,fm[1]));
                    }
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(sb.toString());
                    clipboard.setContents(selection, SftpPushCommandAction.this);
                    NotificationUtils.info("scp命令拷贝成功","",anActionEvent.getProject());
                });
            }

        });
        listPopup.showInFocusCenter();

        ///home/@chenweidian-yfzx/Code/FMS/FMSS_xfunds/xfunds/WebContent/js/fund
        ///cygdrive/d/app/xfunds.ear/xfunds.war/js/fund/fundSubscribeInput.js


    }


    String targetBase = "/cygdrive/d/app/xfunds.ear/xfunds.war/";

    public String convert(String originPath) {
//        String originPath = "/home/@chenweidian-yfzx/Code/FMS/FMSS_xfunds/xfunds/WebContent/js/fund/fundSubscribeInput.js";
        String identify = "/WebContent/";
//        Path origin = Paths.get(originPath);
        if (originPath.indexOf(identify) != -1) {
            String relPath = originPath.substring(originPath.indexOf(identify) + identify.length());
            Path path = Paths.get(targetBase, relPath);
            return path.toString();
        }
        return null;

    }


    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
