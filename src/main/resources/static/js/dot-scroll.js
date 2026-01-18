document.addEventListener('DOMContentLoaded', () => {
    const sections = document.querySelectorAll('.section');
    const dots = document.querySelectorAll('.dot');

    // Клик по точке → скролл
    dots.forEach(dot => {
        dot.addEventListener('click', () => {
            const targetId = dot.getAttribute('data-target');
            const target = document.querySelector(targetId);
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Отслеживание текущей секции
    const observer = new IntersectionObserver(
        entries => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // Убираем active у всех
                    dots.forEach(d => d.classList.remove('active'));

                    // Находим индекс текущей секции
                    const index = Array.from(sections).indexOf(entry.target);
                    if (index !== -1 && dots[index]) {
                        dots[index].classList.add('active');
                    }
                }
            });
        },
        {
            threshold: 0.5,           // 50% секции видно → активна
            rootMargin: '-10% 0px -40% 0px' // учитывает хедер/футер
        }
    );

    sections.forEach(section => observer.observe(section));
});