document.addEventListener("DOMContentLoaded", function(){
    document.getElementById("burger").addEventListener("click", function(){
        document.querySelector(".header").classList.toggle("open")
    })
})

// Закрыть меню при нажатии на Еѕс 
window.addEventListener('keydown', (e) => {
    if (e.key == "Escape") {
        // Действие при клике
        document.querySelector(".header").classList.remove("open")
    }
});
//Закрыть меню при клике вне его
document.getElementById("menu").addEventListener('click', event =>{
    event._isClickWithInMenu = true;
});

document.getElementById("burger").addEventListener('click', event => {
    event._isClickWithInMenu = true;
});

document.body.addEventListener('click', event => {
    if (event._isClickWithInMenu) return;
    // Действие при клике
    document.querySelector(".header").classList.remove("open")
});

let lastScrollTop = 0;
const header = document.querySelector('.header');
const link_become_railway = document.querySelector('.link_becom_railway');
let isThrottled = false;
const throttleDelay = 10;


function onScroll() {
    const currentScroll = window.pageYOffset || document.documentElement.scrollTop;
    if (currentScroll > lastScrollTop) {
        // Скролл вниз — скрываем header
        header.style.transform = 'translateY(-100%)';
        if(link_become_railway){
            link_become_railway.style.transform = 'translateY(-150%)';
        }
    } else {
        // Скролл вверх — показываем header
        header.style.transform = 'translateY(0)';
        if(link_become_railway){
            link_become_railway.style.transform = 'translateY(0)';
        }

    }
    lastScrollTop = Math.max(0, currentScroll);
}

window.addEventListener('scroll', () => {
    if (!isThrottled) {
        onScroll();
        isThrottled = true;
        setTimeout(() => {
            isThrottled = false;
        }, throttleDelay);
    }
});



// Решение проблемы с двойным киком по ссылке
document.addEventListener('DOMContentLoaded', () => {
    const curtain = document.querySelector('.curtain');
    if (!curtain) return;

    let timeoutId = null;

    const dropperItem = document.querySelector('.menu_item.dropper');

    dropperItem.addEventListener('mouseenter', () => {
        // Сбрасываем предыдущий таймер
        if (timeoutId) clearTimeout(timeoutId);

        const links = curtain.querySelectorAll('a');
        links.forEach(link => {
            link.classList.remove('unlocked');
        });

        timeoutId = setTimeout(() => {
            links.forEach(link => {
                link.classList.add('unlocked');
            });
        }, 50);
    });

    dropperItem.addEventListener('mouseleave', () => {
        if (timeoutId) clearTimeout(timeoutId);

        const links = curtain.querySelectorAll('a');
        links.forEach(link => link.classList.add('unlocked'));
    });
});


//dot-scroll

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

