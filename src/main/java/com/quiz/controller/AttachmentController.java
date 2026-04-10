package com.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/attachments")
public class AttachmentController extends BaseController {

    private static String UPLOADED_FOLDER = "src/main/resources/static/uploads/";

    @GetMapping()
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file")
                                   MultipartFile file,
                                   RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Lütfen bir dosya seçin");
            return "redirect:uploadStatus";
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "Dosya yüklendi: " + file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
}
