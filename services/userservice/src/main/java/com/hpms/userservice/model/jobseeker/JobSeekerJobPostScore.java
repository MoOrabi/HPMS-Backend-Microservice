package com.hpms.userservice.model.jobseeker;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerJobPostScore implements Serializable {

    @EmbeddedId
    private JobSeekerJobPostKey key;

    @ManyToOne
    @MapsId("jobSeekerId")
    @JoinColumn(name = "job_seeker_id")
    private JobSeeker jobSeeker;

//    @ManyToOne
//    @MapsId("jobPostId")
//    @JoinColumn(name = "job_post_id")
//    private JobPost jobPost;

    float score;

}
