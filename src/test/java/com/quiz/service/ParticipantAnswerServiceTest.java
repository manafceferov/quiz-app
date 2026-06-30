//package com.quiz.service;
//
//import com.quiz.entity.ParticipantAnswer;
//import com.quiz.repository.ParticipantAnswerRepository;
//import com.quiz.service.ParticipantAnswerService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.util.Arrays;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class ParticipantAnswerServiceTest {
//
//    @Mock
//    private ParticipantAnswerRepository participantAnswerRepository;
//
//    @InjectMocks
//    private ParticipantAnswerService participantAnswerService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSaveAll() {
//        ParticipantAnswer answer1 = new ParticipantAnswer();
//        ParticipantAnswer answer2 = new ParticipantAnswer();
//        List<ParticipantAnswer> answers = Arrays.asList(answer1, answer2);
//
//        participantAnswerService.saveAll(answers);
//
//        verify(participantAnswerRepository, times(1)).saveAll(answers);
//    }
//
//    @Test
//    void testFindByQuizResultId() {
//        Long quizResultId = 1L;
//        ParticipantAnswer answer = new ParticipantAnswer();
//        List<ParticipantAnswer> expected = Arrays.asList(answer);
//
//        when(participantAnswerRepository.findByQuizResultId(quizResultId)).thenReturn(expected);
//
//        List<ParticipantAnswer> result = participantAnswerService.findByQuizResultId(quizResultId);
//
//        assertEquals(expected, result);
//        verify(participantAnswerRepository, times(1)).findByQuizResultId(quizResultId);
//    }
//}
