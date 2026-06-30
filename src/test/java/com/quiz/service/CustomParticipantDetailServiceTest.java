//package com.quiz.service;
//
//import com.quiz.config.auth.CustomParticipantDetails;
//import com.quiz.entity.Participant;
//import com.quiz.repository.ParticipantRepository;
//import com.quiz.service.CustomParticipantDetailService;
//import com.quiz.util.session.ParticipantSessionData;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CustomParticipantDetailServiceTest {
//
//    @Mock
//    private ParticipantRepository participantRepository;
//
//    @Mock
//    private ParticipantSessionData participantSessionData;
//
//    @InjectMocks
//    private CustomParticipantDetailService service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void loadUserByUsername_success() {
//        Participant participant = new Participant();
//        participant.setId(1L);
//        participant.setEmail("user@mail.com");
//        participant.setPassword("secret");
//
//        when(participantRepository.findByEmail("user@mail.com"))
//                .thenReturn(Optional.of(participant));
//
//        UserDetails userDetails =
//                service.loadUserByUsername("user@mail.com");
//
//        assertNotNull(userDetails);
//        assertTrue(userDetails instanceof CustomParticipantDetails);
//        assertEquals("user@mail.com", userDetails.getUsername());
//
//        verify(participantSessionData, times(1))
//                .setParticipant(participant);
//    }
//
//    @Test
//    void loadUserByUsername_notFound() {
//        when(participantRepository.findByEmail("x@mail.com"))
//                .thenReturn(Optional.empty());
//
//        assertThrows(UsernameNotFoundException.class,
//                () -> service.loadUserByUsername("x@mail.com"));
//
//        verify(participantSessionData, never())
//                .setParticipant(any());
//    }
//}
