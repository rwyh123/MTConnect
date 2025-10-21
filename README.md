# MTConnect Streams Simulator

Cервис, который эмулирует поток данных **агента MTConnect** из локальных датасетов и отдаёт XML по HTTP. Преднозначен для наполнения витрин данных MTConnect.

### Справка:
MT Connect, открытый и бесплатный протокол, увеличивает совместимость между оборудованием с программными приложениями разных производителей. MTConnect является протоколом только для чтения, то есть он только считывает данные с контролируемых устройств, что делает его подходящим для автоматического сбора данных с парка оборудования.

К ключевым элементам, из которых состоит приложение MTConnect, относятся адаптер MTConnect, агент MTConnect и клиент MTConnect. Адаптер MTConnect (может быть как аппаратным, так и программным) преобразовывает все данные, поступающие с устройства, в формат, который распознает агент MTConnect (обычно SHDR). **Затем агент MTConnect сопоставляет эти данные с языком XML и позволяет клиенту MTConnect запрашивать данные по протоколу HTTP**.

## Основные возможности:

- Генерация MTConnect Streams XML v1.8 с шапкой Header и пустым/настраиваемым DeviceStream.

- Маппинг колонок датасета к типам MTConnect (Samples / Events / Conditions) через правила.

- Маппинг колонок датасета к типам MTConnect (Samples / Events / Conditions) через эвристический анализ.

- REST-API: GET /mtconnect/current → application/xml. 

## Требования:

JDK: 17 или 21 (LTS)

Gradle Wrapper: ./gradlew

OS: Windows / macOS / Linux

## Структура проекта в общих чертах:



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
    │     └─ data.csv # Датасет для примера работы приложения
    ├─ build.gradle
    ├─ settings.gradle
    └─ README.md

## **Быстрый старт локально**

### Клонируем и собираем в терминал:

git clone https://github.com/rwyh123/MTConnect

cd MTConnect

./gradlew clean build


### Запускаем:

**Вариант A: через BootRun**

./gradlew bootRun --args='--spring.profiles.active=local'

**Вариант B: через fat-jar** 

java -jar build/libs/*-SNAPSHOT.jar --spring.profiles.active=local


### Проверяем эндпоинт:

curl -H "Accept: application/xml" http://localhost:8080/mtconnect/current


### Ожидаемый ответ — MTConnect XML, пример:

    <MTConnectStreams xmlns="urn:mtconnect.org:MTConnectStreams:1.8">
        <Header creationTime="..." sender="CNC_Mill_Emu" instanceId="1" version="1.8" .../>
        <Streams>
            <DeviceStream name="CNC_Mill_Emu" uuid="EMU-001"/>
        </Streams>
    </MTConnectStreams>

## **Конфигурация:**

### Настройка приложения
### **src/main/resources/application.yml:**

    === Источник данных CSV ===
    sim:
        # Имя CSV-файла в ресурсах (src/main/resources)
        csvName: data.csv

        === Поведение при достижении конца файла: начать сначала (true) или вернуть null (false) ===
        loopAtEof: true

        === Настройки маппинга столбцов CSV → MTConnect ===
        mapping:
            # Режим работы реестра маппинга:
            # - EXPLICIT_ONLY   — использовать ТОЛЬКО правила из файла rulesFile
            # - HEURISTIC_ONLY  — игнорировать файл и использовать авто-эвристику
            mode: EXPLICIT_ONLY

            # Внешний YAML с правилами (мы подготовили mapping-rules.yaml)
            # Можно: classpath:..., file:/abs/path/..., или просто /abs/path/...
            rulesFile: classpath:mapping-rules.yaml

    === Порт приложения ===
    server:
        port: 8080

### Файл с правилами для обработки csv
### **mapping-rules.yaml** пример:
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

    
## Дорожная карта:

 Разработки умной системы обработки csv

## Контакты

https://t.me/Yakov_Legioncommander 
