package com.quiz.config;

import com.quiz.entity.Participant;
import com.quiz.service.ParticipantService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ParticipantControllerAdvice {

    private final ParticipantService participantService;

    public ParticipantControllerAdvice(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @ModelAttribute
    public void addParticipantToModel(Model model, Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails) {

            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                Participant participant = participantService.findByEmail(userDetails.getUsername());
                model.addAttribute("participant", participant);
            } catch (Exception e) {
            }
        }
    }
}