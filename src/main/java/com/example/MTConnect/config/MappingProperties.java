package com.example.MTConnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 Это конфигурационный бин Spring Boot, чьи поля автоматически заполняются из настроек с префиксом sim.mapping (@ConfigurationProperties(prefix = "sim.mapping")).
 Хранит:
 rules : List<MapRule> — список правил сопоставления (по умолчанию пустой).
 mode : MappingMode — режим работы маппинга (значение по умолчанию EXPLICIT_ONLY).
 rulesFile : String — путь к внешнему YAML-файлу с правилами (classpath:... или файловый путь).

 Метод loadFromRulesFileIfPresent(ResourceLoader loader):
 Если указан rulesFile, загружает YAML через ObjectMapper(new YAMLFactory()).
 Поддерживает локации classpath: и file: (для обычного пути добавляется префикс file:).
 Читает структуру вида rules: [...] в вспомогательную обёртку RulesWrapper и, если правила найдены, заменяет текущий rules.
 Бросает понятные исключения, если файл не найден или парсинг не удался.
 Внутренний класс RulesWrapper — простая модель для корневого узла YAML (rules).
 */

@ConfigurationProperties(prefix = "sim.mapping")
public class MappingProperties {
    // fields...
    private List<MapRule> rules = new ArrayList<>();
    private MappingMode mode = MappingMode.EXPLICIT_ONLY;
    private String rulesFile; // classpath:... или файловый путь

    // methods...

    /** Загружает rules из rulesFile, если он задан. */
    public void loadFromRulesFileIfPresent(ResourceLoader loader) {
        if (rulesFile == null || rulesFile.isBlank()) return;

        String location = rulesFile.startsWith("classpath:") || rulesFile.startsWith("file:")
                ? rulesFile
                : "file:" + rulesFile;

        try {
            Resource resource = loader.getResource(location);
            if (!resource.exists()) {
                throw new IllegalStateException("Rules file not found: " + location);
            }
            ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
            try (InputStream in = resource.getInputStream()) {
                RulesWrapper wrapper = yaml.readValue(in, RulesWrapper.class);
                if (wrapper != null && wrapper.getRules() != null) {
                    this.rules = wrapper.getRules();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mapping rules from " + location + ": " + e.getMessage(), e);
        }
    }

    // getters/setters...
    public List<MapRule> getRules() { return rules; }
    public void setRules(List<MapRule> rules) { this.rules = rules; }

    public MappingMode getMode() { return mode; }
    public void setMode(MappingMode mode) { this.mode = mode; }

    public String getRulesFile() { return rulesFile; }
    public void setRulesFile(String rulesFile) { this.rulesFile = rulesFile; }

    /** Вспомогательная обёртка под структуру YAML: rules: [...] */
    public static class RulesWrapper {
        private List<MapRule> rules;
        public List<MapRule> getRules() { return rules; }
        public void setRules(List<MapRule> rules) { this.rules = rules; }
    }
}
