Android-клиент для rutracker.org. Так как rutracker.org не предоставляет
публичного API, все сетевые вызовы осуществляются через собственный API-сервер
[(ссылка)](https://github.com/arBespalov/rt-api),
основной задачей которого является парсинг HTML-страниц rutracker.org и конвертация информации в
JSON для дальнейшей передачи клиенту. Альтернативный архитектурный подход, без собственного
API-сервера, с парсингом HTML прямо на устройстве, технически возможен, но имеет существенные
минусы:
- Основной недостаток, ввиду зависимости на внешнее нестабильное API - невозможность мгновенного
обновления приложений на устройствах пользователей. При публикации в Google Play скорость
доставки обновлений усугубляется модерацией. Например, при небольшом изменении HTML-разметки
ломается парсинг, а при использовании прослойки из собственного API-сервера исправления вносятся
мгновенно для всех клиентов.
- Невозможность реализации стабильных push-уведомлений. Вариант с поллингом сервера прямо с
устройства нестабилен из-за тенденции на постоянное усиление ограничений Android на
работу приложений в фоне, а так же из-за дополнительных ограничений, накладываемых вендорами
устройств.
- Специфический минус - блокировка rutracker.org в России, и соответственно, возможность
использования приложения только с включенным VPN, либо использование зеркал rutracker.org,
которые, однако, так же блокируются и необходимо обновлять или просить пользователя искать и
вводить их самостоятельно. При использовании API-сервера VPN и зеркала на стороне клиента не нужны.
Проблемы соединения между сервером и rutracker.org решаются либо с помощью зеркал, либо с помощью
переноса сервера в локацию, где доступ не заблокирован. Соединение клиент-сервер более уязвимо,
при блокировании URL сервера придется либо обновлять приложение с новым URL, либо, каким-либо
способом сообщать новый URL сервера клиентам в рантайме. Например, как это делал Telegram во время
блокировки, сообщать клиентам новые незаблокированные URL через Firebase Cloud Messaging.

#### Основные детали реализации:
- Clean architecture + MVVM
- Горизонтальное разбиение на feature-модули
- UI на Jetpack Compose
- Общение с сервером через GraphQL
- Многопоточность на Kotlin Coroutines и Kotlin Flows
- DI на Dagger 2

#### Основные фичи:
- Поиск раздач с учетом заданного метода сортировки (по дате добавления, по количесту сидов и т.д.).
Сервер проксирует запрос клиента на rutracker.org, кэширует ответ и отдает пагинированный список.
Пагинация реализована с помощью Jetpack Pagination 3.
<details>
<summary>GIF</summary>
<img src="screenshots/search.gif">
</details>
- Экран описания раздачи реализован с помощью кастомного Server-Driven UI. Сервер загружает страницу
раздачи и, рекурсивно проходя HTML-дерево, формирует JSON следующего вида:
      {
        "type": "column",
        "children": [
            {
              "type": "text",
              "text": "sampleText"
            },
            {
              "type": "image",
              "url": "http://example.com/image.png",
              "width": 500,
              "height": 500,
            },
            {  
              "type": "column",
              "children": [
                ...
              ]
            },
        ]
      }
Клиент, используя полиморфную десериализацию (класс `RuntimeTypeAdapterFactory` в библиотеке GSON),
преобразует JSON в объект типа `SDUIComponent`, наследниками которого являются `SDUIColumnModel`,
`SDUITextModel` и т.д. При рендеринге UI для объекта `SDUIComponent` вызывается функция
`ToComposable()`, где каждому типу сопоставлена собственная composable - функция. При наличии у
компонента поля `children` (column, row и т. д.) функция `ToComposable()` вызывается рекурсивно.
<details>
<summary>GIF</summary>
<img src="screenshots/sdui.gif">
</details>
- Скачивание torrent-файлов реализовано через системный сервис `DownloadManager`.
- Во время ввода поискового запроса приложением с сервера запрашиваются соответствующие поисковые
подсказки. БД подсказок неявно наполняется самими пользователями при запросах с ненулевым
количеством результатов.
- При запуске приложения отображаются популярные запросы за последние N дней (N задается в конфигах
сервера). Это реализовано благодаря тому, что наряду с подсказками БД хранит массив временных
меток запросов.
- Возможность добавлять раздачи в избранное. В качестве хранилища используется Room. Фича
избранного реализует экран со списком избранных раздач с возможностью удаления по swipe-to-dismiss,
а также предоставляет API, благодаря которому другие модули приложения могут добавлять и
удалять раздачи из избранного, а также отслеживать `Flow<List<Favorite>>` для отображения в
собственном UI.
- Push - уведомления о новых раздачах в выбранных категориях. Реализовано через Firebase Cloud
Messaging. Сервер опрашивает rutracker.org с определенным интервалом, и при наличии обновлений,
рассылает уведомления подписавшимся клиентам. Навигация из уведомления происходит через deep-link.
- Реализована темная тема с возможностью независимой от системной ручной смены.
<details>
<summary>GIF</summary>
<img src="screenshots/dark_mode.gif">
</details>

В приложении так же использованы следующие технологии:
- Retrofit - используется для запроса Server-Driven UI - единственный сетевой вызов, не
завернутый в GraphQL
- Apollo GraphQL client
- Jetpack Navigation Component
- DataStore как key-value хранилище
- Coil
- Accompanist Navigation Animation для анимаций переходов между экранами
- Gradle Version Catalog и кастомные gradle-плагины для конфигурации модулей "в одном месте"
- splash-screen с использованием xml-темы
- okHttp - интерсептор для отлавливания ошибки отсутствия интернет-соединения
- junit 4 для unit - тестов
- ktlint
- minSdk = 21, поддержка альбомной ориентации
- Firebase Crashlytics

#### Инструкция по сборке
- добавить строковую переменную SERVER_URL с URL сервера (https или прописать
`usesCleartextTraffic = true` в манифесте) в файл local.properties
- добавить файл google-services.json, полученный при конфигурации firebase-проекта
