package OnlineAuctionPlatform.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveUpload(@PathVariable String filename, @RequestParam(value = "download", required = false) String download) {
        try {
            Path file = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType;
            String filenameLower = resource.getFilename().toLowerCase();
            if (filenameLower.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filenameLower.endsWith(".jpg") || filenameLower.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filenameLower.endsWith(".png")) {
                contentType = "image/png";
            } else if (filenameLower.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filenameLower.endsWith(".txt")) {
                contentType = "text/plain";
            } else if (filenameLower.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (filenameLower.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            HttpHeaders headers = new HttpHeaders();

            if (download != null && (download.equalsIgnoreCase("true") || download.equals("1"))) {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            } else {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
