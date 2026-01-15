package com.hpms.recommendationservice.service;

import com.hpms.recommendationservice.dto.RecommendationScore;
import com.hpms.recommendationservice.model.JobPostProfile;
import com.hpms.recommendationservice.model.JobSeekerProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationEngine {

    // Weight factors for scoring
    private static final double SKILL_MATCH_WEIGHT = 0.50;
    private static final double EXPERIENCE_MATCH_WEIGHT = 0.25;
    private static final double JOB_TYPE_MATCH_WEIGHT = 0.10;
    private static final double LOCATION_MATCH_WEIGHT = 0.15;

    /**
     * Calculate how well a job matches a job seeker
     */
    public RecommendationScore calculateJobMatchScore(
            JobSeekerProfile seeker,
            JobPostProfile job) {

        List<String> reasons = new ArrayList<>();
        double totalScore = 0.0;

        // 1. Skill Match (40% weight)
        double skillScore = calculateSkillMatch(seeker.getSkills(), job.getSkills());
        totalScore += skillScore * SKILL_MATCH_WEIGHT;
        if (skillScore > 0.3) {
            reasons.add(String.format("%.0f%% skill match", skillScore * 100));
        }

        // 2. Experience Match (25% weight)
        double expScore = calculateExperienceMatch(
                seeker.getYearsOfExperience(),
                job.getMinExperienceYears(),
                job.getMaxExperienceYears()
        );
        totalScore += expScore * EXPERIENCE_MATCH_WEIGHT;
        if (expScore > 0.8) {
            reasons.add("Experience matches requirements");
        }

        // 3. Job Type Match (10% weight)
        double jobTypeScore = calculateJobTypeMatch(
                seeker.getJobTypesInterestedIn(),
                job.getJobType()
        );
        totalScore += jobTypeScore * JOB_TYPE_MATCH_WEIGHT;
        if (jobTypeScore == 1.0) {
            reasons.add("Matches preferred job type");
        }

        // 4. Location Match (15% weight)
        double locationScore = calculateLocationMatch(seeker, job);
        totalScore += locationScore * LOCATION_MATCH_WEIGHT;

        return new RecommendationScore(totalScore, reasons);
    }

    /**
     * Calculate how well a candidate matches a job
     */
    public RecommendationScore calculateCandidateMatchScore(
            JobPostProfile job,
            JobSeekerProfile seeker) {

        // Same logic as calculateJobMatchScore but from job's perspective
        return calculateJobMatchScore(seeker, job);
    }

    /**
     * Calculate skill overlap using Jaccard similarity
     */
    private double calculateSkillMatch(Set<String> seekerSkills, Set<String> jobSkills) {
        if (seekerSkills == null || seekerSkills.isEmpty()
                || jobSkills == null || jobSkills.isEmpty()) {
            return 0.0;
        }

        // Normalize
        Set<String> seeker = seekerSkills.stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        Set<String> job = jobSkills.stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        // Shared vocabulary
        Set<String> vocabulary = new HashSet<>(seeker);
        vocabulary.addAll(job);

        int dotProduct = 0;
        int seekerMagnitude = 0;
        int jobMagnitude = 0;

        for (String skill : vocabulary) {
            int seekerVal = seeker.contains(skill) ? 1 : 0;
            int jobVal = job.contains(skill) ? 1 : 0;

            dotProduct += seekerVal * jobVal;
            seekerMagnitude += seekerVal;
            jobMagnitude += jobVal;
        }

        if (seekerMagnitude == 0 || jobMagnitude == 0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(seekerMagnitude) * Math.sqrt(jobMagnitude));
    }

    private String normalize(String skill) {
        if(skill==null) {
            return null;
        }
        return skill.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }


    /**
     * Check if experience matches job requirements
     */
    private double calculateExperienceMatch(
            String yearsOfExp,
            int minRequired,
            int maxRequired) {

        if (yearsOfExp == null || yearsOfExp.isEmpty()) {
            return 0.5; // Neutral score if experience not specified
        }

        try {
            int years = Integer.parseInt(yearsOfExp.replaceAll("[^0-9]", ""));

            if (years >= minRequired && years <= maxRequired) {
                return 1.0; // Perfect match
            } else if (years >= minRequired - 1 && years <= maxRequired + 1) {
                return 0.8; // Close match
            } else if (years < minRequired) {
                return Math.max(0, 1.0 - (minRequired - years) * 0.2); // Penalize under-qualified
            } else {
                return Math.max(0, 1.0 - (years - maxRequired) * 0.1); // Slight penalty for over-qualified
            }
        } catch (Exception e) {
            return 0.5;
        }
    }

    /**
     * Check if job type matches seeker's preferences
     */
    private double calculateJobTypeMatch(Set<String> interestedTypes, String jobType) {
        if (interestedTypes == null || interestedTypes.isEmpty()) {
            return 0.5; // Neutral if no preference
        }

        return interestedTypes.contains(jobType) ? 1.0 : 0.0;
    }

    /**
     * Simple location match
     */
    private double calculateLocationMatch(JobSeekerProfile seeker, JobPostProfile job) {
        // If seeker is ready to relocate, location doesn't matter
        if (seeker.isReadyToRelocate() || job.isRemote()) {
            return 0.8;
        }

        // Otherwise, would need to compare actual locations
        // We may use latitude and longitude later on
        String seekerCity = normalize(seeker.getCity());
        String seekerCountry = normalize(seeker.getCountry());
        String jobCity = normalize(job.getCity());
        String jobCountry = normalize(job.getCountry());

        if(seekerCountry != null && seekerCountry.equals(jobCountry)) {
            if(seekerCity != null && seekerCity.equals(jobCity)) {
                return 1;
            } else {
                return 0.8;
            }
        } else {
            return 0.5;
        }
    }
}


