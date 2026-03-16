package com.quiz.controller;

import com.quiz.dto.question.QuestionEditDto;
import com.quiz.dto.question.QuestionInsertRequest;
import com.quiz.dto.question.QuestionUpdateRequest;
import com.quiz.service.AnswerService;
import com.quiz.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/questions")
public class QuestionController extends BaseController{

    private final QuestionService service;
    private final AnswerService answerService;

    public QuestionController(QuestionService service,
                              AnswerService answerService
    ) {
        this.service = service;
        this.answerService = answerService;
    }

    @GetMapping("/topic/{topicId}")
    public String index(Model model,
                        @PathVariable Long topicId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("questions", service.searchQuestionsByTopicAndKeyword(topicId, keyword, pageable));
        model.addAttribute("topicId", topicId);
        model.addAttribute("keyword", keyword);
        return "admin/question/index";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable
                       Long id,
                       Model model
    ) {
        model.addAttribute("question", service.getById(id));
        model.addAttribute("answers", answerService.getAnswersByQuestionId(id));
        return "admin/question/view";
    }

    @GetMapping("/topic/{topicId}/create")
    public String create(@PathVariable
                         Long topicId,
                         Model model
    ) {
        QuestionInsertRequest request = new QuestionInsertRequest();
        request.setTopicId(topicId);
        model.addAttribute("request", request);
        return "admin/question/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("request")
                         QuestionInsertRequest request,
                         Model model,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(name = "correctAnswerIndex") int correctAnswerIndex
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin/question/create";
        }
        service.save(request, correctAnswerIndex);
        redirectAttributes.addFlashAttribute("success", "Sual əlavə edildi");
        redirectAttributes.addAttribute("topicId", request.getTopicId());
        return "redirect:/admin/questions/topic/{topicId}";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable
                       Long id,
                       Model model
    ) {
        QuestionEditDto data = service.getQuestionWithAnswersById(id);
        model.addAttribute("request", data);
        return "admin/question/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("request")
                       QuestionUpdateRequest request,
                       Model model,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(name = "correctAnswerIndex") int correctAnswerIndex
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin/question/edit";
        }
        service.update(request, correctAnswerIndex);
        redirectAttributes.addFlashAttribute("success", "Sual yeniləndi");
        redirectAttributes.addAttribute("topicId", request.getTopicId());
        return "redirect:/admin/questions/topic/{topicId}";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam Long topicId,
                         RedirectAttributes redirectAttributes
    ) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Sual silindi");
        redirectAttributes.addAttribute("topicId", topicId);
        return "redirect:/admin/questions/topic/{topicId}";
    }

    @GetMapping("/change-status/question/{id}/status/{status}")
    public String changeStatus(@PathVariable Long id,
                               @PathVariable Boolean status,
                               @RequestParam Long topicId,
                               RedirectAttributes redirectAttributes
    ) {
        Boolean result = service.changeStatus(id, status);
        if (!result) {
            redirectAttributes.addFlashAttribute("errors", "Xeta bas verdi");
        }
        redirectAttributes.addAttribute("topicId", topicId);
        return "redirect:/admin/questions/topic/{topicId}";
    }
}