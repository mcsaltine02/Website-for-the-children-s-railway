1. Клонируйте репозиторий:cmd

    git clone https://github.com/ваш-username/ваш-repo.git
    cd ваш-repo


2. Получите свой токен VK

    Перейдите на https://vk.com/dev → «Мои приложения» → «Создать приложение» (или используйте существующее).
    Тип: Игра или Мини-приложение.
    Настройки:Платформа: Веб.
    Доверенный Redirect URL: https://oauth.vk.com/blank.html.

    Откройте:

    https://oauth.vk.com/authorize?client_id=YOUR_CLIENT_ID&scope=wall&redirect_uri=https://oauth.vk.com/blank.html&response_type=token&v=5.199

    Замените YOUR_CLIENT_ID на ID приложения.
    Нажмите «Разрешить» и скопируйте access_token из URL.


3. Установите переменную окружения:cmd

    set VK_ACCESS_TOKEN=ваш_токен


4. Запустите проект:cmd

    mvn spring-boot:run
