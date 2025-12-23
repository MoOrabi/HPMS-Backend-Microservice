package com.hpms.jobservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.jobservice.constants.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {
            CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST,CascadeType.REFRESH
    })
    @JoinColumn(name="job_post_id")
    @JsonIgnore
    private JobPost jobPost;

    @Column(nullable = false)
    private String question ;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType ;

    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionAnswer> questionAnswers;


}
