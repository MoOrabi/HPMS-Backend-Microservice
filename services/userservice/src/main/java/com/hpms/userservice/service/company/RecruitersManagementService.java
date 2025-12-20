package com.hpms.userservice.service.company;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.company.RecruiterEmailAndTitle;
import com.hpms.userservice.dto.company.RecruiterInfo;
import com.hpms.userservice.dto.company.RecruiterInvitationInfo;
import com.hpms.userservice.mapper.RecruiterInfoMapper;
import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.model.company.AddRecruiterRequest;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.repository.company.AddRecruiterRequestRepository;
import com.hpms.userservice.service.EmailService;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hpms.userservice.utils.AppConstants.RECRUITER_FORM_PREFIX;


@Service
@Log
public class RecruitersManagementService {

    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AddRecruiterRequestRepository addRecruiterRequestRepository;
    @Autowired
    private FrequentlyUsed frequentlyUsed;
    @Autowired
    private RecruiterInfoMapper recruiterInfoMapper;

    public ApiResponse<?> getAllRecruiters(UUID companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isEmpty()) {
            ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no company for the entered id")
                    .build();
        }

        Company company = optionalCompany.get();

        List<Recruiter> recruiters = recruiterRepository.getRecruitersForCompany(company.getId());
        List<RecruiterInfo> recruitersInfo = recruiterInfoMapper.recruiterListToInfoList(recruiters);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(recruitersInfo)
                .build();
    }

    public ApiResponse<?> inviteRecruiter(String token, String email, String jobTitle) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        if (userRepository.getUserByUsername(email).isPresent()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("The email you entered already exists in our website, no need to invite them")
                    .build();
        }

        sendEmailForRecruiterToJoin(company, email, jobTitle);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Chosen recruiter got an email from you to join")
                .build();
    }

    private void sendEmailForRecruiterToJoin(Company company, String recruiterEmail,
                                             String jobTitle) {
        String formLink = RECRUITER_FORM_PREFIX + generateRecruiterInvitationRequest(company, recruiterEmail,
                jobTitle)
                .getToken();

        emailService.sendRecruiterInvitation(recruiterEmail, company.getName(), formLink);
    }

    private AddRecruiterRequest generateRecruiterInvitationRequest(Company company, String recruiterEmail,
                                                                   String jobTitle) {
        Optional<AddRecruiterRequest> optionalRequest = addRecruiterRequestRepository
                .getByCompanyAndRecruiterEmail(company, recruiterEmail);

        AddRecruiterRequest request;
        if (optionalRequest.isPresent()) {
            request = optionalRequest.get();
            request.setValid(true);
            request.setExpirationDate(LocalDateTime.now().plusDays(1));
            request.setUpdatedAt(LocalDateTime.now());
        } else {
            String token = UUID.randomUUID().toString();
            request = new AddRecruiterRequest(company, recruiterEmail, jobTitle,
                    token, LocalDateTime.now().plusDays(1));
        }
        addRecruiterRequestRepository.save(request);
        return request;
    }

    public ApiResponse<?> deleteRecruiterInvitation(String token, String recruiterEmail) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        Optional<AddRecruiterRequest> optionalAddRecruiterRequest = addRecruiterRequestRepository
                .getByCompanyAndRecruiterEmail(company, recruiterEmail);

        if (optionalAddRecruiterRequest.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("No requests available for the entered user id")
                    .build();
        }

        AddRecruiterRequest addRecruiterRequest = optionalAddRecruiterRequest.get();

        if (!addRecruiterRequest.getCompany().getUsername().equals(company.getUsername())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authorization to delete this request")
                    .build();
        }

        addRecruiterRequest.setValid(false);
        addRecruiterRequestRepository.save(addRecruiterRequest);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Chosen recruiter is deleted")
                .build();
    }

    public ApiResponse<?> getRecruitersInvitations(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        List<AddRecruiterRequest> invitations = addRecruiterRequestRepository
                .getByCompany(company);


        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(invitations)
                .build();
    }

    public ApiResponse<?> getValidInvitations(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        List<AddRecruiterRequest> invitations = addRecruiterRequestRepository
                .getByCompanyIfValidAndNotExpired(company);

        List<RecruiterInvitationInfo> invitationDTOs = new ArrayList<>();

        for (AddRecruiterRequest invitation:
             invitations) {
            RecruiterInvitationInfo recruiterInvitationInfo = new
                    RecruiterInvitationInfo(invitation.getId(), invitation.getRecruiterEmail());
            invitationDTOs.add(recruiterInvitationInfo);
        }


        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(invitationDTOs)
                .build();
    }


    public ApiResponse<?> deleteRecruiter(String token, String email) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        Optional<Recruiter> optionalRecruiter = recruiterRepository.getByUsername(email);

        if (optionalRecruiter.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("No recruiters available with entered email")
                    .build();
        }

        Recruiter recruiter = optionalRecruiter.get();

        if (!recruiter.getCompany().equals(company)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authorization to delete this recruiter")
                    .build();
        }

        deleteRecruiterFromDatabase(recruiter);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Chosen recruiter is deleted")
                .build();
    }

    private void deleteRecruiterFromDatabase(Recruiter recruiter){
        recruiterRepository.deleteRecruiterFromCompany(recruiter.getId());
        recruiterRepository.delete(recruiter);
    }

    public ApiResponse<?> deleteAllRecruiters(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        boolean isRecruitersDeleted = recruiterRepository.deleteAllForCompany(company.getId());
        if (!isRecruitersDeleted) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("No recruiters available for that company")
                    .build();
        }
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Recruiters of the company deleted")
                .build();
    }

    public ApiResponse<?> getMyAllRecruiters(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        List<Recruiter> recruiters = recruiterRepository.getRecruitersForCompany(company.getId());
        List<RecruiterInfo> recruitersInfo = recruiterInfoMapper.recruiterListToInfoList(recruiters);

        if (recruiters.isEmpty()) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("No recruiters available for that company")
                    .build();
        }
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(recruitersInfo)
                .build();
    }

    public ApiResponse<?> updateRecruiter(String token, UUID recruiterId,
                                          RecruiterEmailAndTitle recruiterEmailAndTitle) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(recruiterId);

        if (optionalRecruiter.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no recruiter for the entered Email")
                    .build();
        }

        Recruiter recruiter = optionalRecruiter.get();

        if(!company.equals(recruiter.getCompany())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You are not authorized for this action")
                    .build();
        }

        recruiter.setUsername(recruiterEmailAndTitle.getEmail());
        recruiter.setJobTitle(recruiterEmailAndTitle.getJobTitle());

        recruiter = recruiterRepository.save(recruiter);

        RecruiterInfo recruiterInfo = recruiterInfoMapper.recruiterToInfo(recruiter);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(recruiterInfo)
                .message("Recruiter Updated Successfully")
                .build();

    }
}
