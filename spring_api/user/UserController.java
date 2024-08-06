package com.lab2ai.cosign.user;

import com.lab2ai.cosign.user.entity.User;
import com.lab2ai.cosign.user.data.AuthenticationToken;
import com.lab2ai.cosign.user.data.LoginRequest;
import com.lab2ai.cosign.user.data.SignUpRequest;
import com.lab2ai.cosign.common.BasicRes;
import com.lab2ai.cosign.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AuthenticationToken login(@RequestBody LoginRequest loginRequest,
                                HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        UserDetails user = userService.loadUserByUsername(loginRequest.getUsername());
        return new AuthenticationToken(user.getUsername(), user.getAuthorities(), session.getId());
    }

    @PostMapping("/logout")
    public BasicRes logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, auth);

            return new BasicRes(BasicRes.CODE_SUCCESS, "로그아웃되었습니다.");
        } else {
            return new BasicRes(BasicRes.CODE_NO_LOGIN, "이미 로그아웃상태입니다.");
        }
    }

    @PostMapping("/login_info")
    public AuthenticationToken getUser(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return new AuthenticationToken(user.getUsername(), user.getAuthorities(), session.getId());
        } else {
            return null;
        }
    }

    @PostMapping("/signUp")
    public boolean SignUp(@RequestBody SignUpRequest signupRequest) {
        return userService.save(signupRequest.getUsername(), signupRequest.getPassword(), signupRequest.getNickName());
    }
}
