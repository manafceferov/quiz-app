package com.quiz.controller;

import com.quiz.dto.exam.QuestionExamDto;
import com.quiz.dto.examdetail.ParticipantQuizResultDetail;
import com.quiz.entity.Participant;
import com.quiz.entity.QuizResult;
import com.quiz.service.ParticipantService;
import com.quiz.util.session.AuthSessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import com.quiz.dto.paticipantquiz.ParticipantQuizResultList;
import com.quiz.dto.paticipantquiz.QuizResultInsertRequest;
import com.quiz.service.QuizResultService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizResultController extends BaseController {

    private final QuizResultService quizResultService;
    private final AuthSessionData authSessionData;
    private final ParticipantService participantService;

    public QuizResultController(QuizResultService quizResultService,
                                AuthSessionData authSessionData,
                                ParticipantService participantService
    ) {
        this.quizResultService = quizResultService;
        this.authSessionData = authSessionData;
        this.participantService = participantService;
    }

//    @PostMapping("")
//    public String create(@ModelAttribute QuizResultInsertRequest request,
//                         RedirectAttributes redirectAttributes) {
//
//        // ParticipantId sessiyadan alaraq saveQuizResult çağır
//        ParticipantQuizResultList savedResult = quizResultService.saveQuizResult(
//                request.getTopicId(),
//                request.getAnswers().stream()
//                        .map(a -> new ParticipantQuestionAnswer(a.getQuestionId(), a.getAnswerId()))
//                        .collect(Collectors.toList())
//        );
//
//        redirectAttributes.addFlashAttribute("message", "Quiz result created successfully");
//        return "redirect:/participant/my-exams";
//    }

    @GetMapping("/my-exams")
    public String myExams(Model model,
                          @RequestParam(required = false) String topic,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ParticipantQuizResultList> resultPage =
                quizResultService.getResultsForCurrentParticipant(topic, pageable);
        model.addAttribute("results", resultPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("totalItems", resultPage.getTotalElements());
        model.addAttribute("topic", topic);
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Participant participant = participantService.findByEmail(userDetails.getUsername());
            model.addAttribute("participant", participant);
        }
        return "participant/my-exams";
    }

    @GetMapping("/result/{id}")
    public String showResult(@PathVariable Long id,
                             Model model
    ) {
        ParticipantQuizResultList result = quizResultService.showResult(id);
        model.addAttribute("result", result);
        return "participant/result";
    }

    @GetMapping("/{topicId}/question/{questionIndex}")
    public String showQuizWithIndex(@PathVariable Long topicId,
                                    @PathVariable int questionIndex,
                                    Model model
    ) {
        List<QuestionExamDto> questions = quizResultService.getAllExamQuestions(topicId);
        if (questionIndex < 0 || questionIndex >= questions.size()) {
            return "redirect:/participant/" + topicId + "/question/0";
        }
        QuestionExamDto question = questions.get(questionIndex);
        model.addAttribute("question", question);
        model.addAttribute("questions", questions);
        model.addAttribute("topicId", topicId);
        model.addAttribute("questionIndex", questionIndex);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("isExamFinished", false);
        return "participant/exam";
    }

    @GetMapping("/{topicId}/question")
    public String showQuiz(@PathVariable Long topicId,
                           Model model,
                           HttpSession session
    ) {
        session.setAttribute("topicId", topicId);
        List<QuestionExamDto> questions = quizResultService.getAllExamQuestions(topicId);
        if (questions.isEmpty()) {
            return "redirect:/";
        }
        QuestionExamDto question = questions.getFirst();
        model.addAttribute("question", question);
        model.addAttribute("questions", questions);
        model.addAttribute("topicId", topicId);
        model.addAttribute("questionIndex", 0);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("isExamFinished", false);
        return "participant/exam";
    }

    @PostMapping("/{topicId}/result")
    @ResponseBody
    public Long saveResult(@PathVariable Long topicId,
                           @RequestBody QuizResultInsertRequest request
    ) {
        Long participantId = authSessionData.getParticipantSessionData().getId();
        request.setParticipantId(participantId);
        request.setTopicId(topicId);
        QuizResult savedResult = quizResultService.saveQuizResult(request);
        return savedResult.getId();
    }

    @GetMapping("/participant/exam-detail/{quizResultId}")
    public String examDetail(@PathVariable Long quizResultId, Model model) {
        ParticipantQuizResultDetail detail = quizResultService.getExamDetail(quizResultId);
        model.addAttribute("detail", detail);
        return "participant/examdetail";
    }
}