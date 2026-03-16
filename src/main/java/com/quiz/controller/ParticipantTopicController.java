package com.quiz.controller;

import com.quiz.dto.topic.TopicInsertRequest;
import com.quiz.dto.topic.TopicUpdateRequest;
import com.quiz.entity.Participant;
import com.quiz.entity.Topic;
import com.quiz.service.ParticipantService;
import com.quiz.service.TopicService;
import com.quiz.util.session.AuthSessionData;
import jakarta.validation.Valid;
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
@RequestMapping("/participant/topics")
public class ParticipantTopicController {

    private final TopicService service;
    private final AuthSessionData authSessionData;
    private final ParticipantService participantService;

    public ParticipantTopicController(TopicService service,
                                      AuthSessionData authSessionData,
                                      ParticipantService participantService
    ) {
        this.service = service;
        this.authSessionData = authSessionData;
        this.participantService = participantService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String name,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model,
                        Authentication authentication
    ) {
        Long participantId = authSessionData.getParticipantSessionData().getId();
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("topics", service.getAllByParticipant(participantId, name, pageable));
        model.addAttribute("name", name);
        model.addAttribute("currentParticipantId", participantId);
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Participant participant = participantService.findByEmail(userDetails.getUsername());
            model.addAttribute("participant", participant);
        }
        return "participant/topic/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("request", new TopicInsertRequest());
        return "participant/topic/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("request") TopicInsertRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirect
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "participant/topic/create";
        }
        request.setByParticipant(authSessionData.getParticipantSessionData().getId());
        service.save(request);
        redirect.addFlashAttribute("success", "Mövzu əlavə edildi");
        return "redirect:/participant/topics";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       Model model
    ) {
        Topic t = service.findById(id).orElseThrow();
        if (!Objects.equals(t.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/topics";
        }
        model.addAttribute("request", t);
        return "participant/topic/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("request") TopicUpdateRequest request,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirect
    ) {
        Topic topic = service.findById(request.getId()).orElseThrow();
        if (!Objects.equals(topic.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/topics";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "participant/topic/edit";
        }
        request.setByParticipant(authSessionData.getParticipantSessionData().getId());
        service.update(request);
        redirect.addFlashAttribute("success", "Mövzu yeniləndi");
        return "redirect:/participant/topics";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirect
    ) {
        Topic t = service.findById(id).orElseThrow();
        if (!Objects.equals(t.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/topics";
        }
        service.deleteById(id);
        redirect.addFlashAttribute("success", "Mövzu silindi");
        return "redirect:/participant/topics";
    }

    @GetMapping("/change-status/topic/{id}/status/{status}")
    public String changeStatus(@PathVariable Long id,
                               @PathVariable Boolean status,
                               RedirectAttributes redirect
    ) {
        Topic t = service.findById(id).orElseThrow();
        if (!Objects.equals(t.getByParticipant(), authSessionData.getParticipantSessionData().getId())) {
            return "redirect:/participant/topics";
        }
        service.changeStatus(id, status);
        return "redirect:/participant/topics";
    }
}