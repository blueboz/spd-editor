package cn.boz.jb.plugin.idea.utils;

import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;

public class ChangeUtils {

    public static String getChangeAfterContent(Change change) {
        ContentRevision afterRevision = change.getAfterRevision();
        if (afterRevision == null) {
            return "";
        }
        try {
            return afterRevision.getContent();
        } catch (VcsException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getChangeAfterTitle(Change change) {
        ContentRevision afterRevision = change.getAfterRevision();
        if (afterRevision == null) {
            return "";
        }
        return afterRevision.getRevisionNumber().asString();
    }

    public static String getChangeAfterTitleWithName(Change change) {
        ContentRevision afterRevision = change.getAfterRevision();
        if (afterRevision == null) {
            return "";
        }
        return afterRevision.getRevisionNumber().asString() + " " + afterRevision.getFile().getName();
    }

    public static String getChangeBeforeContent(Change change) {
        ContentRevision beforeRev = change.getBeforeRevision();
        if (beforeRev == null) {
            return "";
        }
        try {
            return beforeRev.getContent();
        } catch (VcsException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getChangeBeforeTitle(Change change) {
        ContentRevision beforeRevision = change.getBeforeRevision();
        if (beforeRevision == null) {
            return "";
        }
        return beforeRevision.getRevisionNumber().asString();

    }

    public static String getChangeAvaiableFileName(Change change) {
        FileStatus fileStatus = change.getFileStatus();
        if (fileStatus == FileStatus.DELETED) {
            return change.getBeforeRevision().getFile().getName();
        }
        return change.getAfterRevision().getFile().getName();
    }

    public static String getChangeBeforeTitleWithName(Change change) {
        ContentRevision beforeRevision = change.getBeforeRevision();
        if (beforeRevision == null) {
            return "";
        }
        return beforeRevision.getRevisionNumber().asString() + " " + beforeRevision.getFile().getName();

    }

}
