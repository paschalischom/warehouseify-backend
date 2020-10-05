package com.uoi.spmsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.cloud.firestore.CollectionReference;

/*
 * More info on
 * https://www.concretepage.com/jackson-api/jackson-jsonignore-jsonignoreproperties-and-jsonignoretype
 */
@JsonIgnoreProperties({"childrenRef"})
public class RTreeNode extends RTreeBase {
    MBR mbr;
    CollectionReference childrenRef;

    public RTreeNode(String id, int distance, MBR mbr, CollectionReference childrenRef) {
        super(id, distance);
        this.mbr = mbr;
        this.childrenRef = childrenRef;
    }

    public RTreeNode() {
    }

    public MBR getMbr() {
        return mbr;
    }

    public void setMbr(MBR mbr) {
        this.mbr = mbr;
    }

    public CollectionReference getChildrenRef() {
        return childrenRef;
    }

    public void setChildrenRef(CollectionReference childrenRef) {
        this.childrenRef = childrenRef;
    }
}
