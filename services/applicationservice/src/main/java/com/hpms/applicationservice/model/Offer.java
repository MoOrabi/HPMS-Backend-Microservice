package com.hpms.applicationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.applicationservice.constants.OfferStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Offer extends AppEvent {

    private LocalDateTime deadlineAt;

    private String documentByCompany;

    private double sizeOfDocumentByCompany;

    private String documentByJobSeeker;

    private double sizeOfDocumentByJobSeeker;

    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.Delivered;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private Set<OfferComment> comments;

    private boolean viewed = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnore
    private JobApplication application;

    @PreRemove
    private void deleteOfferFromApplication() {
        application.setOffer(null);
    }
}
