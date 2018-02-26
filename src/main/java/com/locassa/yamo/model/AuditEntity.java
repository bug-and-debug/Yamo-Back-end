package com.locassa.yamo.model;

import com.locassa.yamo.util.YamoUtils;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class AuditEntity implements Serializable {

    private Date created;

    @Version
    private Date updated;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PrePersist
    private void onCreate() {
        setCreated(YamoUtils.now());
    }

}
