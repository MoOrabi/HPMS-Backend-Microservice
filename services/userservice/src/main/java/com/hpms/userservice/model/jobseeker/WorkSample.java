package com.hpms.userservice.model.jobseeker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkSample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String photo;

    private String title;

    private String description;

    private String link;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "job-seeker-id", nullable = false)
    private JobSeeker jobSeeker;

    public WorkSample(String pathWithFileName, String title, String description, String link, JobSeeker jobSeeker) {
        this.photo = pathWithFileName;
        this.title = title;
        this.description = description;
        this.link = link;
        this.jobSeeker = jobSeeker;
    }
}
