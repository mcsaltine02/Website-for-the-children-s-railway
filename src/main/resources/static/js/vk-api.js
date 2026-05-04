const CACHE_KEY = 'vk_posts_cache';
const CACHE_TTL = 60 * 60 * 1000; // 10 минут

// === КЭШИРОВАНИЕ ===
// function getCachedPosts() {
//     const cached = localStorage.getItem(CACHE_KEY);
//     if (!cached) return null;
//     const {data, timestamp} = JSON.parse(cached);
//     if (Date.now() - timestamp > CACHE_TTL) {
//         localStorage.removeItem(CACHE_KEY);
//         return null;
//     }
//     return data;
// }

function setCachedPosts(data) {
    const cacheObj = {data, timestamp: Date.now()};
    localStorage.setItem(CACHE_KEY, JSON.stringify(cacheObj));
}

// === ОСНОВНАЯ ЗАГРУЗКА ===
async function fetchPosts(forceRefresh = false) {
    const loading = document.getElementById('loading');
    const errorDiv = document.getElementById('error');
    const postsDiv = document.getElementById('posts');

    if (forceRefresh) {
        localStorage.removeItem(CACHE_KEY);
    }

    // const cached = getCachedPosts();
    // if (cached && !forceRefresh) {
    //     loading.style.display = 'none';
    //     displayPosts(cached.response.items);
    //     return;
    // }

    try {
        loading.style.display = 'block';
        errorDiv.style.display = 'none';
        postsDiv.innerHTML = '';

        const response = await fetch('/vk-proxy');
        const data = await response.json();

        if (data.error) throw new Error(data.error.error_msg || 'VK API error');

        setCachedPosts(data);
        displayPosts(data.response.items);
    } catch (error) {
        loading.style.display = 'none';
        errorDiv.textContent = `Ошибка: ${error.message}`;
        errorDiv.style.display = 'block';
        console.error(error);
    }
}

// === ОТОБРАЖЕНИЕ ПОСТОВ ===
function displayPosts(posts) {
    const loading = document.getElementById('loading');
    const container = document.getElementById('posts');
    loading.style.display = 'none';

    if (!posts || posts.length === 0) {
        container.innerHTML = '<p>Нет постов.</p>';
        return;
    }

    const postsHtml = posts.map(post => {
        let hasVideo = false;
        let hasPhoto = false;

        const attachments = post.attachments || [];
        const copyHistory = post.copy_history || [];
        let repostHtml = '';
        let repostText = '';
        let repostAttachments = [];

        if (copyHistory.length > 0) {
            const orig = copyHistory[0];
            repostText = orig.text || '';
            repostAttachments = orig.attachments || [];
        }

        const allAtt = [...attachments, ...repostAttachments];
        const fullPhotoUrls = []; // полные размеры для модалки

        const photoItems = allAtt
            .filter(att => att.type === 'photo')
            .map(att => {
                hasPhoto = true;
                const photo = att.photo;
                const sizes = photo.sizes;

                // Полный размер для модалки
                const fullSize = sizes.sort((a, b) => b.width - a.width)[0];
                fullPhotoUrls.push(fullSize.url);

                // Превью для поста (меньший размер)
                const previewSize = sizes.find(s => s.type === 'z') ||
                    sizes.find(s => s.type === 'y') ||
                    sizes.find(s => s.type === 'x') ||
                    sizes.sort((a, b) => b.width - a.width)[1] || fullSize;

                return previewSize.url;
            });

        // HTML для фото (только видимые 3)
        const photosHtml = photoItems.map((url, i) => {
            const isLastVisible = i === 2 && photoItems.length > 3;
            const hiddenCount = photoItems.length > 3 ? photoItems.length - 3 : 0;

            if (isLastVisible) {
                // Третье фото с оверлеем вместо blur
                return `
                    <div class="post-imag-item overlay-plus" style="position:relative; overflow:hidden;">
                        <img src="${url}" loading="lazy" decoding="async" style="width:100%; object-fit:cover;">
                        <div class="vk-plus-overlay"></div>
                        <div class="vk-plus">+${hiddenCount}</div>
                    </div>`;
            }

            if (i < 3) {
                return `<img src="${url}" class="post-imag-item" loading="lazy" decoding="async">`;
            }

            return ''; // скрытые фото не рендерим вообще
        }).join('');

        const attachmentsHtml = allAtt.map(att => {
            if (att.type === 'photo') return '';
            if (att.type === 'video') {
                hasVideo = true;
                const video = att.video;
                let mp4Url = '';
                if (video.files) {
                    const qualities = ['mp4_1080', 'mp4_720', 'mp4_480', 'mp4_360', 'mp4_240'];
                    for (const q of qualities) {
                        if (video.files[q]) {
                            mp4Url = video.files[q];
                            break;
                        }
                    }
                }

                const preview = video.image?.sort((a, b) => b.width - a.width)[0]?.url || '';
                if (mp4Url) {
                    return `<video class="post-video" controls preload="metadata" poster="${preview}" style="width:100%; max-height:500px; border-radius:8px; background:#000; margin:10px 0;">
                        <source src="${mp4Url}" type="video/mp4">
                    </video>`;
                }
                const link = `https://vk.com/video${video.owner_id}_${video.id}`;
                return `<div style="margin:10px 0;">
                    ${preview ? `<img src="${preview}" class="post-image" style="max-height:400px; border-radius:8px;">` : ''}
                    <br><a href="${link}" target="_blank" class="post-video-link">Смотреть видео во ВКонтакте</a>
                </div>`;
            }
            return '';
        }).join('');

        let fullText = post.text || '';
        if (!fullText && repostText) fullText = repostText;
        if (!fullText && hasVideo) fullText = 'Видео';
        if (!fullText && hasPhoto && repostText) fullText = 'Репост с фото';

        const textHtml = fullText ? `<div class="post-text"><p>${fullText.replace(/\n/g, '<br>')}</p></div>` : '';

        if (copyHistory.length > 0) {
            const repostPhotosBlock = fullPhotoUrls.length > 0 ? `
                <div class="post-imag" data-photos='${JSON.stringify(fullPhotoUrls)}'>
                    ${photosHtml}
                </div>
            ` : '';

            repostHtml = `<div class="repost"><p><strong>Репост:</strong><br>${textHtml}${repostPhotosBlock || attachmentsHtml}</p></div>`;
        }

        const photosBlock = fullPhotoUrls.length > 0 ? `
            <div class="post-imag" data-photos='${JSON.stringify(fullPhotoUrls)}'>
                ${photosHtml}
            </div>
        ` : '';

        return `
          
            <div class="post">
                ${repostHtml || textHtml}
                ${copyHistory.length === 0 ? (photosBlock || attachmentsHtml) : ''}
                <div class="post-meta">
                <p>Дата: ${new Date(post.date * 1000).toLocaleString('ru-RU')}</p>
                </div>
            </div>`;
    });

    container.innerHTML = postsHtml.join('');

    // Предзагрузка полных фото
    document.querySelectorAll('.post-imag').forEach(block => {
        const photoUrls = JSON.parse(block.dataset.photos || '[]');
        photoUrls.forEach(url => {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.as = 'image';
            link.href = url;
            document.head.appendChild(link);
        });
    });

    // Ленивая инициализация кликов через IntersectionObserver
    initPhotoBlocksLazy();
}

// Ленивая инициализация галерей
function initPhotoBlocksLazy() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const block = entry.target;
                const photoUrls = JSON.parse(block.dataset.photos || '[]');
                const items = block.querySelectorAll('.post-imag-item');

                items.forEach((item, i) => {
                    if (i >= 3) return;
                    item.style.cursor = 'pointer';
                    item.addEventListener('click', () => openVkModal(photoUrls, i));
                });

                observer.unobserve(block);
            }
        });
    }, {rootMargin: '200px'}); // загружаем заранее

    document.querySelectorAll('.post-imag').forEach(block => observer.observe(block));
}

// === МОДАЛКА ===
let currentVkIndex = 0;
let currentVkPhotos = [];

function preloadNext() {
    if (currentVkPhotos.length <= 1) return;
    const nextIndex = (currentVkIndex + 1) % currentVkPhotos.length;
    const img = new Image();
    img.src = currentVkPhotos[nextIndex];
}

function openVkModal(photos, startIndex) {
    currentVkPhotos = photos;
    currentVkIndex = startIndex;

    const modal = document.getElementById('vk-modal') || createVkModal();
    const img = modal.querySelector('img');
    const loadingEl = modal.querySelector('.vk-modal-loading');
    const prevBtn = modal.querySelector('.vk-modal-prev');
    const nextBtn = modal.querySelector('.vk-modal-next');

    loadingEl.style.display = 'block';
    img.style.display = 'none';

    img.onload = () => {
        loadingEl.style.display = 'none';
        img.style.display = 'block';
    };

    img.src = photos[startIndex];
    preloadNext();

    if (photos.length <= 1) {
        prevBtn.style.display = 'none';
        nextBtn.style.display = 'none';
    } else {
        prevBtn.style.display = 'flex';
        nextBtn.style.display = 'flex';
    }

    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function createVkModal() {
    const modal = document.createElement('div');
    modal.id = 'vk-modal';
    modal.className = 'vk-modal';
    modal.innerHTML = `
        <div class="vk-modal-close">×</div>
        <div class="vk-modal-nav vk-modal-prev">🡄</div>
        <div class="vk-modal-nav vk-modal-next">🡆</div>
        <div class="vk-modal-loading">Загрузка...</div>
        <img src="" alt="">
    `;
    document.body.appendChild(modal);

    const closeBtn = modal.querySelector('.vk-modal-close');
    const prevBtn = modal.querySelector('.vk-modal-prev');
    const nextBtn = modal.querySelector('.vk-modal-next');
    const img = modal.querySelector('img');
    const loadingEl = modal.querySelector('.vk-modal-loading');

    closeBtn.onclick = () => {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    };

    prevBtn.onclick = () => {
        currentVkIndex = (currentVkIndex - 1 + currentVkPhotos.length) % currentVkPhotos.length;
        loadingEl.style.display = 'block';
        img.style.display = 'none';
        img.onload = () => {
            loadingEl.style.display = 'none';
            img.style.display = 'block';
        };
        img.src = currentVkPhotos[currentVkIndex];
        preloadNext();
    };

    nextBtn.onclick = () => {
        currentVkIndex = (currentVkIndex + 1) % currentVkPhotos.length;
        loadingEl.style.display = 'block';
        img.style.display = 'none';
        img.onload = () => {
            loadingEl.style.display = 'none';
            img.style.display = 'block';
        };
        img.src = currentVkPhotos[currentVkIndex];
        preloadNext();
    };

    modal.onclick = (e) => {
        if (e.target === modal) closeBtn.click();
    };

    document.addEventListener('keydown', e => {
        if (!modal.classList.contains('active')) return;
        if (e.key === 'Escape') closeBtn.click();
        if (e.key === 'ArrowLeft') prevBtn.click();
        if (e.key === 'ArrowRight') nextBtn.click();
    });

    return modal;
}

// Кнопка обновления
document.addEventListener('DOMContentLoaded', () => {
    const refreshBtn = document.getElementById('refresh-btn');
    const svgIcon = refreshBtn.innerHTML;

    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            refreshBtn.innerHTML = '<span>Обновление...</span>';

            fetchPosts(true).finally(() => {
                refreshBtn.innerHTML = svgIcon;
            });
        });
    }
});


fetchPosts();
