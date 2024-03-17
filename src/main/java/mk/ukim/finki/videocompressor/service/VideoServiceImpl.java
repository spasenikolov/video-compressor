package mk.ukim.finki.videocompressor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl implements VideoService {

  @SneakyThrows
  @Override
  public byte[] compressVideo(InputStream videoStream, String codec, String audioCodec, String extension, String resolution) {
    extension = "." + extension;
    File tempInputFile = File.createTempFile(UUID.randomUUID().toString(), extension);
    try {
      Files.copy(videoStream, tempInputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      String ffmpegCommand = "ffmpeg -i " + tempInputFile.getAbsolutePath() +
          " -c:v " + codec +
          " -vf scale=" + resolution +
          " -preset slow -c:a " + audioCodec +
          " -movflags +faststart output" + extension;

      return process(extension, ffmpegCommand);
    } catch (IOException | InterruptedException e) {
      throw new IOException("Failed to compress video", e);
    } finally {
      tempInputFile.delete();
      new File("output" + extension).delete();
    }
  }

  @SneakyThrows
  @Override
  public byte[] resizeVideo(InputStream videoStream, String extension, String resolution) {
    extension = "." + extension;
    File tempInputFile = File.createTempFile(UUID.randomUUID().toString(), extension);
    try {
      Files.copy(videoStream, tempInputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      String ffmpegCommand = "ffmpeg -i " + tempInputFile.getAbsolutePath() +
          " -vf scale=" + resolution +
          " -c:a copy output" + extension;

      return process(extension, ffmpegCommand);
    } catch (IOException | InterruptedException e) {
      throw new IOException("Failed to resize video", e);
    } finally {
      tempInputFile.delete();
      new File("output" + extension).delete();
    }
  }


  @SneakyThrows
  @Override
  public byte[] changeFormat(InputStream videoStream, String oldExtension, String newExtension) {
    oldExtension = "." + oldExtension;
    newExtension = "." + newExtension;
    File tempInputFile = File.createTempFile(UUID.randomUUID().toString(), oldExtension);
    try {
      Files.copy(videoStream, tempInputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      String ffmpegCommand = "ffmpeg -i " + tempInputFile.getAbsolutePath() +
          " -c:a copy output" + newExtension;

      return process(newExtension, ffmpegCommand);
    } catch (IOException | InterruptedException e) {
      throw new IOException("Failed to change video format", e);
    } finally {
      tempInputFile.delete();
      new File("output" + newExtension).delete();
    }
  }

  private byte[] process(String extension, String ffmpegCommand) throws IOException, InterruptedException {

    Process process = execute(ffmpegCommand);

    int exitCode = process.waitFor();
    if (exitCode == 0) {
      File compressedFile = new File("output" + extension);
      return Files.readAllBytes(compressedFile.toPath());
    } else {
      throw new IOException("FFMPEG process returned non-zero exit code: " + exitCode);
    }
  }

  private Process execute(String ffmpegCommand) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("bash", "-c", ffmpegCommand);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    return process;
  }
}

