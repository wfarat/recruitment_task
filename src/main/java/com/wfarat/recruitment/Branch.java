package com.wfarat.recruitment;

public class Branch {
    private String name;
    private Commit commit;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastCommitSha() {
        return commit.getSha();
    }
    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
