package com.ediattah.rezoschool.Utils;

/**
 * Created by ujs on 17/10/2017.
 */

public interface OnTaskResult {
    void onTaskCompleted();
    void onTaskStarted();
    void onTaskUpdated(Integer... progress);
    void onTaskError(String result);
}
