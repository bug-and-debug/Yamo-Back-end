package com.locassa.yamo.model.summary;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "route")
public class RouteSummary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private String name;

    private Date created;

    @Transient
    private boolean popularRoute = false;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof RouteSummary && ((RouteSummary) obj).getUuid().equals(uuid);
    }

    public boolean isPopularRoute() {
        return popularRoute;
    }

    public void setPopularRoute(boolean popularRoute) {
        this.popularRoute = popularRoute;
    }
}
