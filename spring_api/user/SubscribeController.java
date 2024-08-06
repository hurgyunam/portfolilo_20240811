package com.lab2ai.cosign.user;

import com.lab2ai.cosign.user.entity.SubscribeRelation;
import com.lab2ai.cosign.user.entity.User;
import com.lab2ai.cosign.common.BasicRes;
import com.lab2ai.cosign.user.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;

    @PostMapping("/is_subscribed/{mentorPageId}")
    public BasicRes isSubscribed(@PathVariable String mentorPageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = ((User) auth.getPrincipal());
            SubscribeRelation subscribeRelation = subscribeService.getSubscribeRelation(mentorPageId, user.getId());

            if(subscribeRelation != null) {
                return new BasicRes(BasicRes.CODE_SUCCESS, "구독되어있습니다.");
            } else {
                return new BasicRes(BasicRes.CODE_NOT_EXISTS, "구독하지 않았습니다.");
            }
        } else {
            return new BasicRes(BasicRes.CODE_NO_LOGIN, "로그인이 필요합니다.");
        }
    }

    @PostMapping("/subscribe")
    public BasicRes subscribe(@RequestBody HashMap params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mentorPageId = params.get("mentorPageId").toString();

        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = ((User) auth.getPrincipal());
            subscribeService.subscribe(mentorPageId, user.getId());
            return new BasicRes(BasicRes.CODE_SUCCESS, "구독에 성공했습니다.");
        } else {
            return new BasicRes(BasicRes.CODE_NO_LOGIN, "로그인없이 구독할 수 없습니다.");
        }
    }

    @PostMapping("/unsubscribe")
    public BasicRes unsubscribe(@RequestBody HashMap params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mentorPageId = params.get("mentorPageId").toString();

        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = ((User) auth.getPrincipal());
            subscribeService.unsubscribe(mentorPageId, user.getId());
            return new BasicRes(BasicRes.CODE_SUCCESS, "구독에 성공했습니다.");
        } else {
            return new BasicRes(BasicRes.CODE_NO_LOGIN, "로그인없이 구독할 수 없습니다.");
        }
    }
}
