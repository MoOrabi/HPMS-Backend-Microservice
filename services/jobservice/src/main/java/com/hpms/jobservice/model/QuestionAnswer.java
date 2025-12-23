package com.hpms.jobservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    private String answer;

    @ManyToOne (cascade = { CascadeType.DETACH,CascadeType.MERGE,
                            CascadeType.PERSIST,CascadeType.REFRESH
    })
    @JoinColumn(name="question_id")
    private Question question;

/*
    @ManyToOne(fetch = FetchType.LAZY,cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH
    })
    @JoinColumn(name="application_id")
    private JobApplication jobApplication;
*/

}
