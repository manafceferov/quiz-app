//package com.quiz.controller;
//
//import com.quiz.dto.question.QuestionEditDto;
//import com.quiz.dto.question.QuestionInsertRequest;
//import com.quiz.dto.question.QuestionUpdateRequest;
//import com.quiz.controller.QuestionController;
//import com.quiz.service.AnswerService;
//import com.quiz.service.QuestionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class QuestionControllerTest {
//
//    @Mock
//    private QuestionService questionService;
//
//    @Mock
//    private AnswerService answerService;
//
//    @Mock
//    private Model model;
//
//    @Mock
//    private BindingResult bindingResult;
//
//    @Mock
//    private RedirectAttributes redirectAttributes;
//
//    @InjectMocks
//    private QuestionController controller;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ✅ 1. Index (siyahı səhifəsi)
//    @Test
//    void testIndex_ShouldReturnIndexView() {
//        Long topicId = 1L;
//        when(questionService.searchQuestionsByTopicAndKeyword(eq(topicId), anyString(), any(PageRequest.class)))
//                .thenReturn(new PageImpl<>(List.of()));
//
//        String result = controller.index(model, topicId, 0, 10, "spring");
//
//        verify(model).addAttribute(eq("questions"), any());
//        verify(model).addAttribute("topicId", topicId);
//        verify(model).addAttribute("keyword", "spring");
//        assertEquals("admin/question/index", result);
//    }
//
//    // ✅ 2. View səhifəsi
//    @Test
//    void testView_ShouldReturnViewPage() {
//        Long id = 5L;
//        QuestionEditDto dto = new QuestionEditDto();
//
//        when(questionService.getById(id)).thenReturn(dto);
//        when(answerService.getAnswersByQuestionId(id)).thenReturn(List.of());
//
//        String result = controller.view(id, model);
//
//        verify(model).addAttribute("question", dto);
//        verify(model).addAttribute(eq("answers"), any());
//        assertEquals("admin/question/view", result);
//    }
//
//    // ✅ 3. Create GET
//    @Test
//    void testCreateGet_ShouldReturnCreateView() {
//        Long topicId = 3L;
//
//        String result = controller.create(topicId, model);
//
//        verify(model).addAttribute(eq("request"), any(QuestionInsertRequest.class));
//        assertEquals("admin/question/create", result);
//    }
//
//    // ✅ 4. Create POST (uğurlu)
//    @Test
//    void testCreatePost_ShouldRedirectAfterSave() {
//        QuestionInsertRequest request = new QuestionInsertRequest();
//        request.setTopicId(10L);
//        when(bindingResult.hasErrors()).thenReturn(false);
//
//        String result = controller.create(request, model, bindingResult, redirectAttributes, 2);
//
//        verify(questionService).save(request, 2);
//        verify(redirectAttributes).addFlashAttribute("success", "Sual əlavə edildi");
//        verify(redirectAttributes).addAttribute("topicId", 10L);
//        assertEquals("redirect:/admin/questions/topic/{topicId}", result);
//    }
//
//    // ❌ 5. Create POST (validation xətası)
//    @Test
//    void testCreatePost_WithErrors_ShouldReturnCreateView() {
//        when(bindingResult.hasErrors()).thenReturn(true);
//        QuestionInsertRequest request = new QuestionInsertRequest();
//
//        String result = controller.create(request, model, bindingResult, redirectAttributes, 0);
//
//        verify(model).addAttribute(eq("errors"), any());
//        assertEquals("admin/question/create", result);
//    }
//
//    // ✅ 6. Edit GET
//    @Test
//    void testEditGet_ShouldReturnEditView() {
//        Long id = 7L;
//        QuestionEditDto dto = new QuestionEditDto();
//        when(questionService.getQuestionWithAnswersById(id)).thenReturn(dto);
//
//        String result = controller.edit(id, model);
//
//        verify(model).addAttribute("request", dto);
//        assertEquals("admin/question/edit", result);
//    }
//
//    // ✅ 7. Edit POST (uğurlu)
//    @Test
//    void testEditPost_ShouldRedirectAfterUpdate() {
//        QuestionUpdateRequest request = new QuestionUpdateRequest();
//        request.setTopicId(9L);
//        when(bindingResult.hasErrors()).thenReturn(false);
//
//        String result = controller.edit(request, model, bindingResult, redirectAttributes, 1);
//
//        verify(questionService).update(request, 1);
//        verify(redirectAttributes).addFlashAttribute("success", "Sual yeniləndi");
//        verify(redirectAttributes).addAttribute("topicId", 9L);
//        assertEquals("redirect:/admin/questions/topic/{topicId}", result);
//    }
//
//    // ❌ 8. Edit POST (validation xətası)
//    @Test
//    void testEditPost_WithErrors_ShouldReturnEditView() {
//        when(bindingResult.hasErrors()).thenReturn(true);
//        QuestionUpdateRequest request = new QuestionUpdateRequest();
//
//        String result = controller.edit(request, model, bindingResult, redirectAttributes, 0);
//
//        verify(model).addAttribute(eq("errors"), any());
//        assertEquals("admin/question/edit", result);
//    }
//
//    // ✅ 9. Delete sualı
//    @Test
//    void testDelete_ShouldRedirectAfterDelete() {
//        Long id = 4L;
//        Long topicId = 12L;
//
//        String result = controller.delete(id, topicId, redirectAttributes);
//
//        verify(questionService).deleteById(id);
//        verify(redirectAttributes).addFlashAttribute("success", "Sual silindi");
//        verify(redirectAttributes).addAttribute("topicId", topicId);
//        assertEquals("redirect:/admin/questions/topic/{topicId}", result);
//    }
//
//    // ✅ 10. Status dəyişmək
//    @Test
//    void testChangeStatus_ShouldRedirectAfterChange() {
//        Long id = 1L;
//        Boolean status = true;
//        Long topicId = 8L;
//
//        when(questionService.changeStatus(id, status)).thenReturn(true);
//
//        String result = controller.changeStatus(id, status, topicId, redirectAttributes);
//
//        verify(questionService).changeStatus(id, status);
//        verify(redirectAttributes).addAttribute("topicId", topicId);
//        assertEquals("redirect:/admin/questions/topic/{topicId}", result);
//    }
//
//    // ❌ 11. Status dəyişmək (xəta halı)
//    @Test
//    void testChangeStatus_WhenError_ShouldAddErrorMessage() {
//        Long id = 2L;
//        Boolean status = false;
//        Long topicId = 15L;
//
//        when(questionService.changeStatus(id, status)).thenReturn(false);
//
//        String result = controller.changeStatus(id, status, topicId, redirectAttributes);
//
//        verify(redirectAttributes).addFlashAttribute("errors", "Xeta bas verdi");
//        verify(redirectAttributes).addAttribute("topicId", topicId);
//        assertEquals("redirect:/admin/questions/topic/{topicId}", result);
//    }
//}
