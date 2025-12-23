package com.hpms.userservice.service.company;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.jobservice.service.client.JobServiceClient;
import com.hpms.userservice.dto.JobPostDto;
import com.hpms.userservice.dto.company.*;
import com.hpms.userservice.mapper.company.AboutInfoMapper;
import com.hpms.userservice.mapper.company.BasicsInfoMapper;
import com.hpms.userservice.mapper.company.CompanyMapper;
import com.hpms.userservice.model.*;
import com.hpms.userservice.repository.AboutCompanyRepository;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.shared.LocationRepository;
import com.hpms.userservice.repository.shared.SocialIconsInfoRepository;
import com.hpms.userservice.repository.shared.UserSocialLinkRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@AllArgsConstructor
public class CompanyProfileServices {

    private CompanyRepository companyRepository;

    private final AboutCompanyRepository aboutCompanyRepository;

    private final JwtTokenUtils tokenUtils;

    private final BasicsInfoMapper basicsInfoMapper;

    private final AboutInfoMapper aboutInfoMapper;

    private final CompanyMapper companyMapper;

    private final SocialIconsInfoRepository iconsInfoRepository;

    private final UserSocialLinkRepository userSocialLinkRepository;

    private final LocationRepository locationRepository;

    private final FrequentlyUsed frequentlyUsed;

    private final JobServiceClient jobServiceClient;

    public ApiResponse<?> getAllCompanyInfo(UUID companyId) {
        Company company = companyRepository.getReferenceById(companyId);

        if (company != null) {
            AllCompanyInfoDto allCompanyInfoDto = companyMapper.toDto(company);

            List<JobPostDto> jobPostDtoPage = jobServiceClient.getCompnayRecentJobPosts(companyId);
            allCompanyInfoDto.setJobPosts(jobPostDtoPage);
            return ApiResponse.builder()
                    .ok(true)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .body(allCompanyInfoDto)
                    .status(HttpStatus.ACCEPTED.value())
                    .build();
        }
        return ApiResponse.builder()
                .ok(false)
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    public ApiResponse<?> getCompanySubscriptionChoices(String companyToken) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(companyToken);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        CompanySubscriptionChoices subscriptionChoices = new CompanySubscriptionChoices(
                company.isReceiveNewApplicantsEmails(), company.isReceiveTalentSuggestionsEmails());
        return ApiResponse.builder()
                .ok(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .body(subscriptionChoices)
                .status(HttpStatus.OK.value())
                .build();
    }

    public ApiResponse<?> setCompanySubscriptionChoices(String companyToken, CompanySubscriptionChoices subscriptionChoices) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(companyToken);
        Company company = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            company = (Company) isThereUserFromToken.getBody();
        }

        company.setReceiveNewApplicantsEmails(subscriptionChoices.isReceiveNewApplicantsEmails());
        company.setReceiveTalentSuggestionsEmails(subscriptionChoices.isReceiveTalentSuggestionsEmails());
        companyRepository.save(company);
        return ApiResponse.builder()
                .ok(true)
                .message("Subscription choices saved")
                .body(subscriptionChoices)
                .status(HttpStatus.OK.value())
                .build();
    }

    public ApiResponse<?> checkNumberIsExist(String mobileNumberCountryCode, String mobileNumber) {
        System.out.println(mobileNumberCountryCode + " " + mobileNumber);
        Optional<Company> company = companyRepository.getByMobileNumberCountryCodeAndMobileNumber(mobileNumberCountryCode, mobileNumber);
        System.out.println(mobileNumberCountryCode + " " + mobileNumber);
        if (company.isEmpty()) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .body(false)
                    .build();
        }
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(true)
                .build();
    }

    public ApiResponse<?> updateBasicInfo(String companyToken, CompanyBasicInfoRequest basicInfoDto) {
        String companyUsername = tokenUtils.extractUsername(companyToken.substring(7));
        Company company = companyRepository.getCompanyByUsername(companyUsername);
        if (company != null) {
            company.setTagline(basicInfoDto.getTagline());
            company.setIndustry(basicInfoDto.getIndustry());
            company.setCompanySize(basicInfoDto.getCompanySize());
            updateLocation(basicInfoDto.getMainBranchLocation());
            Company savedCompany = companyRepository.save(company);
            CompanyBasicsInfoResponse companyBasicsInfoResponse = basicsInfoMapper.toDto(savedCompany);
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("Updated")
                    .body(companyBasicsInfoResponse)
                    .build();
        }
        return ApiResponse.getDefaultErrorResponse();
    }


    public ApiResponse<?> updateAboutInfo(String companyToken, CompanyAboutInfoRequest aboutInfoDto) {
        String companyUsername = tokenUtils.extractUsername(companyToken.substring(7));
        Company company = companyRepository.getCompanyByUsername(companyUsername);
        if (company == null) {
            ApiResponse.getDefaultErrorResponse();
        }
        AboutCompany aboutCompany = company.getAboutCompany();
        AboutCompany saveAboutCompany = aboutCompanyRepository.save(UpdateAboutCompany(aboutCompany, aboutInfoDto));
        CompanyAboutInfoResponse dto = aboutInfoMapper.toDto(saveAboutCompany);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Updated")
                .body(dto)
                .build();
    }


    public ApiResponse<?> subscribeToCompany(String userToken, UUID companyId) {

        return null;
    }





    private List<UserSocialLink> createSocialLink(UserSocialLink[] socialLinks) {
        List<UserSocialLink> userSocialLinks = new ArrayList<>();
        for (UserSocialLink socialLink : socialLinks) {
            SocialIconInfo socialIconInfo = iconsInfoRepository.getReferenceById(socialLink.getIcon().getId());
            UserSocialLink userSocialLink = new UserSocialLink();
            userSocialLink.setLink(socialLink.getLink());
            userSocialLink.setIcon(socialIconInfo);
            userSocialLinks.add(userSocialLink);

        }
        return userSocialLinkRepository.saveAll(userSocialLinks);
    }

    private AboutCompany UpdateAboutCompany(AboutCompany aboutCompany, CompanyAboutInfoRequest aboutInfoDto) {
        aboutCompany.setAbout(aboutInfoDto.getAbout());
        aboutCompany.setWebsite(aboutInfoDto.getWebsite());
        aboutCompany.setFoundingDate(aboutInfoDto.getFoundingDate());
        userSocialLinkRepository.deleteAll(aboutCompany.getSocialLinks());
        aboutCompany.setSocialLinks(aboutInfoDto.getSocialLinks());
        return aboutCompany;
    }

    private void updateLocation(Location l) {
        if (l != null && l.getId() == 0) {
            Location location = Location.builder().state(l.getState())
                    .city(l.getCity())
                    .country(l.getCountry())
                    .street(l.getStreet())
                    .build();
            locationRepository.save(location);
            return;
        }
        Location location = locationRepository.getReferenceById(l.getId());
        location.setCity(l.getCity());
        location.setState(l.getState());
        location.setStreet(l.getStreet());
        location.setCountry(l.getCountry());
        locationRepository.save(location);
    }

    private void updateLocationList(Set<Location> subLocations) {
        for (Location l : subLocations) {
            updateLocation(l);
        }
    }

    public ApiResponse<?> saveLogo(String token, MultipartFile logoFile) throws Exception {
        return frequentlyUsed.addFileToUser(token, logoFile, "com-logo", 2 * 1048,
                "company-files/logo/",
                "Photo Uploaded Successfully", companyRepository, FrequentlyUsed.imageExtensions,
                null);
    }

    public ApiResponse<?> getLogo(String token) {
        Optional<Company> optionalCompany = companyRepository
                .getByUsername(tokenUtils.extractUsername(token.substring(7)));
        System.out.println(tokenUtils.extractUsername(token.substring(7)) + " \n " + token);
        if (optionalCompany.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no user for the entered token")
                    .build();
        }
        Company company = optionalCompany.get();

        String filePathWithName = company.getLogo();

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathWithName)
                .build();
    }

    public ApiResponse<?> saveCoverPhoto(String token, MultipartFile coverFile) throws Exception {

        return frequentlyUsed.addFileToUser(token, coverFile, "com-cover", 2 * 1048,
                "company-files/logo/",
                "Photo Uploaded Successfully", companyRepository, FrequentlyUsed.imageExtensions,
                null);
    }

    public ApiResponse<?> getCoverPhoto(String token) {
        Optional<Company> optionalCompany = companyRepository
                .getByUsername(tokenUtils.extractUsername(token.substring(7)));
        if (optionalCompany.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no user for the entered token")
                    .build();
        }
        Company company = optionalCompany.get();

        String filePathWithName = company.getCoverPhoto();

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathWithName)
                .build();
    }


    public ApiResponse<?> updateBenefits(String companyToken, List<Benefit> benefits) {
        String companyUsername = tokenUtils.extractUsername(companyToken.substring(7));
        Company company = companyRepository.getCompanyByUsername(companyUsername);

        company.getAboutCompany().setCompanyBenefits(benefits);

        this.aboutCompanyRepository.save(company.getAboutCompany());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(company.getAboutCompany().getCompanyBenefits())
                .message("Updated")
                .build();
    }

    public ApiResponse<?> getCompanyStatistics(String companyToken) {
        String companyId = tokenUtils.extractId(companyToken.substring(7));
        Optional<Company> optionalCompany = companyRepository.findById(UUID.fromString(companyId));
        if (optionalCompany.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("NOT A VALID ID")
                    .build();
        }

        List<Integer> companyStatisticsNumbers = companyRepository.getCompanyStatisticsById(UUID.fromString(companyId)).get(0);

        int activeJobsCount = jobServiceClient.countCompanyActiveJobs(UUID.fromString(companyId));

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(CompanyStatistics.builder()
                        .companyActiveJobsNumber(activeJobsCount)
                        .companyMembersNumber( companyStatisticsNumbers.get(0))
                        .pendingInvitations( companyStatisticsNumbers.get(1))
                        .build()
                )
                .build() ;
    }


    public ApiResponse<?> getNameOfCompany(String companyToken) {
        Optional<Company> optionalCompany = companyRepository
                .getByUsername(tokenUtils.extractUsername(companyToken.substring(7)));
        if (optionalCompany.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no company for the entered token")
                    .build();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(optionalCompany.get().getName())
                .build() ;
    }

}
