package com.quiz.service;

import com.quiz.dto.participant.ParticipantListDto;
import com.quiz.dto.profile.ProfileProjectionEditDto;
import com.quiz.entity.Attachment;
import com.quiz.entity.Participant;
import com.quiz.entity.VerificationToken;
import com.quiz.enums.OwnerType;
import com.quiz.mapper.ParticipantMapper;
import com.quiz.repository.ParticipantRepository;
import com.quiz.repository.VerificationTokenRepository;
import com.quiz.util.session.AuthSessionData;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentService attachmentService;
    private final ParticipantMapper participantMapper;
    private final AuthSessionData authSessionData;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public ParticipantService(ParticipantRepository participantRepository,
                              PasswordEncoder passwordEncoder,
                              AttachmentService attachmentService,
                              ParticipantMapper participantMapper,
                              AuthSessionData authSessionData,
                              EmailService emailService,
                              VerificationTokenRepository verificationTokenRepository
    ) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
        this.attachmentService = attachmentService;
        this.participantMapper = participantMapper;
        this.authSessionData = authSessionData;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public Participant register(String firstName,
                                String lastName,
                                String email,
                                String password,
                                String confirmPassword
    ) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Şifrə və təsdiq uyğun gəlmir");
        }

        if (participantRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Bu email artıq qeydiyyatdan keçib");
        }

        Participant participant = new Participant();
        participant.setFirstName(firstName);
        participant.setLastName(lastName);
        participant.setEmail(email);
        participant.setPassword(passwordEncoder.encode(password));
        participant.setStatus(false);
        participant = participantRepository.save(participant);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setParticipant(participant);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(email, token);
        return participant;
    }

    public Participant findByEmail(String username) {
        return participantRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with email: " + username));
    }

    @Transactional
    public void updateProfile(ProfileProjectionEditDto dto) {
        Long participantId = authSessionData.getParticipantSessionData().getId();
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı: " + participantId));

        participantMapper.updateParticipantFromProfileProjectionEditDto(dto, participant);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (dto.getConfirmPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("Şifrə və təsdiq uyğun gəlmir");
            }
            participant.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            attachmentService.deleteByOwner(participant.getId(), OwnerType.PARTICIPANT);
            Attachment attachment = attachmentService.uploadAndReturn(participant.getId(), OwnerType.PARTICIPANT, dto.getFile());
            attachment.setParticipant(participant);
            participant.setAttachment(attachment);
        }
        participantRepository.save(participant);
    }

    public ProfileProjectionEditDto getProfile(Long participantId) {
        return participantRepository.findProfileProjectionById(participantId)
                .map(projection -> {
                    ProfileProjectionEditDto dto = new ProfileProjectionEditDto();
                    dto.setId(projection.getId());
                    dto.setFirstName(projection.getFirstName());
                    dto.setLastName(projection.getLastName());
                    dto.setFatherName(projection.getFatherName());
                    dto.setEmail(projection.getEmail());
                    dto.setPhoneNumber(projection.getPhoneNumber());
                    dto.setBirthDate(projection.getBirthDate());
                    dto.setGender(projection.getGender());
                    dto.setAttachmentId(projection.getAttachmentId());
                    dto.setAttachmentUrl(projection.getAttachmentUrl());
                    return dto;
                })
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + participantId));
    }

    public Participant findById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + id));
    }

    @Transactional
    public void changeStatus(Long id,
                             Boolean status
    ) {
        participantRepository.changeStatus(id, status);
    }

    public Page<ParticipantListDto> getAll(Pageable pageable) {
        return participantRepository.findAll(pageable)
                .map(participantMapper::toListDto);
    }

    public Page<Participant> searchUsers(String name,
                                         Pageable pageable
    ) {
        if (name != null && !name.trim().isEmpty()) {
            return participantRepository.searchByFullName(name.trim(), pageable);
        }
        return participantRepository.findAll(pageable);
    }
}
