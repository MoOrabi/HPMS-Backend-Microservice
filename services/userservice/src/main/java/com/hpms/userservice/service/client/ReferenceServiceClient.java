package com.hpms.userservice.service.client;

import com.hpms.commonlib.dto.SelectOption;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Set;

@HttpExchange(url = "http://reference-service/api/references/jobs")
public interface ReferenceServiceClient {

    @GetExchange("/skills-name")
    Set<SelectOption> getSkillsNames(@RequestBody Set<Long> skillIds);
}
