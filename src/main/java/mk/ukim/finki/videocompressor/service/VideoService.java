package mk.ukim.finki.videocompressor.service;

import java.io.InputStream;

public interface VideoService {

  byte[] compressVideo(InputStream inputStream, String codec, String audioCodec, String extension, String resolution);
  byte[] resizeVideo(InputStream inputStream, String extension, String resolution);
  byte[] changeFormat(InputStream inputStream, String oldExtension, String newExtension);
}
