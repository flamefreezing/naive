package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_metrics")
public class PostMetrics {

    @Id
    private UUID postId;

    @Builder.Default
    private Integer likesCount = 0;

    @Builder.Default
    private Integer commentsCount = 0;

    @Builder.Default
    private Integer sharesCount = 0;

    @Builder.Default
    private Integer viewsCount = 0;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
