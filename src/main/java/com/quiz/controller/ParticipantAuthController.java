package com.quiz.controller;

import com.quiz.entity.Participant;
import com.quiz.entity.VerificationToken;
import com.quiz.repository.ParticipantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.quiz.service.ParticipantService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
public class ParticipantAuthController extends BaseController{

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    public ParticipantAuthController(ParticipantService participantService,
                                     ParticipantRepository participantRepository
    ) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated())
            return "redirect:/";
        return "participant/login";
    }

    @PostMapping("/register")
    public String register(@RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirm-password") String confirmPassword,
                           Model model,
                           RedirectAttributes redirectAttributes
    ) {
        try {
            Participant participant = participantService.register(firstName, lastName, email, password, confirmPassword);
            participant.setStatus(true);
            participantRepository.save(participant);

            redirectAttributes.addFlashAttribute("success", "Qeydiyyatdan keçdiniz! İndi daxil ola bilərsiniz.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/login";
        }
    }

//    @GetMapping("/verify")
//    public String verify(@RequestParam String token,
//                         RedirectAttributes redirectAttributes
//    ) {
//        VerificationToken verificationToken = verificationTokenRepository
//                .findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Token tapılmadı"));
//        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            redirectAttributes.addFlashAttribute("error", "Token vaxtı bitib");
//            return "redirect:/login";
//        }
//        Participant participant = verificationToken.getParticipant();
//        participant.setStatus(true);
//        participantRepository.save(participant);
//        verificationToken.setVerified(true);
//        redirectAttributes.addFlashAttribute("success", "Hesab təsdiqləndi");
//        return "redirect:/login";
//    }
}
