package com.hpms.jobservice.mapper;


import com.hpms.jobservice.dto.QuestionDto;
import com.hpms.jobservice.model.Question;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {

    QuestionDto toDto(Question questionEntity);

    List<QuestionDto> toDtoList(List<Question> questionList);


}
