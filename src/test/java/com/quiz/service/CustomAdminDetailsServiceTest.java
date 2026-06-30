//package com.quiz.service;
//
//import com.quiz.config.auth.CustomAdminDetails;
//import com.quiz.entity.Admin;
//import com.quiz.repository.AdminRepository;
//import com.quiz.service.CustomAdminDetailsService;
//import com.quiz.util.session.AdminSessionData;
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
//class CustomAdminDetailsServiceTest {
//
//    @Mock
//    private AdminRepository adminRepository;
//
//    @Mock
//    private AdminSessionData adminSessionData;
//
//    @InjectMocks
//    private CustomAdminDetailsService service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void loadUserByUsername_success() {
//        Admin admin = new Admin();
//        admin.setId(1L);
//        admin.setEmail("admin@mail.com");
//        admin.setPassword("secret");
//
//        when(adminRepository.findByEmail("admin@mail.com"))
//                .thenReturn(Optional.of(admin));
//
//        UserDetails userDetails =
//                service.loadUserByUsername("admin@mail.com");
//
//        assertNotNull(userDetails);
//        assertTrue(userDetails instanceof CustomAdminDetails);
//        assertEquals("admin@mail.com", userDetails.getUsername());
//
//        verify(adminSessionData, times(1)).setAdmin(admin);
//    }
//
//    @Test
//    void loadUserByUsername_notFound() {
//        when(adminRepository.findByEmail("x@mail.com"))
//                .thenReturn(Optional.empty());
//
//        assertThrows(UsernameNotFoundException.class,
//                () -> service.loadUserByUsername("x@mail.com"));
//
//        verify(adminSessionData, never()).setAdmin(any());
//    }
//}
