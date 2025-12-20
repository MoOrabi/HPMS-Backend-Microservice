package com.hpms.userservice.model.jobseeker;


import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class JobSeekerJobPostKey implements Serializable {

    private UUID jobSeekerId;

    private UUID jobPostId;

}
