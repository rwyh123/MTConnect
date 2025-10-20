package com.example.MTConnect.data;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * CSVDataReader — источник "сырых" данных из CSV.
 * - читает CSV с заголовком (OpenCSV CSVReaderHeaderAware);
 * - отдаёт по строке как Map<column, value>;
 * - по EOF может "крутиться по кругу" (loopAtEof);
 * - всегда читает из ресурсов проекта по имени файла (src/main/resources).
 * <p>
 * Конфигурация (application.yml / args / env):
 * sim.csvName: "имя_файла.csv"  (обязательно)
 * sim.loopAtEof: true|false
 */

@Component
public class CSVDataReader implements Closeable {

    // fields...
    /// Конфиг / состояние
    private String csvName;      // может меняться через reload()
    private boolean loopAtEof;   // может меняться через reload()

    private Reader reader;
    private CSVReaderHeaderAware csv;
    private boolean closed = true;

    // constructors...

    /// основной конструктор: Spring подставит значения
    public CSVDataReader(
            @Value("${sim.csvName}") String csvName,
            @Value("${sim.loopAtEof:true}") boolean loopAtEof
    ) {
        this.csvName = csvName;
        this.loopAtEof = loopAtEof;
        reopen();
    }

    // methods...

    /**
     * Главный метод: вернуть следующую строку как Map<Header,Value>.
     * Потокобезопасен.
     *
     * @return Map<String,String> или null, если EOF и loopAtEof=false
     */
    public synchronized Map<String, String> getNextRow() {
        ensureOpen();
        try {
            Map<String, String> row = csv.readMap();

            if (row == null) {
                // EOF
                if (!loopAtEof) {
                    return null;
                }
                // loopAtEof=true — переоткрываем источник и продолжаем с начала
                reopen();
                row = csv.readMap(); // может быть null, если файл пуст
                if (row == null) {
                    return null;
                }
            }
            return row;

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("CSV read error: " + e.getMessage(), e);
        }
    }

    // Internal methods

    /**
     * Переоткрыть источник "с начала" (после заголовка). Потокобезопасно.
     */
    private synchronized void reopen() {
        closeQuietly();
        try {
            ClassPathResource resource = new ClassPathResource(csvName);
            if (!resource.exists()) {
                throw new IOException("Classpath resource not found: " + csvName);
            }
            this.reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            this.csv = new CSVReaderHeaderAware(reader); // заголовок будет считан lazily
            this.closed = false;
        } catch (IOException e) {
            throw new RuntimeException("Cannot open CSV resource (" + csvName + "): " + e.getMessage(), e);
        }
    }

    /**
     * Проверка, что поток открыт; при необходимости открыть.
     */
    private void ensureOpen() {
        if (closed || reader == null || csv == null) {
            reopen();
        }
        /// На будущее: можно добавить быстрый health-check, если потребуется.
    }

    /**
     * Тихое закрытие (для переоткрытия).
     */
    private void closeQuietly() {
        try { if (csv != null) csv.close(); } catch (Exception ignored) {}
        try { if (reader != null) reader.close(); } catch (Exception ignored) {}
        csv = null;
        reader = null;
        closed = true;
    }

    @Override
    public synchronized void close() {
        closeQuietly();
    }

    // utilities

    // getters/setters...
}
