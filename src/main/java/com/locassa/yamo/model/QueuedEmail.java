package com.locassa.yamo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class QueuedEmail extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User targetUser;

    private String subject;

    @Column(name = "html_content", columnDefinition = "TEXT CHARACTER SET 'utf8mb4' collate utf8mb4_unicode_ci NULL DEFAULT NULL")
    private String htmlContent;

    private Date scheduledDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
