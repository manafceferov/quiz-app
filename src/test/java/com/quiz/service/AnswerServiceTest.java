//package com.quiz.service;
//
//import com.quiz.dto.answer.AnswerInsertRequest;
//import com.quiz.dto.answer.AnswerUpdateRequest;
//import com.quiz.dto.answer.AnswerEditDto;
//import com.quiz.entity.Answer;
//import com.quiz.mapper.AnswerMapper;
//import com.quiz.mapper.QuestionMapper;
//import com.quiz.repository.AnswerRepository;
//import com.quiz.repository.QuestionRepository;
//import com.quiz.service.AnswerService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AnswerServiceTest {
//
//    @Mock
//    private AnswerRepository repository;
//
//    @Mock
//    private AnswerMapper mapper;
//
//    @Mock
//    private QuestionRepository questionRepository;
//
//    @Mock
//    private QuestionMapper questionMapper;
//
//    @InjectMocks
//    private AnswerService answerService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAnswersByQuestionId() {
//        Long questionId = 1L;
//        List<Answer> answers = Arrays.asList(new Answer(), new Answer());
//        when(repository.findByQuestionId(questionId)).thenReturn(answers);
//
//        List<Answer> result = answerService.getAnswersByQuestionId(questionId);
//
//        assertEquals(2, result.size());
//        verify(repository, times(1)).findByQuestionId(questionId);
//    }
//
//    @Test
//    void testSave() {
//        AnswerInsertRequest request = new AnswerInsertRequest();
//        Answer answer = new Answer();
//        when(mapper.toDboFromInsert(request)).thenReturn(answer);
//
//        answerService.save(request);
//
//        verify(repository, times(1)).save(answer);
//    }
//
//    @Test
//    void testSaveAll() {
//        List<AnswerInsertRequest> requestList = Arrays.asList(new AnswerInsertRequest(), new AnswerInsertRequest());
//        List<Answer> answers = Arrays.asList(new Answer(), new Answer());
//        when(mapper.toDboFromAnswerInsertRequest(requestList)).thenReturn(answers);
//
//        answerService.saveAll(requestList);
//
//        verify(repository, times(1)).saveAll(answers);
//    }
//
//    @Test
//    void testUpdateAll() {
//        List<AnswerUpdateRequest> requestList = Arrays.asList(new AnswerUpdateRequest(), new AnswerUpdateRequest());
//        List<Answer> answers = Arrays.asList(new Answer(), new Answer());
//        when(mapper.toDboFromAnswerUpdateRequest(requestList)).thenReturn(answers);
//
//        answerService.updateAll(requestList);
//
//        verify(repository, times(1)).saveAll(answers);
//    }
//
//    @Test
//    void testGetById_Found() {
//        Long id = 1L;
//        Answer answer = new Answer();
//        AnswerEditDto dto = new AnswerEditDto();
//        when(repository.findById(id)).thenReturn(Optional.of(answer));
//        when(mapper.toDto(answer)).thenReturn(dto);
//
//        AnswerEditDto result = answerService.getById(id);
//
//        assertNotNull(result);
//        verify(repository, times(1)).findById(id);
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        Long id = 1L;
//        when(repository.findById(id)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> answerService.getById(id));
//        verify(repository, times(1)).findById(id);
//    }
//
//    @Test
//    void testDeleteById() {
//        Long id = 1L;
//        List<Long> deleteIds = Arrays.asList(2L, 3L);
//
//        answerService.deleteById(id, deleteIds);
//
//        verify(repository, times(1)).deleteByIds(id, deleteIds);
//    }
//
//    @Test
//    void testDeleteByIds() {
//        Long questionId = 1L;
//        List<Long> ids = Arrays.asList(2L, 3L);
//
//        answerService.deleteByIds(questionId, ids);
//
//        verify(repository, times(1)).deleteByIds(questionId, ids);
//    }
//
//    @Test
//    void testChangeStatus() {
//        Long id = 1L;
//        Boolean status = true;
//
//        answerService.changeStatus(id, status);
//
//        verify(repository, times(1)).changeStatus(id, status);
//    }
//
//    @Test
//    void testGetActiveAnswerByQuestionId() {
//        Long questionId = 1L;
//        when(repository.getExsistTwoIsActiveAnswerByQuestionId(questionId)).thenReturn(true);
//
//        Boolean result = answerService.getActiveAnswerByQuestionId(questionId);
//
//        assertTrue(result);
//        verify(repository, times(1)).getExsistTwoIsActiveAnswerByQuestionId(questionId);
//    }
//}
