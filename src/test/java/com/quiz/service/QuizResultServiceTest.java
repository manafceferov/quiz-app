//package com.quiz.service;
//
//import com.quiz.dto.exam.QuestionExamDto;
//import com.quiz.dto.examdetail.ParticipantQuizResultDetail;
//import com.quiz.dto.paticipantquiz.ParticipantAnswerInsertRequest;
//import com.quiz.dto.paticipantquiz.ParticipantQuizResultList;
//import com.quiz.dto.paticipantquiz.QuizResultInsertRequest;
//import com.quiz.entity.*;
//import com.quiz.entity.*;
//import com.quiz.mapper.AnswerMapper;
//import com.quiz.mapper.QuestionMapper;
//import com.quiz.mapper.QuizResultMapper;
//import com.quiz.repository.AnswerRepository;
//import com.quiz.repository.ParticipantAnswerRepository;
//import com.quiz.repository.QuestionRepository;
//import com.quiz.repository.QuizResultRepository;
//import com.quiz.util.session.AuthSessionData;
//import com.quiz.util.session.ParticipantSessionData;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class QuizResultServiceTest {
//
//    @Mock private QuizResultRepository quizResultRepository;
//    @Mock private AnswerService answerService;
//    @Mock private QuestionRepository questionRepository;
//    @Mock private QuizResultMapper quizResultMapper;
//    @Mock private AuthSessionData authSessionData;
//    @Mock private ParticipantSessionData participantSessionData;
//    @Mock private ParticipantAnswerService participantAnswerService;
//    @Mock private ParticipantAnswerRepository participantAnswerRepository;
//    @Mock private QuestionMapper questionMapper;
//    @Mock private AnswerMapper answerMapper;
//    @Mock private AnswerRepository answerRepository;
//
//    @InjectMocks
//    private QuizResultService service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void saveQuizResult_participantFromSession() {
//        QuizResultInsertRequest request = mock(QuizResultInsertRequest.class);
//
//        when(request.getParticipantId()).thenReturn(null);
//        when(request.getTopicId()).thenReturn(1L);
//
//        when(authSessionData.getParticipantSessionData()).thenReturn(participantSessionData);
//        when(participantSessionData.getId()).thenReturn(10L);
//
//        when(questionRepository.getCountByTopicId(1L)).thenReturn(1L);
//
//        Question question = new Question();
//        question.setId(1L);
//
//        Answer correctAnswer = new Answer();
//        correctAnswer.setId(100L);
//        correctAnswer.setQuestion(question);
//
//        when(answerRepository.findCorrectAnswersByTopicId(1L))
//                .thenReturn(List.of(correctAnswer));
//
//        ParticipantAnswerInsertRequest paDto = new ParticipantAnswerInsertRequest();
//        paDto.setQuestionId(1L);
//        paDto.setAnswerId(100L);
//
//        when(request.getAnswers()).thenReturn(List.of(paDto));
//
//        QuizResult quizResult = new QuizResult();
//        quizResult.setId(5L);
//
//        when(quizResultMapper.toDbo(request)).thenReturn(quizResult);
//        when(quizResultRepository.save(quizResult)).thenReturn(quizResult);
//
//        QuizResult result = service.saveQuizResult(request);
//
//        assertNotNull(result);
//        verify(participantAnswerService).saveAll(anyList());
//    }
//
//    @Test
//    void showResult_found() {
//        QuizResult quizResult = new QuizResult();
//        ParticipantQuizResultList dto = new ParticipantQuizResultList();
//
//        when(quizResultRepository.findById(1L)).thenReturn(Optional.of(quizResult));
//        when(quizResultMapper.toParticipantQuizResultList(quizResult)).thenReturn(dto);
//
//        ParticipantQuizResultList result = service.showResult(1L);
//        assertEquals(dto, result);
//    }
//
//    @Test
//    void showResult_notFound() {
//        when(quizResultRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(IllegalArgumentException.class, () -> service.showResult(1L));
//    }
//
//    @Test
//    void getCurrentParticipantId() {
//        when(authSessionData.getParticipantSessionData()).thenReturn(participantSessionData);
//        when(participantSessionData.getId()).thenReturn(7L);
//
//        assertEquals(7L, service.getCurrentParticipantId());
//    }
//
//    @Test
//    void getResultsForCurrentParticipant() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        when(authSessionData.getParticipantSessionData())
//                .thenReturn(participantSessionData);
//        when(participantSessionData.getId())
//                .thenReturn(7L);
//
//        QuizResult qr = new QuizResult();
//        Page<QuizResult> quizResultPage =
//                new PageImpl<>(List.of(qr), pageable, 1);
//
//        when(quizResultRepository.searchByParticipantAndTopic(
//                eq(7L),
//                isNull(),
//                eq(pageable)
//        )).thenReturn(quizResultPage);
//
//        when(quizResultMapper.toParticipantQuizResultList(qr))
//                .thenReturn(new ParticipantQuizResultList());
//
//        Page<ParticipantQuizResultList> result =
//                service.getResultsForCurrentParticipant(null, pageable);
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//
//        verify(quizResultRepository).searchByParticipantAndTopic(
//                7L,
//                null,
//                pageable
//        );
//    }
//
//    @Test
//    void getAllExamQuestions_onlyActive() {
//        Question question = new Question();
//        question.setId(1L);
//        question.setActive(true);
//
//        Page<Question> page = new PageImpl<>(List.of(question));
//        when(questionRepository.findByTopicId(1L, Pageable.unpaged())).thenReturn(page);
//
//        when(questionMapper.toQuestionExamDtoFromQuestionDbo(question))
//                .thenReturn(new QuestionExamDto());
//
//        Answer answer = new Answer();
//        answer.setActive(true);
//
//        when(answerService.getAnswersByQuestionId(1L)).thenReturn(List.of(answer));
//        when(answerMapper.toAnswerExamDtoList(anyList())).thenReturn(List.of());
//
//        List<QuestionExamDto> result = service.getAllExamQuestions(1L);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getExamDetail_success() {
//        QuizResult quizResult = new QuizResult();
//        quizResult.setParticipantId(1L);
//        quizResult.setCorrectAnswersCount(1L);
//        quizResult.setQuestionsCount(1L);
//        quizResult.setCorrectPercent(100L);
//
//        Topic topic = new Topic();
//        topic.setName("Math");
//        quizResult.setTopic(topic);
//
//        when(quizResultRepository.findById(1L)).thenReturn(Optional.of(quizResult));
//
//        Question question = new Question();
//        question.setQuestion("2+2?");
//        question.setActive(true);
//
//        Answer answer = new Answer();
//        answer.setId(10L);
//        answer.setAnswer("4");
//        answer.setCorrect(true);
//        answer.setActive(true);
//
//        question.setAnswers(Set.of(answer));
//
//        ParticipantAnswer pa = new ParticipantAnswer();
//        pa.setQuestion(question);
//        pa.setAnswerId(10L);
//
//        when(participantAnswerRepository.findByQuizResultId(1L))
//                .thenReturn(List.of(pa));
//
//        ParticipantQuizResultDetail detail = service.getExamDetail(1L);
//
//        assertEquals("Math", detail.getTopicName());
//        assertEquals(1, detail.getQuestions().size());
//    }
//
//    @Test
//    void getExamDetail_notFound() {
//        when(quizResultRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(IllegalArgumentException.class, () -> service.getExamDetail(1L));
//    }
//}
