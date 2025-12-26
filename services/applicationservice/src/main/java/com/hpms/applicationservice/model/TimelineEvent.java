package com.hpms.applicationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.applicationservice.constants.TimelineEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimelineEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TimelineEventType type;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "eventId")
    private AppEvent content;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnore
    private JobApplication application;

    public TimelineEvent(TimelineEventType type, AppEvent content, UUID creatorId, LocalDateTime createdAt,
                         LocalDateTime updatedAt, JobApplication application) {
        this.type = type;
        this.content = content;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.application = application;
    }
}
