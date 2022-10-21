package cn.boz.jb.plugin.idea.action.export;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.action.export.bean.FileWrapper;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ExportBaseAction extends AnAction {


    protected void exportSpdAction(AnActionEvent anActionEvent,List<FileWrapper> fileWrappers,boolean spdtoSql,boolean bom,String hint){
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, anActionEvent.getProject(), null);
        if (virtualFile == null) {
            return;
        }
        List<FileWrapper> wrappers;
        if(spdtoSql){
             wrappers=fileWrappers.stream().filter(item -> item.getPath().endsWith(".spd")).map(item -> {
                String orgPath = item.getPath();
                String nameSub = orgPath.substring(0, orgPath.lastIndexOf(".spd")) + ".sql";
                ChartPanel cp = new ChartPanel();
                cp.loadFromInputStream(new ByteArrayInputStream(item.getContent()));
                List<String> sqls = cp.generateSql();
                String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                byte[] bytes = joiningSql.getBytes(StandardCharsets.UTF_8);
                item.setContent(bytes);
                item.setPath(nameSub);
                return item;
            }).collect(Collectors.toList());
        }else{
            wrappers=new ArrayList<>(fileWrappers);
        }


        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {

            try {
                LocalDateTime now = LocalDateTime.now();

                String yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);

                String filename = "GitExport_" + yyyyMMdd + ".zip";
                VirtualFile exportDest = virtualFile.createChildData(null, filename);

                ZipOutputStream zos = new ZipOutputStream(exportDest.getOutputStream(null));

                for (FileWrapper fileWrapper : wrappers) {
                    zos.putNextEntry(new ZipEntry(fileWrapper.getPath()));
                    if(bom){
                        zos.write(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf});
                    }
                    zos.write(fileWrapper.getContent(), 0, fileWrapper.getContent().length);
                    zos.closeEntry();
                }
                zos.close();

                Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
                spdEditorNotification.setTitle("exported");
                spdEditorNotification.addAction(new AnAction() {
                    @Override
                    public @Nullable String getTemplateText() {
                        return "Open";
                    }

                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        File file = new File(virtualFile.getPath());
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                    }
                });
                spdEditorNotification.setContent("Export as "+hint+" in " + exportDest.getPath());
                spdEditorNotification.notify(anActionEvent.getProject());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



}
