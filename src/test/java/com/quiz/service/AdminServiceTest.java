//package com.quiz.service;
//
//import com.quiz.dto.admin.AdminInsertRequest;
//import com.quiz.dto.admin.AdminUpdateRequest;
//import com.quiz.entity.Admin;
//import com.quiz.entity.Attachment;
//import com.quiz.enums.OwnerType;
//import com.quiz.mapper.AdminMapper;
//import com.quiz.repository.AdminRepository;
//import com.quiz.service.AdminService;
//import com.quiz.service.AttachmentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AdminServiceTest {
//
//    @Mock
//    private AdminRepository repository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AttachmentService attachmentService;
//
//    @Mock
//    private AdminMapper mapper;
//
//    @InjectMocks
//    private AdminService adminService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSaveAdminWithAttachment() {
//        AdminInsertRequest request = new AdminInsertRequest();
//        request.setEmail("test@test.com");
//        request.setPassword("123456");
//
//        Admin admin = new Admin();
//        admin.setId(1L);
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
//        when(mapper.toDboUserFromUserInsertRequest(request)).thenReturn(admin);
//        when(repository.save(admin)).thenReturn(admin);
//
//        MockMultipartFile file = new MockMultipartFile(
//                "file", "test.txt", "text/plain", "Hello".getBytes()
//        );
//        request.setFile(file);
//
//        Attachment mockAttachment = new Attachment();
//        when(attachmentService.uploadAndReturn(eq(admin.getId()), eq(OwnerType.USER), eq(file)))
//                .thenReturn(mockAttachment);
//
//        adminService.save(request);
//
//        verify(repository, times(2)).save(admin);
//        assertEquals("encoded", request.getPassword());
//    }
//
//    @Test
//    void testSaveAdminWithoutAttachment() {
//        AdminInsertRequest request = new AdminInsertRequest();
//        request.setEmail("test@test.com");
//        request.setPassword("123456");
//
//        Admin admin = new Admin();
//        admin.setId(1L);
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
//        when(mapper.toDboUserFromUserInsertRequest(request)).thenReturn(admin);
//        when(repository.save(admin)).thenReturn(admin);
//
//        request.setFile(null);
//
//        adminService.save(request);
//
//        verify(repository, times(1)).save(admin);
//        assertEquals("encoded", request.getPassword());
//        verify(attachmentService, never()).uploadAndReturn(anyLong(), any(), any());
//    }
//
//    @Test
//    void testUpdateAdminPasswordChange() {
//        AdminUpdateRequest request = new AdminUpdateRequest();
//        request.setId(1L);
//        request.setPassword("newpass");
//
//        Admin existingAdmin = new Admin();
//        existingAdmin.setId(1L);
//        existingAdmin.setPassword("oldpass");
//
//        when(repository.findById(1L)).thenReturn(Optional.of(existingAdmin));
//        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
//        when(mapper.toDboUserFromUserUpdateRequest(request)).thenReturn(existingAdmin);
//
//        adminService.update(request);
//
//        verify(repository, times(1)).save(existingAdmin);
//        assertEquals("encodedpass", request.getPassword());
//    }
//
//    @Test
//    void testUpdateAdminPasswordEmpty() {
//        AdminUpdateRequest request = new AdminUpdateRequest();
//        request.setId(1L);
//        request.setPassword(null);
//
//        Admin existingAdmin = new Admin();
//        existingAdmin.setId(1L);
//        existingAdmin.setPassword("oldpass");
//
//        when(repository.findById(1L)).thenReturn(Optional.of(existingAdmin));
//        when(mapper.toDboUserFromUserUpdateRequest(request)).thenReturn(existingAdmin);
//
//        adminService.update(request);
//
//        verify(repository, times(1)).save(existingAdmin);
//        assertEquals("oldpass", request.getPassword());
//    }
//
//    @Test
//    void testDeleteAdmin() {
//        Long id = 1L;
//
//        adminService.deleteById(id);
//
//        verify(attachmentService, times(1)).deleteByOwner(eq(id), eq(OwnerType.USER));
//        verify(repository, times(1)).deleteById(id);
//    }
//
//    @Test
//    void testExistsByEmail() {
//        when(repository.existsByEmail("test@test.com")).thenReturn(true);
//
//        boolean exists = adminService.existsByEmail("test@test.com");
//
//        assertTrue(exists);
//        verify(repository, times(1)).existsByEmail("test@test.com");
//    }
//
//    @Test
//    void testEditAdmin() {
//        Admin admin = new Admin();
//        admin.setId(1L);
//
//        when(repository.findById(1L)).thenReturn(Optional.of(admin));
//
//        Admin result = adminService.edit(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testSearchUsersWithName() {
//        Pageable pageable = mock(Pageable.class);
//        Admin admin = new Admin();
//        List<Admin> list = Collections.singletonList(admin);
//        Page<Admin> page = new PageImpl<>(list);
//
//        when(repository.searchByFullName("John", pageable)).thenReturn(page);
//
//        Page<Admin> result = adminService.searchUsers("John", pageable);
//
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//        verify(repository, times(1)).searchByFullName("John", pageable);
//    }
//
//    @Test
//    void testSearchUsersWithoutName() {
//        Pageable pageable = mock(Pageable.class);
//        Page<Admin> page = new PageImpl<>(Collections.emptyList());
//
//        when(repository.findAll(pageable)).thenReturn(page);
//
//        Page<Admin> result = adminService.searchUsers(null, pageable);
//
//        assertNotNull(result);
//        assertEquals(0, result.getContent().size());
//        verify(repository, times(1)).findAll(pageable);
//    }
//
//    @Test
//    void testSaveAllAdmins() {
//        AdminInsertRequest request1 = new AdminInsertRequest();
//        request1.setPassword("pass1");
//        AdminInsertRequest request2 = new AdminInsertRequest();
//        request2.setPassword("pass2");
//
//        Admin admin1 = new Admin();
//        Admin admin2 = new Admin();
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
//        when(mapper.toDboUserFromUserInsertRequest(request1)).thenReturn(admin1);
//        when(mapper.toDboUserFromUserInsertRequest(request2)).thenReturn(admin2);
//        when(repository.save(admin1)).thenReturn(admin1);
//        when(repository.save(admin2)).thenReturn(admin2);
//
//        adminService.saveAll(List.of(request1, request2));
//
//        verify(repository, times(2)).save(any(Admin.class));
//        assertEquals("encoded", request1.getPassword());
//        assertEquals("encoded", request2.getPassword());
//    }
//
//    @Test
//    void testChangeStatus() {
//        Long id = 1L;
//        Boolean status = true;
//
//        adminService.changeStatus(id, status);
//        verify(repository, times(1)).changeStatus(id, status);
//    }
//}
