package edu.kpi.developmentmethods.logic;

import edu.kpi.developmentmethods.dto.Example;
import edu.kpi.developmentmethods.dto.UrlEntry;
import edu.kpi.developmentmethods.storage.Storage;

import java.util.Random;

/**
 * Класс, который инкапсулирует в себя бизнес-логику приложения
 * (Добавлена логика сокращения URL)
 */
public class Logic {
    private final Storage storage;
    private static final String BASE_URL = "http://short.ly/";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6;
    private final Random random = new Random();

    public Logic(Storage storage) {
        this.storage = storage;
    }

    /**
     * Сокращает URL и сохраняет его
     * 
     * @param originalUrl Оригинальный URL, который пользователь хочет сохранить
     * @return Сокращенный URL (Например: http://short.ly/abc123)
     */
    public synchronized String shortenUrl(String originalUrl) {
        String shortKey = generateShortKey();
        
        // Проверка на случайное совпадение уже существующего короткого URL
        while (storage.get(shortKey) != null) {
            shortKey = generateShortKey();
        }

        UrlEntry urlEntry = new UrlEntry(originalUrl, shortKey);
        storage.put(shortKey, urlEntry);
        
        return BASE_URL + shortKey;
    }

    /**
     * Получает оригинальный URL по сокращенному ключу
     * 
     * @param shortKey Короткий URL-ключ (например, "abc123")
     * @return Оригинальный длинный URL или null, если он не найден
     */
    public String getOriginalUrl(String shortKey) {
        UrlEntry entry = storage.get(shortKey);
        return entry != null ? entry.getOriginalUrl() : null;
    }

    /**
     * Удаляет сокращенный URL
     * 
     * @param shortKey Ключ удаляемого URL
     * @return true, если URL был успешно удален, false в противном случае
     */
    public synchronized boolean deleteShortUrl(String shortKey) {
        if (storage.get(shortKey) != null) {
            storage.del(shortKey);
            return true;
        }
        return false;
    }

    /**
     * Генерирует случайный 6-символьный короткий URL-ключ
     * 
     * @return Случайно сгенерированный ключ для короткого URL
     */
    private String generateShortKey() {
        StringBuilder key = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            key.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return key.toString();
    }
}
