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
     * @param leftstr
     * @param rightStr
     * @param fileType
     * @param project
     */
    public static void compare(String leftstr, String rightStr, FileType fileType, Project project) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DocumentContent left = contentFactory.create(leftstr, fileType);
        DocumentContent right = contentFactory.create(rightStr, fileType);
        MutableDiffRequestChain mutableDiffRequestChain = new MutableDiffRequestChain(left, right);
        DiffManager.getInstance().showDiff(project, mutableDiffRequestChain, DiffDialogHints.DEFAULT);

    }
}
