package com.mispower.common.file;

import java.util.List;

/**
 * multi file event
 *
 * @author wuguolin
 */
public class Event<T> {

    /**
     * list of expire file's name
     */
    private final List<String> expireFiles;

    /**
     * list of active  file's name
     */
    private final List<String> activeFiles;


    /**
     * policy when the file has been dealt.Default is "delete".
     */
    private PolicyEnum policy;
    /**
     * if policy is "change", change file postfix to this when the file has been dealt
     */
    private String completed;

    /**
     * event data
     */
    private List<T> data;

    /**
     * constructor
     * @param policy policy
     * @param completed completed
     * @param expireFiles expireFiles
     * @param activeFiles  activeFiles
     * @param data data
     */
    public Event(final PolicyEnum policy, final String completed,List<String> expireFiles, List<String> activeFiles,  List<T> data) {
        this.policy = policy;
        this.expireFiles = expireFiles;
        this.activeFiles = activeFiles;
        this.completed = completed;
        this.data = data;
    }

    public List<String> getExpireFiles() {
        return expireFiles;
    }

    public List<String> getActiveFiles() {
        return activeFiles;
    }

    public PolicyEnum getPolicy() {
        return policy;
    }

    public String getCompleted() {
        return completed;
    }
    public List<T> getData() {
        return data;
    }
}
