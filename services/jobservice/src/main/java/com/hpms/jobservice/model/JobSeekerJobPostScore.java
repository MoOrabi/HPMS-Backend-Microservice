package com.hpms.jobservice.model;

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

    float score;

}
