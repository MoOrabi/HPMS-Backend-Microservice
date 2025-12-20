package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.company.RecruiterInfo;
import com.hpms.userservice.model.Recruiter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RecruiterInfoMapper {
    RecruiterInfo recruiterToInfo(Recruiter recruiter);
    List<RecruiterInfo> recruiterListToInfoList(List<Recruiter> recruiter);

}
