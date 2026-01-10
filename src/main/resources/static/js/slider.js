document.addEventListener("DOMContentLoaded", () => {
    let images = [];
    let currentIndex = 0;
    let isAnimating = false;

    // Теперь два wrapper'а: один для фона, один — для управления
    const bgWrapper = document.getElementById('slides-wrapper-bg');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');

    const wait = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    async function loadImages() {
        try {
            const response = await fetch('/api/images');
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            images = await response.json();

            if (images.length === 0) {
                console.warn("Нет изображений");
                return;
            }

            renderBackgroundSlides();
            showSlide(0);
        } catch (err) {
            console.error("Ошибка загрузки изображений:", err);
        }
    }

    // Создаём слайды только для фона
    function renderBackgroundSlides() {
        bgWrapper.innerHTML = '';
        images.forEach((imgData) => {
            const slide = document.createElement('div');
            slide.className = 'slide-bg';
            slide.style.backgroundImage = `url("${imgData.path}?v=${Date.now()}")`;
            bgWrapper.appendChild(slide);
        });
    }

    function showSlide(nextIndex) {
        if (isAnimating || images.length === 0) return;
        isAnimating = true;

        const slides = bgWrapper.querySelectorAll('.slide-bg');

        // Сразу ставим новое фото поверх, но с opacity: 0
        slides[nextIndex].style.opacity = '0';
        slides[nextIndex].classList.add('active');

        // Через кадр запускаем fade-in
        requestAnimationFrame(() => {
            slides[nextIndex].style.opacity = '1';
        });

        // Старая плавно исчезает
        slides[currentIndex].style.opacity = '0';

        // Через 1.2 сек разрешаем следующий клик
        setTimeout(() => {
            slides[currentIndex].classList.remove('active');
            currentIndex = nextIndex;
            isAnimating = false;
        }, 1200);
    }

    function showNext() {
        if (!isAnimating) {
            const next = (currentIndex + 1) % images.length;
            showSlide(next);
        }
    }

    function showPrev() {
        if (!isAnimating) {
            const prev = (currentIndex - 1 + images.length) % images.length;
            showSlide(prev);
        }
    }

    // Управление
    prevBtn.onclick = showPrev;
    nextBtn.onclick = showNext;

    document.addEventListener('keydown', e => {
        if (e.key === 'ArrowLeft') showPrev();
        if (e.key === 'ArrowRight' || e.key === ' ') showNext();
    });

    // Автопрокрутка (по желанию)
    // setInterval(showNext, 60000);

    loadImages();


});