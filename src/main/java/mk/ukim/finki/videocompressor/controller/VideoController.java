package mk.ukim.finki.videocompressor.controller;

import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.videocompressor.service.VideoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class VideoController {

  private final VideoService videoService;

  public VideoController(VideoService videoService) {
    this.videoService = videoService;
  }

  @PostMapping("/upload")
  public ResponseEntity<byte[]> handleFileUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam("codec") String codec,
      @RequestParam("audioCodec") String audioCodec,
      @RequestParam("extension") String extension,
      @RequestParam("resolution") String resolution) {
    try {
      byte[] compressedVideo = videoService.compressVideo(file.getInputStream(), codec, audioCodec, extension, resolution);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("filename", "compressed_video." + extension);
      return new ResponseEntity<>(compressedVideo, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/resize-file")
  public ResponseEntity<byte[]> resizeFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("extension") String extension,
      @RequestParam("resolution") String resolution) {
    try {
      byte[] compressedVideo = videoService.resizeVideo(file.getInputStream(), extension, resolution);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("filename", "compressed_video." + extension);
      return new ResponseEntity<>(compressedVideo, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/change-format")
  public ResponseEntity<byte[]> changeFormat(
      @RequestParam("file") MultipartFile file,
      @RequestParam("oldExtension") String oldExtension,
      @RequestParam("newExtension") String newExtension) {
    try {
      byte[] compressedVideo = videoService.changeFormat(file.getInputStream(), oldExtension, newExtension);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("filename", "compressed_video." + newExtension);
      return new ResponseEntity<>(compressedVideo, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
