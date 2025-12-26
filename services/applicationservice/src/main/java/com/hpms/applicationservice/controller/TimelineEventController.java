package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.service.impl.TimelineEventService;
import com.hpms.commonlib.dto.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/apps")
@AllArgsConstructor
public class TimelineEventController {

    private final TimelineEventService timelineEventService;

    @PutMapping("/status")
    public ApiResponse<?> moveApplication(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam UUID appId,
                                          @RequestParam String applicationStatus) {
        return timelineEventService.moveApplication(token, appId, applicationStatus);
    }

    @GetMapping("/timeline")
    public ApiResponse<?> getTimelineForApplication(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam UUID appId) {
        return timelineEventService.getTimelineEventsForApplication(token, appId);
    }
}
