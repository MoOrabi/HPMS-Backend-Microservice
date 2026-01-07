package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.SocialIconInfo;
import com.hpms.userservice.service.shared.SocialIconsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/api")
public class SocialIconsController {

    @Autowired
    private SocialIconsService socialIconsService;

    @GetMapping("/social-icons")
    public List<SocialIconInfo> getAllIcons() {
        return socialIconsService.getAllIcons();
    }

}
