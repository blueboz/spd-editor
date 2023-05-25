package cn.boz.jb.plugin.idea.action.export;

import cn.boz.jb.plugin.idea.action.export.bean.FileWrapper;
import com.intellij.openapi.ListSelection;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ByteBackedContentRevision;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GitGetExportAsSqlAction extends ExportBaseAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        //s
        ListSelection<Change> changeListSelection = anActionEvent.getData(VcsDataKeys.CHANGES_SELECTION);
        if (changeListSelection == null) {
            return;
        }
        if (changeListSelection.isEmpty()) {
            return;
        }

        ListPopup listPopup = JBPopupFactory.getInstance()
            .createListPopup(new BaseListPopupStep<String>("Before Or After Revision",
                    "Before Revision", "After Revision") {

                @Override
                public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                    if (finalChoice) {
                        return doFinalStep(() -> doRun(selectedValue));
                    }
                    return PopupStep.FINAL_CHOICE;
                }

                private void doRun(String selectedValue) {
                    List<FileWrapper> wrappers = new ArrayList<>();
                    List<Change> changes = new ArrayList<>();
                    if (changeListSelection.getSelectedIndex() != 0) {
                        changes.add(changeListSelection.getList().get(changeListSelection.getSelectedIndex()));
                    } else {
                        changes.addAll(changeListSelection.getList());
                    }
                    if ("Before Revision".equals(selectedValue)) {
                        for (Change change : changes) {
                            ContentRevision beforeRevision = change.getBeforeRevision();
                            FileStatus fileStatus = change.getFileStatus();
                            if (FileStatus.ADDED == fileStatus) {
                            } else {
                                try {
                                    wrappers.add(new FileWrapper(beforeRevision.getFile().getPath(), beforeRevision.getContent().getBytes(StandardCharsets.UTF_8)));
                                } catch (VcsException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    } else {
                        for (Change change : changes) {
                            ContentRevision afterRevision = change.getAfterRevision();
                            FileStatus fileStatus = change.getFileStatus();
                            if (FileStatus.DELETED == fileStatus) {
                                //文件是被删除的
                                //文件仙剑
                            } else {
                                try {
                                    wrappers.add(new FileWrapper(afterRevision.getFile().getPath(), afterRevision.getContent().getBytes(StandardCharsets.UTF_8)));
                                } catch (VcsException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    exportSpdAction(anActionEvent,wrappers,true,true,"SQL");


                }
            });

        InputEvent inputEvent = anActionEvent.getInputEvent();
        if (inputEvent instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) inputEvent;
            listPopup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
        } else {
            listPopup.showInFocusCenter();
        }
    }


}
