package com.quiz.controller;

import com.quiz.dto.question.QuestionEditDto;
import com.quiz.dto.question.QuestionInsertRequest;
import com.quiz.dto.question.QuestionUpdateRequest;
import com.quiz.entity.Participant;
import com.quiz.service.AnswerService;
import com.quiz.service.ParticipantService;
import com.quiz.service.QuestionService;
import com.quiz.service.TopicService;
import com.quiz.util.session.AuthSessionData;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/participant/questions")
public class ParticipantQuestionController {

    private final QuestionService service;
    private final AnswerService answerService;
    private final AuthSessionData authSessionData;
    private final TopicService topicService;
    private final ParticipantService participantService;


    public ParticipantQuestionController(QuestionService service,
                                         AnswerService answerService,
                                         AuthSessionData authSessionData,
                                         TopicService topicService,
                                         ParticipantService participantService
    ) {
        this.service = service;
        this.answerService = answerService;
        this.authSessionData = authSessionData;
        this.topicService = topicService;
        this.participantService = participantService;
    }

    @GetMapping("/topic/{topicId}")
    public String index(Model model,
                        @PathVariable Long topicId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String keyword,
                        Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionEditDto> questions = service.getQuestionsWithParticipantByTopic(topicId, keyword, pageable);
        model.addAttribute("questions", questions);
        model.addAttribute("topic", topicService.findById(topicId).orElseThrow());
        model.addAttribute("currentParticipantId", authSessionData.getParticipantSessionData().getId());
        model.addAttribute("topicId", topicId);
        model.addAttribute("keyword", keyword);
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Participant participant = participantService.findByEmail(userDetails.getUsername());
            model.addAttribute("participant", participant);
        }

        return "participant/question/index";
    }

    @GetMapping("/topic/{topicId}/create")
    public String create(@PathVariable Long topicId,
                         Model model
    ) {
        QuestionInsertRequest request = new QuestionInsertRequest();
        request.setTopicId(topicId);
        model.addAttribute("request", request);
        return "participant/question/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("request") QuestionInsertRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirect,
                         @RequestParam int correctAnswerIndex
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "participant/question/create";
        }
        request.setByParticipant(authSessionData.getParticipantSessionData().getId());
        service.save(request, correctAnswerIndex);
        redirect.addFlashAttribute("success", "Sual əlavə edildi");
        redirect.addAttribute("topicId", request.getTopicId());
        return "redirect:/participant/questions/topic/{topicId}";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       Model model
    ) {
        QuestionEditDto data = service.getQuestionWithAnswersById(id);
        if (!Objects.equals(data.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/questions/topic/" + data.getTopicId();
        }
        model.addAttribute("request", data);
        return "participant/question/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("request") QuestionUpdateRequest request,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirect,
                       @RequestParam int correctAnswerIndex
    ) {
        QuestionEditDto data = service.getQuestionWithAnswersById(request.getId());
        if (!Objects.equals(data.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/questions/topic/" + data.getTopicId();
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "participant/question/edit";
        }
        service.update(request, correctAnswerIndex);
        redirect.addFlashAttribute("success", "Sual yeniləndi");
        redirect.addAttribute("topicId", request.getTopicId());
        return "redirect:/participant/questions/topic/{topicId}";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam Long topicId,
                         RedirectAttributes redirect
    ) {
        QuestionEditDto data = service.getQuestionWithAnswersById(id);
        if (!Objects.equals(data.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            redirect.addAttribute("topicId", topicId);
            return "redirect:/participant/questions/topic/{topicId}";
        }
        service.deleteById(id);
        redirect.addFlashAttribute("success", "Sual silindi");
        redirect.addAttribute("topicId", topicId);
        return "redirect:/participant/questions/topic/{topicId}";
    }

    @GetMapping("/change-status/question/{id}/status/{status}")
    public String changeStatus(@PathVariable Long id,
                               @PathVariable Boolean status,
                               @RequestParam Long topicId,
                               RedirectAttributes redirect
    ) {
        QuestionEditDto data = service.getQuestionWithAnswersById(id);
        if (!Objects.equals(data.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            redirect.addAttribute("topicId", topicId);
            return "redirect:/participant/questions/topic/{topicId}";
        }
        service.changeStatus(id, status);
        redirect.addAttribute("topicId", topicId);
        return "redirect:/participant/questions/topic/{topicId}";
    }
}
