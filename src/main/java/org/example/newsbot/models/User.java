package org.example.newsbot.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private int id;

    @Column(name = "last_update")
    private Timestamp lastUpdate;

    private boolean subscribed;

    @Column(name = "all_news")
    private boolean allNews;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tag",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();

    public User() {
    }

    public User(int id) {
        this.id = id;
        lastUpdate = Timestamp.valueOf(LocalDateTime.now());
        subscribed = false;
        allNews = false;
    }

    public User(int id, boolean subscribed, boolean allNews) {
        this(id);
        this.subscribed = subscribed;
        this.allNews = allNews;
    }

    public int getId() {
        return id;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public boolean isAllNews() {
        return allNews;
    }

    public void setAllNews(boolean allNews) {
        this.allNews = allNews;
    }
}
