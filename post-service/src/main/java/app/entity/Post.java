package app.entity;

import common.constant.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private List<PostMedia> mediaList = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "post_mentions", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "mention")
    private List<String> mentions = new ArrayList<>();

    @Builder.Default
    private Boolean isEdited = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Visibility visibility =  Visibility.PUBLIC;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
