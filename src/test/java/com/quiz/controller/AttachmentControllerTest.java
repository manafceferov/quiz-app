//package com.quiz.controller;
//
//import com.quiz.controller.AttachmentController;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class AttachmentControllerTest {
//
//    @Mock
//    private RedirectAttributes redirectAttributes;
//
//    @InjectMocks
//    private AttachmentController controller;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ✅ 1. index() səhifəsi
//    @Test
//    void testIndex_ShouldReturnUploadView() {
//        String result = controller.index();
//        assertEquals("upload", result);
//    }
//
//    // ✅ 2. uploadStatus() səhifəsi
//    @Test
//    void testUploadStatus_ShouldReturnUploadStatusView() {
//        String result = controller.uploadStatus();
//        assertEquals("uploadStatus", result);
//    }
//
//    // ✅ 3. Fayl seçilməyibsə
//    @Test
//    void testSingleFileUpload_WhenFileIsEmpty_ShouldRedirectWithMessage() {
//        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
//
//        String result = controller.singleFileUpload(emptyFile, redirectAttributes);
//
//        verify(redirectAttributes).addFlashAttribute("message", "Lütfen bir dosya seçin");
//        assertEquals("redirect:uploadStatus", result);
//    }
//
//    // ✅ 4. Fayl uğurla yüklənib
//    @Test
//    void testSingleFileUpload_WhenFileIsNotEmpty_ShouldSaveFileAndRedirect() throws IOException {
//        MockMultipartFile file = new MockMultipartFile(
//                "file", "test.txt", "text/plain", "Hello World".getBytes()
//        );
//
//        Path uploadPath = Paths.get("src/main/resources/static/uploads/test.txt");
//        Files.deleteIfExists(uploadPath); // testdən öncə silirik
//
//        String result = controller.singleFileUpload(file, redirectAttributes);
//
//        verify(redirectAttributes).addFlashAttribute("message", "Dosya yüklendi: test.txt");
//        assertEquals("redirect:/uploadStatus", result);
//        assertEquals(true, Files.exists(uploadPath));
//
//        Files.deleteIfExists(uploadPath); // testdən sonra təmizləyirik
//    }
//
//    // ❌ 5. IOException halı
//    @Test
//    void testSingleFileUpload_WhenIOExceptionOccurs_ShouldHandleException() throws IOException {
//        MockMultipartFile file = mock(MockMultipartFile.class);
//        when(file.isEmpty()).thenReturn(false);
//        when(file.getOriginalFilename()).thenReturn("fail.txt");
//        when(file.getBytes()).thenThrow(new IOException("Disk full"));
//
//        String result = controller.singleFileUpload(file, redirectAttributes);
//
//        // Exception atılsa belə, redirect davam edir
//        assertEquals("redirect:/uploadStatus", result);
//        verify(file).getBytes();
//    }
//}
