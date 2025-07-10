package ru.practicum.item;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.user.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter @Setter @ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String url;

    @Column(name = "resolved_url")
    private String resolvedUrl;

    @Column(name = "mime_type")
    private String mimeType;

    @Column
    private String title;

    @Column(name = "has_image")
    private boolean hasImage;

    @Column(name = "has_video")
    private boolean hasVideo;

    @Column(name = "date_resolved")
    private Instant dateResolved;

    @Column(name = "unread")
    private boolean unread = true;

    @ElementCollection
    @CollectionTable(name="tags", joinColumns=@JoinColumn(name="item_id"))
    @Column(name="name")
    private Set<String> tags = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
