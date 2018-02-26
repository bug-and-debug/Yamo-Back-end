package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.comparator.RouteStepComparator;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "route")
public class Route extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    @OrderBy("sequence_order ASC")
    @SortComparator(value = RouteStepComparator.class)
    private SortedSet<RouteStep> steps = new TreeSet<>();

    @ManyToOne
    @JsonIgnore
    private User user;

    @Transient
    private Long userId;

    private int counter = 0;

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

    public SortedSet<RouteStep> getSteps() {
        return steps;
    }

    public void setSteps(SortedSet<RouteStep> steps) {
        this.steps = steps;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return null == user ? -1 : user.getUuid();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
