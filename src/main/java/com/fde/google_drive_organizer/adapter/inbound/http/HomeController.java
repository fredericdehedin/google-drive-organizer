package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.usecase.ListDriveFilesUC;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final String DRIVE_FILES = "driveFiles";
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final ListDriveFilesUC listDriveFilesUseCase;

    public HomeController(ListDriveFilesUC listDriveFilesUseCase) {
        this.listDriveFilesUseCase = listDriveFilesUseCase;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User user, Model model) {
        if (user != null) {
            try {
                List<DriveFile> files = listDriveFilesUseCase.execute();
                model.addAttribute(DRIVE_FILES, files);
            } catch (IllegalStateException e) {
                log.warn("Failed to retrieve drive files: {}", e.getMessage());
                model.addAttribute(DRIVE_FILES, Collections.emptyList());
            }
        } else {
            model.addAttribute(DRIVE_FILES, Collections.emptyList());
        }

        model.addAttribute("message", "Hello HTMX from Controller");
        return "index";
    }
}