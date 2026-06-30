//package com.quiz.service;
//
//import com.quiz.entity.Attachment;
//import com.quiz.enums.OwnerType;
//import com.quiz.repository.AttachmentRepository;
//import com.quiz.service.AttachmentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AttachmentServiceTest {
//
//    @Mock
//    private AttachmentRepository attachmentRepository;
//
//    @InjectMocks
//    private AttachmentService attachmentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testUpload_NewFile() throws Exception {
//        MultipartFile file = new MockMultipartFile("file", "test.txt",
//                "text/plain", "Hello World".getBytes());
//
//        when(attachmentRepository.findByOwnerIdAndOwnerType(1L, OwnerType.USER)).thenReturn(Optional.empty());
//
//        attachmentService.uploadAndReturn(1L, OwnerType.USER, file);
//
//        verify(attachmentRepository, times(1)).save(any(Attachment.class));
//    }
//
//    @Test
//    void testUpload_ReplaceExistingFile() throws Exception {
//        MultipartFile file = new MockMultipartFile("file", "test.txt",
//                "text/plain", "Hello World".getBytes());
//
//        Attachment existing = new Attachment();
//        existing.setFileUrl("/uploads/old.txt");
//
//        when(attachmentRepository.findByOwnerIdAndOwnerType(1L, OwnerType.USER))
//                .thenReturn(Optional.of(existing));
//
//        attachmentService.uploadAndReturn(1L, OwnerType.USER, file);
//
//        verify(attachmentRepository, times(1)).delete(existing);
//        verify(attachmentRepository, times(1)).save(any(Attachment.class));
//    }
//
//    @Test
//    void testUploadAndReturn_NewFile() throws Exception {
//        MultipartFile file = new MockMultipartFile("file", "file.txt",
//                "text/plain", "Hello".getBytes());
//
//        when(attachmentRepository.findByOwnerIdAndOwnerType(2L, OwnerType.USER))
//                .thenReturn(Optional.empty());
//
//        Attachment saved = new Attachment();
//        when(attachmentRepository.save(any(Attachment.class))).thenReturn(saved);
//
//        Attachment result = attachmentService.uploadAndReturn(2L, OwnerType.USER, file);
//
//        assertNotNull(result);
//        verify(attachmentRepository, times(1)).save(any(Attachment.class));
//    }
//
//    @Test
//    void testDeleteByOwner_FileExists() {
//        Attachment attachment = new Attachment();
//        attachment.setFileUrl("/uploads/test.txt");
//
//        when(attachmentRepository.findByOwnerIdAndOwnerType(1L, OwnerType.USER))
//                .thenReturn(Optional.of(attachment));
//
//        attachmentService.deleteByOwner(1L, OwnerType.USER);
//
//        verify(attachmentRepository, times(1)).delete(attachment);
//    }
//
//    @Test
//    void testDeleteByOwner_FileNotExists() {
//        when(attachmentRepository.findByOwnerIdAndOwnerType(1L, OwnerType.USER))
//                .thenReturn(Optional.empty());
//
//        attachmentService.deleteByOwner(1L, OwnerType.USER);
//
//        verify(attachmentRepository, never()).delete(any());
//    }
//}
