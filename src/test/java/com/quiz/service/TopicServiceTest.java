//package com.quiz.service;
//
//import com.quiz.dto.topic.*;
//import com.quiz.dto.topic.TopicEditDto;
//import com.quiz.dto.topic.TopicInsertRequest;
//import com.quiz.dto.topic.TopicUpdateRequest;
//import com.quiz.dto.topic.TopicWithQuestionCountProjection;
//import com.quiz.entity.Topic;
//import com.quiz.mapper.TopicMapper;
//import com.quiz.repository.AnswerRepository;
//import com.quiz.repository.QuestionRepository;
//import com.quiz.repository.TopicRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.data.domain.*;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TopicServiceTest {
//
//    private TopicRepository topicRepository;
//    private TopicMapper topicMapper;
//    private QuestionRepository questionRepository;
//    private AnswerRepository answerRepository;
//
//    private TopicService topicService;
//
//    @BeforeEach
//    void setUp() {
//        topicRepository = mock(TopicRepository.class);
//        topicMapper = mock(TopicMapper.class);
//        questionRepository = mock(QuestionRepository.class);
//        answerRepository = mock(AnswerRepository.class);
//
//        topicService = new TopicService(
//                topicRepository,
//                topicMapper,
//                questionRepository,
//                answerRepository
//        );
//    }
//
//    @Test
//    void testSave() {
//        TopicInsertRequest request = new TopicInsertRequest();
//        Topic topic = new Topic();
//
//        when(topicMapper.toDboQuizTopicFromQuizTopicInsertRequest(request)).thenReturn(topic);
//
//        topicService.save(request);
//
//        verify(topicRepository).save(topic);
//    }
//
//    @Test
//    void testGetById_Found() {
//        Topic topic = new Topic();
//        TopicEditDto dto = new TopicEditDto();
//
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
//        when(topicMapper.toDto(topic)).thenReturn(dto);
//
//        TopicEditDto result = topicService.getById(1L);
//        assertEquals(dto, result);
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        when(topicRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class,
//                () -> topicService.getById(1L));
//    }
//
//    @Test
//    void testGetAll_Pageable() {
//        Page<Topic> page = new PageImpl<>(List.of(new Topic()));
//        when(topicRepository.findAll(Pageable.unpaged())).thenReturn(page);
//
//        Page<Topic> result = topicService.getAll(Pageable.unpaged());
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    void testGetAllTopics() {
//        Page<TopicWithQuestionCountProjection> page =
//                new PageImpl<>(Collections.emptyList());
//
//        when(topicRepository.getAllTopicsWithQuestionsCount(Pageable.unpaged()))
//                .thenReturn(page);
//
//        Page<TopicWithQuestionCountProjection> result =
//                topicService.getAllTopics(Pageable.unpaged());
//
//        assertNotNull(result);
//    }
//
//    @Test
//    void testGetTopics_WithName() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<TopicWithQuestionCountProjection> page =
//                new PageImpl<>(Collections.emptyList(), pageable, 0);
//
//        when(topicRepository.searchByNameWithQuestionCount(
//                eq("java"), eq(pageable)))
//                .thenReturn(page);
//
//        Page<TopicWithQuestionCountProjection> result =
//                topicService.getTopics("java", pageable);
//
//        assertNotNull(result);
//        assertEquals(0, result.getTotalElements());
//
//        verify(topicRepository)
//                .searchByNameWithQuestionCount("java", pageable);
//    }
//
//    @Test
//    void testUpdate() {
//        TopicUpdateRequest request = new TopicUpdateRequest();
//        Topic topic = new Topic();
//
//        when(topicMapper.toDboQuizTopicFromQuizTopicUpdateRequest(request)).thenReturn(topic);
//
//        topicService.update(request);
//
//        verify(topicRepository).save(topic);
//    }
//
//    @Test
//    void testDeleteById() {
//        topicService.deleteById(1L);
//        verify(topicRepository).deleteById(1L);
//    }
//
//    @Test
//    void testFindById() {
//        Topic topic = new Topic();
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
//
//        Optional<Topic> result = topicService.findById(1L);
//        assertTrue(result.isPresent());
//    }
//
//    @Test
//    void testGetAll_WithBlankName() {
//        Page<Topic> page = new PageImpl<>(List.of(new Topic()));
//        when(topicRepository.findAll(Pageable.unpaged())).thenReturn(page);
//
//        Page<Topic> result = topicService.getAll("", Pageable.unpaged());
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    void testGetAll_WithName() {
//        Page<Topic> page = new PageImpl<>(List.of(new Topic()));
//        when(topicRepository.searchByNameOrParticipant("math", Pageable.unpaged()))
//                .thenReturn(page);
//
//        Page<Topic> result = topicService.getAll("math", Pageable.unpaged());
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    void testChangeStatus_Deactivate() {
//        topicService.changeStatus(1L, false);
//
//        verify(topicRepository).changeStatus(1L, false);
//        verify(questionRepository).deactivateByTopicId(1L);
//        verify(answerRepository).deactivateByTopicId(1L);
//    }
//
//    @Test
//    void testChangeStatus_Activate() {
//        topicService.changeStatus(1L, true);
//
//        verify(topicRepository).changeStatus(1L, true);
//        verify(questionRepository, never()).deactivateByTopicId(anyLong());
//        verify(answerRepository, never()).deactivateByTopicId(anyLong());
//    }
//
//    @Test
//    void testGetAllByParticipant_WithoutName() {
//        Page<Topic> page = new PageImpl<>(List.of(new Topic()));
//
//        when(topicRepository.findByByParticipant(1L, Pageable.unpaged()))
//                .thenReturn(page);
//
//        Page<Topic> result =
//                topicService.getAllByParticipant(1L, "", Pageable.unpaged());
//
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    void testGetAllByParticipant_WithName() {
//        Page<Topic> page = new PageImpl<>(List.of(new Topic()));
//
//        when(topicRepository.findByByParticipantAndNameContainingIgnoreCase(
//                1L, "java", Pageable.unpaged()))
//                .thenReturn(page);
//
//        Page<Topic> result =
//                topicService.getAllByParticipant(1L, "java", Pageable.unpaged());
//
//        assertEquals(1, result.getTotalElements());
//    }
//}
