//package com.quiz.service;
//
//import com.quiz.dto.answer.AnswerInsertRequest;
//import com.quiz.dto.answer.AnswerUpdateRequest;
//import com.quiz.dto.question.QuestionEditDto;
//import com.quiz.dto.question.QuestionInsertRequest;
//import com.quiz.dto.question.QuestionUpdateRequest;
//import com.quiz.entity.Question;
//import com.quiz.mapper.QuestionMapper;
//import com.quiz.repository.QuestionRepository;
//import com.quiz.service.AnswerService;
//import com.quiz.service.QuestionService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.*;
//import java.util.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class QuestionServiceTest {
//
//    @Mock
//    private QuestionRepository repository;
//
//    @Mock
//    private QuestionMapper mapper;
//
//    @Mock
//    private AnswerService answerService;
//
//    @InjectMocks
//    private QuestionService questionService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSave() {
//        QuestionInsertRequest request = new QuestionInsertRequest();
//        AnswerInsertRequest a1 = new AnswerInsertRequest();
//        AnswerInsertRequest a2 = new AnswerInsertRequest();
//        request.setAnswers(List.of(a1, a2));
//
//        Question saved = new Question();
//        saved.setId(1L);
//
//        when(mapper.toDboQuestionFromQuestionInsertRequest(request)).thenReturn(saved);
//        when(repository.saveAndFlush(saved)).thenReturn(saved);
//
//        questionService.save(request, 0);
//
//        verify(repository).saveAndFlush(saved);
//        verify(answerService).saveAll(request.getAnswers());
//        assertTrue(a1.isCorrect());
//        assertFalse(a2.isCorrect());
//    }
//
//    @Test
//    void testGetById_Found() {
//        Question q = new Question();
//        QuestionEditDto dto = new QuestionEditDto();
//
//        when(repository.findById(1L)).thenReturn(Optional.of(q));
//        when(mapper.toQuestionEditDtoFromQuestionDbo(q)).thenReturn(dto);
//
//        QuestionEditDto result = questionService.getById(1L);
//        assertEquals(dto, result);
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        when(repository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class,
//                () -> questionService.getById(1L));
//    }
//
//    @Test
//    void testGetQuestionsByTopicId() {
//        Page<Question> page = new PageImpl<>(List.of(new Question()));
//        when(repository.findByTopicId(anyLong(), any())).thenReturn(page);
//
//        Page<Question> result = questionService.getQuestionsByTopicId(1L, Pageable.unpaged());
//        assertEquals(page, result);
//    }
//
//    @Test
//    void testUpdate() {
//        QuestionUpdateRequest request = new QuestionUpdateRequest();
//        request.setId(1L);
//        request.setQuestion("New Question");
//        request.setTopicId(2L);
//
//        AnswerUpdateRequest a1 = new AnswerUpdateRequest();
//        a1.setId(10L);
//        a1.setAnswer("A");
//
//        AnswerUpdateRequest a2 = new AnswerUpdateRequest();
//        a2.setAnswer("B");
//
//        request.setAnswers(List.of(a1, a2));
//
//        Question question = new Question();
//        question.setId(1L);
//
//        when(repository.findById(1L)).thenReturn(Optional.of(question));
//        when(answerService.getActiveAnswerByQuestionId(1L)).thenReturn(true);
//
//        questionService.update(request, 0);
//
//        verify(repository).save(question);
//        verify(answerService).deleteByIds(eq(1L), anyList());
//        verify(answerService).updateAll(anyList());
//        verify(answerService).saveAll(anyList());
//        verify(repository).changeStatus(1L, true);
//
//        assertTrue(a1.isCorrect());
//        assertFalse(a2.isCorrect());
//    }
//
//    @Test
//    void testCheckQuestionStatus() {
//        questionService.chechkQuestionStatus(1L);
//        verify(repository).deactivateIfAnyAnswerIsInactive(1L);
//    }
//
//    @Test
//    void testDeleteById() {
//        questionService.deleteById(1L);
//        verify(repository).deleteById(1L);
//    }
//
//    @Test
//    void testFindById() {
//        Question q = new Question();
//        when(repository.findById(1L)).thenReturn(Optional.of(q));
//
//        Optional<Question> result = questionService.findById(1L);
//        assertTrue(result.isPresent());
//    }
//
//    @Test
//    void testSearch_WithKeyword() {
//        Page<Question> page = new PageImpl<>(List.of(new Question()));
//        when(repository.findByTopicIdAndQuestionContainingIgnoreCase(eq(1L), eq("test"), any()))
//                .thenReturn(page);
//
//        Page<Question> result =
//                questionService.searchQuestionsByTopicAndKeyword(1L, "test", Pageable.unpaged());
//
//        assertEquals(page, result);
//    }
//
//    @Test
//    void testSearch_WithoutKeyword() {
//        Page<Question> page = new PageImpl<>(List.of(new Question()));
//        when(repository.findByTopicId(eq(1L), any())).thenReturn(page);
//
//        Page<Question> result =
//                questionService.searchQuestionsByTopicAndKeyword(1L, "", Pageable.unpaged());
//
//        assertEquals(page, result);
//    }
//
//    @Test
//    void testGetQuestionWithAnswersById() {
//        Question q = new Question();
//        QuestionEditDto dto = new QuestionEditDto();
//
//        when(repository.getQuestionWithAnswersById(1L)).thenReturn(q);
//        when(mapper.toQuestionEditDtoFromQuestionDbo(q)).thenReturn(dto);
//
//        QuestionEditDto result = questionService.getQuestionWithAnswersById(1L);
//        assertEquals(dto, result);
//    }
//
//    @Test
//    void testChangeStatus_ReturnFalse_WhenNoActiveAnswer() {
//        when(answerService.getActiveAnswerByQuestionId(1L)).thenReturn(false);
//
//        Boolean result = questionService.changeStatus(1L, true);
//
//        assertFalse(result);
//        verify(repository, never()).changeStatus(anyLong(), anyBoolean());
//    }
//
//    @Test
//    void testChangeStatus_ReturnTrue() {
//        when(answerService.getActiveAnswerByQuestionId(1L)).thenReturn(true);
//
//        Boolean result = questionService.changeStatus(1L, true);
//
//        assertTrue(result);
//        verify(repository).changeStatus(1L, true);
//    }
//
//    @Test
//    void testGetQuestionsWithParticipant_WithoutKeyword() {
//        Question q = new Question();
//        QuestionEditDto dto = new QuestionEditDto();
//        Page<Question> page = new PageImpl<>(List.of(q));
//
//        when(repository.findByTopicId(eq(1L), any())).thenReturn(page);
//        when(mapper.toQuestionEditDtoFromQuestionDbo(q)).thenReturn(dto);
//
//        Page<QuestionEditDto> result =
//                questionService.getQuestionsWithParticipantByTopic(1L, null, Pageable.unpaged());
//
//        assertEquals(1, result.getContent().size());
//    }
//
//    @Test
//    void testGetQuestionsWithParticipant_WithKeyword() {
//        Question q = new Question();
//        QuestionEditDto dto = new QuestionEditDto();
//        Page<Question> page = new PageImpl<>(List.of(q));
//
//        when(repository.findByTopicIdAndQuestionContainingIgnoreCase(eq(1L), eq("java"), any()))
//                .thenReturn(page);
//        when(mapper.toQuestionEditDtoFromQuestionDbo(q)).thenReturn(dto);
//
//        Page<QuestionEditDto> result =
//                questionService.getQuestionsWithParticipantByTopic(1L, "java", Pageable.unpaged());
//
//        assertEquals(1, result.getContent().size());
//    }
//
//    @Test
//    void testGetQuestionCountByTopicId() {
//        when(repository.getCountByTopicId(1L)).thenReturn(10L);
//        assertEquals(10L, questionService.getQuestionCountByTopicId(1L));
//    }
//}
