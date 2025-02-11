package edu.kpi.developmentmethods.logic;

import edu.kpi.developmentmethods.dto.Example;
import edu.kpi.developmentmethods.dto.UrlEntry;
import edu.kpi.developmentmethods.storage.Storage;

import java.util.Random;

/**
 * Класс, который инкапсулирует в себя бизнес-логику приложения
 * (URL Shortener mantığını ekledik)
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
     * URL'yi kısaltır ve saklar
     * 
     * @param originalUrl Kullanıcının kaydetmek istediği orijinal URL
     * @return Kısaltılmış URL (Örn: http://short.ly/abc123)
     */
    public synchronized String shortenUrl(String originalUrl) {
        String shortKey = generateShortKey();
        
        // Aynı kısa URL'nin tekrar üretilmemesi için kontrol
        while (storage.get(shortKey) != null) {
            shortKey = generateShortKey();
        }

        UrlEntry urlEntry = new UrlEntry(originalUrl, shortKey);
        storage.put(shortKey, urlEntry);
        
        return BASE_URL + shortKey;
    }

    /**
     * Kısaltılmış URL'nin orijinal halini getirir
     * 
     * @param shortKey Kısaltılmış URL'nin son kısmı (Örn: "abc123")
     * @return Orijinal uzun URL, eğer bulunamazsa null döner
     */
    public String getOriginalUrl(String shortKey) {
        UrlEntry entry = storage.get(shortKey);
        return entry != null ? entry.getOriginalUrl() : null;
    }

    /**
     * Kısaltılmış URL'yi siler
     * 
     * @param shortKey Silinecek URL'nin kısa anahtarı
     * @return true eğer başarılı bir şekilde silindiyse, false aksi takdirde
     */
    public synchronized boolean deleteShortUrl(String shortKey) {
        if (storage.get(shortKey) != null) {
            storage.del(shortKey);
            return true;
        }
        return false;
    }

    /**
     * Rastgele 6 karakter uzunluğunda bir kısa URL anahtarı oluşturur
     * 
     * @return Kısa URL için rastgele anahtar
     */
    private String generateShortKey() {
        StringBuilder key = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            key.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return key.toString();
    }
}
