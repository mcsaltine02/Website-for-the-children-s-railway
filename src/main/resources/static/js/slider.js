

document.addEventListener("DOMContentLoaded", () => {
    let images = [];
    let currentIndex = 0;
    let isAnimating = false;

    const wrapper = document.getElementById('slides-wrapper');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    const info = document.getElementById('slider-info');
    const counter = document.getElementById('counter');


    const wait = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    async function loadImages() {
        try {
            const response = await fetch('/api/images');
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            images = await response.json();

            if (images.length === 0) {
                info.textContent = "Фото не найдены";
                counter.textContent = "0 / 0";
                return;
            }

            renderSlides();
            showSlide(0);
            updateInfo();
        } catch (err) {
            console.error("Ошибка загрузки изображений:", err);
            info.textContent = "Ошибка загрузки";
            counter.textContent = "0 / 0";
        }
    }

    function renderSlides() {
        wrapper.innerHTML = '';
        images.forEach((imgData) => {
            const slide = document.createElement('div');
            slide.className = 'slide';
            slide.innerHTML = `<img src="${imgData.path}?v=${Date.now()}" alt="" loading="lazy">`;
            wrapper.appendChild(slide);
        });
    }

    async function showSlide(nextIndex) {
        if (isAnimating || images.length === 0) return;
        isAnimating = true;

        const slides = wrapper.querySelectorAll('.slide');
        const current = slides[currentIndex];
        const next = slides[nextIndex];

        slides.forEach(s => s.classList.remove('active', 'prev', 'next'));

        const goingForward = (nextIndex > currentIndex) || (nextIndex === 0 && currentIndex === images.length - 1);

        if (goingForward) {
            current.classList.add('prev');
            next.classList.add('next', 'active');
        } else {
            current.classList.add('next');
            next.classList.add('prev', 'active');
        }

        //задержка
        await wait(0);

        slides.forEach(s => s.classList.remove('prev', 'next'));
        currentIndex = nextIndex;
        isAnimating = false;
        updateInfo();
    }

    function showNext() { if (!isAnimating) showSlide((currentIndex + 1) % images.length); }
    function showPrev() { if (!isAnimating) showSlide((currentIndex - 1 + images.length) % images.length); }

    function updateInfo() {
        const img = images[currentIndex];
        info.textContent = `${img.folder}: ${img.filename}`;
        counter.textContent = `${currentIndex + 1} / ${images.length}`;
    }

    prevBtn.onclick = showPrev;
    nextBtn.onclick = showNext;

    document.addEventListener('keydown', e => {
        if (e.key === 'ArrowLeft') showPrev();
        if (e.key === 'ArrowRight' || e.key === ' ') showNext();
    });

    loadImages();
});