# 1. Клонируйте репозиторий:cmd
```bash
    git clone https://github.com/ваш-username/ваш-repo.git
    cd "путь, куда вы клонировали гит"
```

# 2. Получите свой токен VK

## Шаг 1: Создать приложение (Standalone / Мини-приложение)

1. Перейдите:  
   https://vk.com/editapp?act=create
2. Выберите **Сайт** (или любой доступный тип).
3. Заполните:
   - Название: `VkPostApp` (любое)
   - Платформы: `Web-приложение`
   - Базовый домен: `localhost`
   - Доверенный Redirect URL: `https://oauth.vk.com/blank.html`

## Шаг 2: Получить токен

1. Ниже будет `Ключи доступа`, скопируйте `Сервисный ключ доступа` он же Токен

## Шаг 3: Проверить токен

```bash
curl "https://api.vk.com/method/wall.get?owner_id=-2608975&count=10&access_token=ВАШ_ТОКЕН&v=5.199"
```


# 3. Установите переменную окружения:cmd
```bash для Windows
    set VK_ACCESS_TOKEN=ваш_токен
``` 
```bash для Linux
    export VK_ACCESS_TOKEN='ваш_токен'
``` 

## 3.2 Проверьте, что переменная установлена:cmd
```bash
    echo %VK_ACCESS_TOKEN%
``` 

# 4. Запустите проект:cmd
```bash
    mvn clean install
    mvn spring-boot:run
``` 

# 5. Откройте http://localhost:8081
