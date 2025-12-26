package com.hpms.applicationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.applicationservice.constants.EvaluationType;
import com.hpms.applicationservice.constants.TechnicalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation extends AppEvent {

    private String title;

    private String link;

    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    @Enumerated(EnumType.STRING)
    private TechnicalType technicalType;

    private boolean viewed = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnore
    private JobApplication application;


    public Evaluation(String title, String link, EvaluationType evaluationType, TechnicalType technicalType, boolean viewed, JobApplication application, UUID creatorId) {
        super(creatorId);
        this.title = title;
        this.link = link;
        this.evaluationType = evaluationType;
        this.technicalType = technicalType;
        this.viewed = viewed;
        this.application = application;
    }
}
