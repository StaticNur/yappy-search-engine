package com.yappy.reading_audio.service;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

@Service
public class AudioExtractorService {

    public void extractAndSaveAudio(String videoUrl, String outputPath) throws Exception {
        // Загрузка видео из URL
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoUrl);
        grabber.start();

        // Получение информации об аудио
        AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, false);
        AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(new byte[0]), audioFormat, grabber.getLengthInTime());

        // Создание файла для сохранения аудио
        OutputStream outputStream = new FileOutputStream(outputPath);

        // Чтение каждого кадра видео
        Frame frame;
        while ((frame = grabber.grab()) != null) {
            // Выбор только аудио кадров
            if (frame.samples != null) {
                // Запись аудио данных
                ShortBuffer samples = (ShortBuffer) frame.samples[0];
                ByteBuffer byteBuffer = ByteBuffer.allocate(samples.capacity() * 2);
                for (int i = 0; i < samples.capacity(); i++) {
                    byteBuffer.putShort(samples.get(i));
                }
                outputStream.write(byteBuffer.array());
            }
        }

        // Освобождение ресурсов
        grabber.stop();
        outputStream.close();

        // Сохранение аудио в файл
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(outputPath));
    }

    public byte[] extractAudioFromVideo(String videoUrl) throws Exception {
        // Загрузка видео из URL
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoUrl);
        grabber.start();

        // Переменная для хранения аудио данных
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Чтение каждого кадра видео
        Frame frame;
        while ((frame = grabber.grab()) != null) {
            // Выбор только аудио кадров
            if (frame.samples != null) {
                // Запись аудио данных
                ShortBuffer samples = (ShortBuffer) frame.samples[0];
                ByteBuffer byteBuffer = ByteBuffer.allocate(samples.capacity() * 2);
                for (int i = 0; i < samples.capacity(); i++) {
                    byteBuffer.putShort(samples.get(i));
                }
                outputStream.write(byteBuffer.array());
            }
        }

        // Освобождение ресурсов
        grabber.stop();
        outputStream.close();

        // Возвращение аудио данных в виде байтов
        return outputStream.toByteArray();
    }

   /* public static void main(String[] args) {
        String videoFilePath = "путь_к_видеофайлу.mp4";
        String audioFilePath = "путь_к_аудиофайлу.wav";

        try {
            // Инициализация FFmpegFrameGrabber для чтения видеофайла
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath);
            grabber.start();

            // Создание нового файлового дескриптора для записи аудиофайла
            avutil.av_log_set_level(avutil.AV_LOG_ERROR); // Установка уровня логирования FFmpeg
            FrameGrabber audioGrabber = FrameGrabber.create(audioFilePath, 0);

            // Установка параметров для аудиофайла (формат, количество каналов, частота дискретизации и т.д.)
            audioGrabber.setAudioChannels(grabber.getAudioChannels());
            audioGrabber.setSampleRate(grabber.getSampleRate());
            audioGrabber.setFormat("wav");

            // Начало записи аудио в новый файл
            audioGrabber.start();

            // Чтение и запись каждого аудиофрейма
            Frame audioFrame;
            while ((audioFrame = grabber.grabSamples()) != null) {
                audioGrabber.record(audioFrame);
            }

            // Остановка записи и закрытие дескрипторов
            audioGrabber.stop();
            grabber.stop();

            System.out.println("Аудио успешно извлечено и сохранено в файл: " + audioFilePath);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }*/
}

