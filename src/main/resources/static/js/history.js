const items = document.querySelectorAll('.timeline-item');
const desktopContents = document.querySelectorAll('.timeline-content');
const mobileContent = document.getElementById('mobile-content');

const mobileTexts = {
    'item-1940': `<p>Ростовская детская железная дорога начала свою историю в <strong>1940 году</strong>, когда 4 августа, в День железнодорожника, был дан старт строительству. Уже 9 ноября того же года состоялось торжественное открытие дороги.</p>
                      <p>Первый подвижной состав: паровоз <span class="highlight">ЮП-305</span>, мотовоз <span class="highlight">М-72</span> и пять мягких вагонов в составе «Малыш». Построено депо с мастерскими.</p>`,
    'item-1941': `<p>ДЖД закрыли, подвижной состав эвакуировали в Тбилиси. В 1945 году принято решение о восстановлении несмотря на разрушения.</p>`,
    'item-1947': `<p>1947 — малое кольцо (~2 км), 1948 — большое кольцо (3,15 км), общая длина 4,45 км. 1949 — новый паровоз <span class="highlight">КЧ4-101</span> («Шкода»).</p>`,
    'item-1950': `<p>Новые вагоны, современная сигнализация и связь. В <strong>1961 году</strong> дороге присвоено имя Юрия Гагарина.</p>`,
    'item-1970': `<p>Лауреат премии Ленинского комсомола. Из-за строительства Дворца культуры длина сократилась до 4,09 км.</p>`,
    'item-1980': `<p>Тепловозы, новые вагоны. 1986 — открыт четырёхэтажный учебный корпус на ст. Солнечная.</p>`,
    'item-1990': `<p>Часть инфраструктуры демонтировали, но дорога работала. В 2000-е отремонтирован паровоз <span class="highlight">Гр-185</span>, введены составы «Тихий Дон» и «Атаман Платов».</p>`,
    'item-today': `<p>Сегодня — это полноценный учебный комплекс. Юные железнодорожники изучают профессии, а дорога сохраняет традиции и привлекает тысячи посетителей ежегодно.</p>`
};

function activateItem(item) {
    const targetId = item.getAttribute('data-target');

    // Убираем класс active у ВСЕХ пунктов навигации
    items.forEach(i => i.classList.remove('active'));

    // Добавляем только выбранному
    item.classList.add('active');

    // Десктоп: переключаем контент
    desktopContents.forEach(c => c.classList.remove('active'));
    const desktopTarget = document.getElementById(targetId);
    if (desktopTarget) {
        desktopTarget.classList.add('active');
    }

    // Мобильная версия
    if (mobileTexts[targetId]) {
        mobileContent.classList.remove('active');

        setTimeout(() => {
            mobileContent.innerHTML = mobileTexts[targetId];

            if (mobileContent.parentNode) {
                mobileContent.parentNode.removeChild(mobileContent);
            }
            item.after(mobileContent);

            requestAnimationFrame(() => {
                mobileContent.classList.add('active');
            });
        }, 600);
    }
}

items.forEach(item => {
    item.addEventListener('click', () => activateItem(item));
});

// Инициализация
activateItem(document.querySelector('.timeline-item.active'));