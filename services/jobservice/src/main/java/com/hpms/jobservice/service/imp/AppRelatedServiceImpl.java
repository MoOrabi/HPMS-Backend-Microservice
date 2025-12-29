package com.hpms.jobservice.service.imp;

import com.hpms.jobservice.dto.QuestionAnswerDto;
import com.hpms.jobservice.dto.app.ApplicationAnswerDTO;
import com.hpms.jobservice.dto.app.JobPostCompanyRecruitersAndCreator;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.model.Question;
import com.hpms.jobservice.repository.JobPostRepository;
import com.hpms.jobservice.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppRelatedServiceImpl {
    private final JobPostRepository jobPostRepository;
    private final QuestionRepository questionRepository;

    public JobPostCompanyRecruitersAndCreator getJobPostCompanyRecruitersAndCreator(UUID postId) {
        Optional<JobPost> optionalJobPost = jobPostRepository.findById(postId);
        if (optionalJobPost.isEmpty()) {
            return JobPostCompanyRecruitersAndCreator
                    .builder().build();
        }
        JobPost jobPost = optionalJobPost.get();
        return JobPostCompanyRecruitersAndCreator.builder()
                .companyId(jobPost.getCompanyId())
                .recruiterIds(jobPost.getRecruiterIds())
                .creatorId(jobPost.getCreatorId())
                .build();
    }

    public List<ApplicationAnswerDTO> getQuestionDetailsFromAnswers(List<QuestionAnswerDto> answers) {
        List<ApplicationAnswerDTO> applicationAnswerDTOS = new ArrayList<>();
        answers.forEach(answer -> {
            Optional<Question> optionalQuestion = questionRepository.findById(answer.getQuestionId());
            optionalQuestion.ifPresent(question -> applicationAnswerDTOS.add(ApplicationAnswerDTO.builder()
                    .questionAnswer(answer.getQuestionAnswer())
                    .questionId(answer.getQuestionId())
                    .questionType(question.getQuestionType().name())
                    .question(question.getQuestion())
                    .build()));

        });
        return applicationAnswerDTOS;
    }
}
