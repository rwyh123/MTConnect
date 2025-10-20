MTConnect Streams Simulator (Spring Boot)

Небольшой сервис, который эмулирует поток данных MTConnect (Streams) из локальных датасетов (CSV) и отдаёт XML по HTTP. Подходит для отладки интеграций, наполнения витрин данных и тестирования парсеров MTConnect.

Основные возможности

Генерация MTConnect Streams XML (v1.8) с шапкой Header и пустым/настраиваемым DeviceStream.

Маппинг колонок датасета к типам MTConnect (Samples / Events / Conditions) через правила.

Конфиг профилями Spring (local, dev, prod).

Простое REST-API: GET /mtconnect/current → application/xml.

Требования

JDK: 17 или 21 (LTS)

Gradle Wrapper (в репозитории): ./gradlew

OS: Windows / macOS / Linux

(Опционально) Docker 24+

Структура проекта (в общих чертах)
MTConnect/
├─ src/
│  ├─ main/java/com/example/MTConnect/
│  │  ├─ MtConnectApplication.java
│  │  ├─ controller/MTConnectController.java     # /mtconnect/current
│  │  ├─ service/MTConnectService.java           # сбор XML
│  │  ├─ data/CSVDataReader.java                 # чтение CSV
│  │  ├─ mapping/RowMapper.java                  # привязка колонок к MTConnect типам
│  │  └─ config/                                 # Beans, MappingProperties и пр.
│  └─ main/resources/
│     ├─ application.yml
│     └─ (при необходимости XSD/XML шаблоны)
├─ datasets/                                     # локальные CSV-датасеты (не коммитим, см. .gitignore)
├─ build.gradle.kts|build.gradle
├─ settings.gradle
└─ README.md

Быстрый старт (локально)

Клонируем и собираем:

git clone <repo-url>
cd MTConnect
./gradlew clean build


Запускаем (любой вариант):

# Вариант A: через BootRun (удобно для разработки)
./gradlew bootRun --args='--spring.profiles.active=local'

# Вариант B: через fat-jar (рантайм)
java -jar build/libs/*-SNAPSHOT.jar --spring.profiles.active=local


Проверяем эндпоинт:

curl -H "Accept: application/xml" http://localhost:8080/mtconnect/current


Ожидаемый ответ — MTConnect XML (Streams), например:

<MTConnectStreams xmlns="urn:mtconnect.org:MTConnectStreams:1.8">
  <Header creationTime="..." sender="CNC_Mill_Emu" instanceId="1" version="1.8" .../>
  <Streams>
    <DeviceStream name="CNC_Mill_Emu" uuid="EMU-001"/>
  </Streams>
</MTConnectStreams>

Конфигурация

Базовый application.yml хранит только безопасные дефолты. Локальные секреты/пути — в application-local.yml (в .gitignore).

Пример src/main/resources/application.yml:

server:
  port: 8080

spring:
  application:
    name: MTConnect

sim:
  device:
    name: CNC_Mill_Emu
    uuid: EMU-001
  mapping:
    # режим маппинга: EXPLICIT_ONLY | SMART | FALLBACK
    mode: EXPLICIT_ONLY
    # путь к rules-файлу (classpath:... или file:...)
    rulesFile: file:./datasets/mapping-rules.yaml

csv:
  # пример: откуда читать данные
  path: ./datasets/current.csv
  delimiter: ","
  header: true


Пример application-local.yml (не коммитим):

sim:
  mapping:
    rulesFile: file:./datasets/mapping-rules.local.yaml

csv:
  path: ./datasets/dev/current-small.csv

Mapping Rules (пример)
rules:
  - column: spindle_speed
    category: Samples
    type: RotaryVelocity
    dataItemId: spindle_rpm
    nativeUnits: REVOLUTION/MINUTE

  - column: mode
    category: Events
    type: ControllerMode
    dataItemId: ctrl_mode

  - column: alarm_code
    category: Conditions
    type: Alarm
    dataItemId: cnc_alarm
    subType: FAILURE


Мы ранее фиксировали, что колонки датасета маппятся на типы MTConnect Samples/Events/Conditions — эти правила как раз для этого.

Профили и запуск

local — разработка, локальные CSV и правила, подробные логи.

dev / prod — для стенда/продакшна; настраивайте rulesFile и csv.path через переменные окружения.

Запуск с профилем:

./gradlew bootRun --args='--spring.profiles.active=local'
# или
java -jar build/libs/*-SNAPSHOT.jar --spring.profiles.active=dev

Докер (опционально)

Dockerfile (multi-stage) можно добавить, чтобы собирать образ:

# сборка
./gradlew bootJar
docker build -t mtconnect-sim:latest .

# запуск (пробрасываем датасеты внутрь контейнера)
docker run --rm -p 8080:8080 \
  -v "$PWD/datasets:/app/datasets" \
  -e "SPRING_PROFILES_ACTIVE=local" \
  mtconnect-sim:latest


Проверка:

curl http://localhost:8080/mtconnect/current

Эндпоинты
Метод	Путь	Описание
GET	/mtconnect/current	Текущий MTConnect Streams XML
GET	/actuator/health	(если включен) статус приложения
Разработка

Форматирование/линт: ./gradlew spotlessApply

Тесты: ./gradlew test

Полная сборка: ./gradlew clean build

Запуск: ./gradlew bootRun

Рекомендуемые плагины качества: Spotless, Checkstyle/PMD, SpotBugs, OWASP Dependency-Check.

Отладка и типичные проблемы

Сообщение:
Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar
Что делать: убедиться, что commons-logging.jar не тянется транзитивно; при необходимости исключить зависимость в build.gradle.

JAXB IllegalAnnotationExceptions при сборке XML:
проверьте аннотации @XmlAccessorType, @XmlAttribute, @XmlValue и соответствие типов. Часто помогает выверить DTO/модели под JAXB и убрать конфликтующие Lombok-генерации (@Data иногда мешает equals/hashCode/toString).

Путь к датасету/правилам:
если используете относительные пути, запускайте из корня проекта или переходите на абсолютные/file:/classpath: URL.

Дорожная карта

 Расширить DeviceStream фактическими ComponentStream/Samples/Events/Conditions.

 Добавить swagger (springdoc) для служебных REST-эндпоинтов.

 Генерация XML по расписанию / подписка на SSE.

Лицензия

Укажите лицензию проекта (например, MIT или Apache-2.0) в файле LICENSE.

Контакты

Вопросы и предложения — создавайте Issue или пишите в обсуждения репозитория.