package cn.boz.jb.plugin.idea.action;

import com.intellij.openapi.ListSelection;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
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

public class GitGetAction extends AnAction {
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
                                    ContentRevision afterRevision = change.getAfterRevision();
                                    String path = afterRevision.getFile().getPath();
                                    File file = new File(path);
                                    if (file.exists()) {
                                        file.delete();

                                    }                                    //文件仙剑
                                    reloadFromDisk(anActionEvent.getProject());

                                } else {
                                    toRevision(beforeRevision, anActionEvent.getProject());
                                }

                            }
                        } else {
                            for (Change change : changes) {
                                ContentRevision afterRevision = change.getAfterRevision();
                                FileStatus fileStatus = change.getFileStatus();
                                if (FileStatus.DELETED == fileStatus) {
                                    //文件是被删除的
                                    ContentRevision beforeRevision = change.getBeforeRevision();
                                    String path = beforeRevision.getFile().getPath();
                                    File file = new File(path);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    reloadFromDisk(anActionEvent.getProject());
                                    //文件仙剑
                                } else {
                                    toRevision(afterRevision, anActionEvent.getProject());
                                }
                            }
                        }

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

    public void reloadFromDisk(Project project) {
        VirtualFile baseDir = project.getBaseDir();
        baseDir.refresh(true, true);
    }

    public void toRevision(ContentRevision toRevision, Project project) {
        FilePath filePath = toRevision.getFile();
        VirtualFile virtualFile = filePath.getVirtualFile();
        if (virtualFile == null) {
            File file = new File(filePath.getPath());
            if (!file.exists()) {
                try {
                    file.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
                if (toRevision instanceof ByteBackedContentRevision) {
                    ByteBackedContentRevision bbcr = (ByteBackedContentRevision) toRevision;
                    fileOutputStream.write(bbcr.getContentAsBytes());
                } else {
                    fileOutputStream.write(toRevision.getContent().getBytes(StandardCharsets.UTF_8));
                }
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            reloadFromDisk(project);

        } else {
            Application application = ApplicationManager.getApplication();
            application.saveAll();
            //直接覆盖
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    if (toRevision instanceof ByteBackedContentRevision) {
                        ByteBackedContentRevision bbcr = (ByteBackedContentRevision) toRevision;
                        virtualFile.setBinaryContent(bbcr.getContentAsBytes());
                    } else {
                        Charset charset = toRevision.getFile().getCharset();
                        virtualFile.setBinaryContent(toRevision.getContent().getBytes(charset));
                    }
                    virtualFile.setBinaryContent(toRevision.getContent().getBytes(StandardCharsets.UTF_8));
                    reloadFromDisk(project);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (VcsException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
