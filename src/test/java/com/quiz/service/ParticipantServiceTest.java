//package com.quiz.service;
//
//import com.quiz.dto.participant.ParticipantListDto;
//import com.quiz.dto.profile.ProfileProjectionEdit;
//import com.quiz.dto.profile.ProfileProjectionEditDto;
//import com.quiz.entity.Attachment;
//import com.quiz.entity.Participant;
//import com.quiz.enums.OwnerType;
//import com.quiz.mapper.ParticipantMapper;
//import com.quiz.repository.ParticipantRepository;
//import com.quiz.service.AttachmentService;
//import com.quiz.service.ParticipantService;
//import com.quiz.util.session.AuthSessionData;
//import com.quiz.util.session.ParticipantSessionData;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.Collections;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ParticipantServiceTest {
//
//    @Mock
//    private ParticipantRepository participantRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AttachmentService attachmentService;
//
//    @Mock
//    private ParticipantMapper participantMapper;
//
//    @Mock
//    private AuthSessionData authSessionData;
//
//    @InjectMocks
//    private ParticipantService participantService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void register_success() {
//        when(participantRepository.findByEmail("test@mail.com"))
//                .thenReturn(Optional.empty());
//        when(passwordEncoder.encode("123")).thenReturn("ENCODED");
//
//        when(participantRepository.save(any()))
//                .thenAnswer(i -> i.getArgument(0));
//
//        Participant result = participantService.register(
//                "Ali", "Veli", "test@mail.com", "123", "123"
//        );
//
//        assertNotNull(result);
//        verify(participantRepository).save(any());
//    }
//
//    @Test
//    void register_passwordMismatch() {
//        assertThrows(IllegalArgumentException.class, () ->
//                participantService.register("Ali", "Veli", "a@mail.com", "1", "2"));
//    }
//
//    @Test
//    void register_emailAlreadyExists() {
//        when(participantRepository.findByEmail("a@mail.com"))
//                .thenReturn(Optional.of(new Participant()));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                participantService.register("Ali", "Veli", "a@mail.com", "1", "1"));
//    }
//
//    @Test
//    void findByEmail_success() {
//        Participant p = new Participant();
//        when(participantRepository.findByEmail("a@mail.com"))
//                .thenReturn(Optional.of(p));
//
//        assertEquals(p, participantService.findByEmail("a@mail.com"));
//    }
//
//    @Test
//    void findByEmail_notFound() {
//        when(participantRepository.findByEmail(any()))
//                .thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> participantService.findByEmail("x@mail.com"));
//    }
//
//    private void mockSession(Long id) {
//        Participant participant = new Participant();
//        participant.setId(id);
//
//        ParticipantSessionData sessionData = new ParticipantSessionData();
//        sessionData.setParticipant(participant);
//
//        when(authSessionData.getParticipantSessionData())
//                .thenReturn(sessionData);
//    }
//
//    @Test
//    void updateProfile_withoutPassword_withoutFile() {
//        mockSession(1L);
//
//        when(participantRepository.findById(1L))
//                .thenReturn(Optional.of(new Participant()));
//
//        participantService.updateProfile(new ProfileProjectionEditDto());
//
//        verify(participantRepository).save(any());
//    }
//
//    @Test
//    void updateProfile_withPassword() {
//        mockSession(1L);
//
//        ProfileProjectionEditDto dto = new ProfileProjectionEditDto();
//        dto.setPassword("123");
//        dto.setConfirmPassword("123");
//
//        Participant participant = new Participant();
//
//        when(participantRepository.findById(1L))
//                .thenReturn(Optional.of(participant));
//        when(passwordEncoder.encode("123"))
//                .thenReturn("ENCODED");
//
//        participantService.updateProfile(dto);
//
//        assertEquals("ENCODED", participant.getPassword());
//    }
//
//    @Test
//    void updateProfile_passwordMismatch() {
//        mockSession(1L);
//
//        ProfileProjectionEditDto dto = new ProfileProjectionEditDto();
//        dto.setPassword("1");
//        dto.setConfirmPassword("2");
//
//        when(participantRepository.findById(1L))
//                .thenReturn(Optional.of(new Participant()));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> participantService.updateProfile(dto));
//    }
//
//    @Test
//    void updateProfile_withFile() {
//        mockSession(1L);
//
//        ProfileProjectionEditDto dto = new ProfileProjectionEditDto();
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.isEmpty()).thenReturn(false);
//        dto.setFile(file);
//
//        Participant participant = new Participant();
//        participant.setId(1L);
//
//        when(participantRepository.findById(1L))
//                .thenReturn(Optional.of(participant));
//        when(attachmentService.uploadAndReturn(eq(1L), eq(OwnerType.PARTICIPANT), eq(file)))
//                .thenReturn(new Attachment());
//
//        participantService.updateProfile(dto);
//
//        verify(attachmentService).deleteByOwner(1L, OwnerType.PARTICIPANT);
//        verify(participantRepository).save(participant);
//    }
//
//    @Test
//    void getProfile_success() {
//        ProfileProjectionEdit projection = mock(ProfileProjectionEdit.class);
//
//        when(projection.getId()).thenReturn(1L);
//        when(projection.getFirstName()).thenReturn("Ali");
//        when(projection.getLastName()).thenReturn("Veli");
//        when(projection.getFatherName()).thenReturn("Hasan");
//        when(projection.getEmail()).thenReturn("a@mail.com");
//        when(projection.getAttachmentId()).thenReturn(null);
//        when(projection.getAttachmentUrl()).thenReturn(null);
//
//        when(participantRepository.findProfileProjectionById(1L))
//                .thenReturn(Optional.of(projection));
//
//        ProfileProjectionEditDto result = participantService.getProfile(1L);
//
//        assertNotNull(result);
//        assertEquals("Ali", result.getFirstName());
//    }
//
//    @Test
//    void getProfile_notFound() {
//        when(participantRepository.findProfileProjectionById(1L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> participantService.getProfile(1L));
//    }
//
//    @Test
//    void findById_success() {
//        Participant p = new Participant();
//        when(participantRepository.findById(1L))
//                .thenReturn(Optional.of(p));
//
//        assertEquals(p, participantService.findById(1L));
//    }
//
//    @Test
//    void findById_notFound() {
//        when(participantRepository.findById(any()))
//                .thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> participantService.findById(1L));
//    }
//
//    @Test
//    void changeStatus() {
//        participantService.changeStatus(1L, false);
//        verify(participantRepository).changeStatus(1L, false);
//    }
//
//    @Test
//    void getAll() {
//        Page<Participant> page =
//                new PageImpl<>(Collections.singletonList(new Participant()));
//
//        when(participantRepository.findAll(Pageable.unpaged()))
//                .thenReturn(page);
//        when(participantMapper.toListDto(any()))
//                .thenReturn(mock(ParticipantListDto.class));
//
//
//        Page<ParticipantListDto> result =
//                participantService.getAll(Pageable.unpaged());
//
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    void searchUsers_withName() {
//        Page<Participant> page =
//                new PageImpl<>(Collections.singletonList(new Participant()));
//
//        when(participantRepository.searchByFullName(eq("ali"), any()))
//                .thenReturn(page);
//
//        assertEquals(1,
//                participantService.searchUsers("ali", Pageable.unpaged())
//                        .getTotalElements());
//    }
//
//    @Test
//    void searchUsers_withoutName() {
//        Page<Participant> page =
//                new PageImpl<>(Collections.singletonList(new Participant()));
//
//        when(participantRepository.findAll(Pageable.unpaged()))
//                .thenReturn(page);
//
//        Page<Participant> result =
//                participantService.searchUsers("", Pageable.unpaged());
//
//        assertEquals(1, result.getTotalElements());
//    }
//
//}
//
