package com.hpms.userservice.service.shared;

import com.hpms.userservice.model.shared.SocialIconInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SocialIconsService {

    List<SocialIconInfo> getAllIcons();
}
