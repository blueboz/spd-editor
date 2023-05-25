package cn.boz.jb.plugin.idea.utils;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class CompareUtils {

    /**
     * 这里的Project 一定不能送ProjectManager.getDefaultProject();
     *
     * @param leftstr
     * @param rightStr
     * @param fileType
     * @param project
     */
    public static void compare(String oldver, String newVer, FileType fileType, Project project) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DocumentContent left = contentFactory.create(oldver, fileType);
        DocumentContent right = contentFactory.create(newVer, fileType);
        MutableDiffRequestChain mutableDiffRequestChain = new MutableDiffRequestChain(left, right);
        DiffManager.getInstance().showDiff(project, mutableDiffRequestChain, DiffDialogHints.DEFAULT);

    }

    public static void compare(String oldver, String oldTitle, String newVer, String newTitle, FileType fileType, Project project, String windowTitle) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DocumentContent left = contentFactory.create(oldver, fileType);
        DocumentContent right = contentFactory.create(newVer, fileType);
        MutableDiffRequestChain mutableDiffRequestChain = new MutableDiffRequestChain(left, right);
        mutableDiffRequestChain.setTitle1(oldTitle);
        mutableDiffRequestChain.setTitle2(newTitle);
        mutableDiffRequestChain.setWindowTitle(windowTitle);
        DiffManager.getInstance().showDiff(project, mutableDiffRequestChain, DiffDialogHints.DEFAULT);
    }
}

