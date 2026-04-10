package com.quiz.controller;

import com.quiz.dto.ai.AiQuizResponse;
import com.quiz.entity.Participant;
import com.quiz.service.AiQuizService;
import com.quiz.service.ParticipantService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/participant/ai-quiz")
public class AiQuizController {

    private final AiQuizService aiQuizService;
    private final ParticipantService participantService;

    public AiQuizController(AiQuizService aiQuizService,
                            ParticipantService participantService
    ) {
        this.aiQuizService = aiQuizService;
        this.participantService = participantService;
    }

    @GetMapping
    public String showAiPage(Model model,
                             HttpServletRequest request,
                             Authentication authentication
    ){
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Participant participant =
                    participantService.findByEmail(userDetails.getUsername());
            model.addAttribute("participant", participant);
        }
        return "participant/ai-quiz/create";
    }

    @PostMapping("/generate")
    @ResponseBody
    public Map<String, Object> generateQuiz(@RequestBody Map<String,
                                            String> request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.isBlank()) {
                throw new IllegalArgumentException("Prompt boş ola bilməz");
            }
            prompt = normalizePrompt(prompt);
            AiQuizResponse result = aiQuizService.generateQuiz(prompt);
            response.put("success", true);
            response.put("data", result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage() != null ? e.getMessage() : "Naməlum xəta");
        }
        return response;
    }

    @PostMapping("/save")
    public String saveQuiz(@RequestParam String topicName,
                           @RequestParam String questionsJson,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes
    ) {
        try {
            String email = authentication.getName();
            aiQuizService.saveQuizToDatabase(email, topicName, questionsJson);
            redirectAttributes.addFlashAttribute("success", "Quiz uğurla yaradıldı!");
            return "redirect:/participant/topics";
        } catch (Exception e) {
            System.err.println("Controller error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Xəta: " + e.getMessage());
            return "redirect:/participant/ai-quiz";
        }
    }

    private String normalizePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return prompt;
        }
        String p = prompt.toLowerCase();
        if (p.contains("sual") || p.contains("yarat")) {
            return prompt;
        }
        return prompt + " haqqında 5 sual yarat";
    }
}