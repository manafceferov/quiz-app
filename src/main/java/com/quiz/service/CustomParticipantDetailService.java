package com.quiz.service;

import com.quiz.config.auth.CustomParticipantDetails;
import com.quiz.entity.Participant;
import com.quiz.repository.ParticipantRepository;
import com.quiz.util.session.ParticipantSessionData;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomParticipantDetailService implements UserDetailsService {

    private final ParticipantRepository repository;
    private final ParticipantSessionData participantSessionData;

    public CustomParticipantDetailService(ParticipantRepository repository,
                                          ParticipantSessionData participantSessionData
    ) {
        this.repository = repository;
        this.participantSessionData = participantSessionData;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Participant participant = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Participant not found with email: " + email));
//        if (!participant.getStatus()) {
//            throw new DisabledException("Hesabınız təsdiqlənməyib. Zəhmət olmasa emailinizi yoxlayın.");
//        }
        this.participantSessionData.setParticipant(participant);
        return new CustomParticipantDetails(participant);
    }
}
