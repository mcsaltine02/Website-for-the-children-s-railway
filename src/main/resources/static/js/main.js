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
const throttleDelay = 100;


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