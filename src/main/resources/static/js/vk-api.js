async function fetchPosts() {
    const loading = document.getElementById('loading');
    const errorDiv = document.getElementById('error');
    const postsDiv = document.getElementById('posts');

    try {
        loading.style.display = 'block';
        errorDiv.style.display = 'none';
        postsDiv.innerHTML = '';

        const response = await fetch('/vk-proxy');
        const data = await response.json();

        if (data.error) throw new Error(data.error.error_msg || 'VK API error');

        displayPosts(data.response.items);
    } catch (error) {
        loading.style.display = 'none';
        errorDiv.textContent = `Ошибка: ${error.message}`;
        errorDiv.style.display = 'block';
        console.error(error);
    }
}

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
        const photoUrls = [];

        const photoItems = allAtt
            .filter(att => att.type === 'photo')
            .map(att => {
                hasPhoto = true;
                const photo = att.photo;
                const size = photo.sizes.sort((a, b) => b.width - a.width)[0];
                photoUrls.push(size.url);
                return size.url;
            });

        // ОГРАНИЧИВАЕМ ДО 3 ФОТО
        const visiblePhotos = photoItems.slice(0, 3);
        const hiddenCount = photoItems.length - 3;

        const photosHtml = visiblePhotos.map((url, i) => {
            const isLast = i === visiblePhotos.length - 1;
            let html = `<img src="${url}" class="post-imag-item" loading="lazy">`;
            if (isLast && hiddenCount > 0) {
                html = `<div class="post-imag-item blurred" style="position:relative;">
                    <img src="${url}" loading="lazy" style="filter:blur(4px); width:100%; height:100%; object-fit:cover;">
                    <div class="vk-plus">+${hiddenCount}</div>
                </div>`;
            }
            return html;
        }).join('');

        const attachmentsHtml = allAtt.map(att => {
            if (att.type === 'photo') return ''; // фото обрабатываем отдельно
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
            const repostPhotosBlock = photoUrls.length > 0 ? `
        <div class="post-imag" data-photos='${JSON.stringify(photoUrls)}'>
            ${photosHtml}
        </div>
    ` : '';

            repostHtml = `<div class="repost"><p><strong>Репост:</strong><br>${textHtml}${repostPhotosBlock || attachmentsHtml}</p></div>`;
        }

        const photosBlock = photoUrls.length > 0 ? `
            <div class="post-imag" data-photos='${JSON.stringify(photoUrls)}'>
                ${photosHtml}
            </div>
        ` : '';

        return `
            <div class="post-meta">
                <p>Дата: ${new Date(post.date * 1000).toLocaleString('ru-RU')}</p>
            </div>
            <div class="post">
                ${repostHtml || textHtml}
                ${copyHistory.length === 0 ? (photosBlock || attachmentsHtml) : ''}
            </div>`;
    });

    container.innerHTML = postsHtml.join('');
    initVkPhotoBlocks();
}

function initVkPhotoBlocks() {
    const blocks = document.querySelectorAll('.post-imag');
    blocks.forEach(block => {
        const images = block.querySelectorAll('.post-imag-item');
        const photoUrls = JSON.parse(block.dataset.photos || '[]');

        if (images.length === 0) return;


        if (images.length > 4) {
            const lastImg = images[images.length - 1];
            const remaining = images.length - 4;
            lastImg.style.position = 'relative';
            lastImg.style.filter = 'blur(4px)';
            lastImg.insertAdjacentHTML('beforeend', `<div class="vk-plus">+${remaining}</div>`);
        }

        images.forEach((img, i) => {
            if (i >= 4 && images.length > 4) return; // не кликабельно +?
            img.style.cursor = 'pointer';
            img.addEventListener('click', () => openVkModal(photoUrls, i));
        });
    });
}

// === мОДАЛКА  ===
let currentVkIndex = 0;
let currentVkPhotos = [];

function openVkModal(photos, startIndex) {
    currentVkPhotos = photos;
    currentVkIndex = startIndex;


    const modal = document.getElementById('vk-modal') || createVkModal();
    const img = modal.querySelector('img');
    const prevBtn = modal.querySelector('.vk-modal-prev');
    const nextBtn = modal.querySelector('.vk-modal-next');
    img.src = photos[startIndex];

    if (photos.length <= 1) {
        prevBtn.style.display = 'none';
        nextBtn.style.display = 'none';
    } else{
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
        <div class="vk-modal-nav vk-modal-prev" style="user-select: none;">🡄</div>
        <div class="vk-modal-nav vk-modal-next" style="user-select: none;">🡆</div>
        <img src="" alt="">
    `;
    document.body.appendChild(modal);

    const closeBtn = modal.querySelector('.vk-modal-close');
    const prevBtn = modal.querySelector('.vk-modal-prev');
    const nextBtn = modal.querySelector('.vk-modal-next');
    const img = modal.querySelector('img');

    closeBtn.onclick = () => {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    };
    prevBtn.onclick = () => {
        currentVkIndex = (currentVkIndex - 1 + currentVkPhotos.length) % currentVkPhotos.length;
        img.src = currentVkPhotos[currentVkIndex];
    };
    nextBtn.onclick = () => {
        currentVkIndex = (currentVkIndex + 1) % currentVkPhotos.length;
        img.src = currentVkPhotos[currentVkIndex];
    };
    modal.onclick = (e) => { if (e.target === modal) closeBtn.click(); };

    document.addEventListener('keydown', e => {
        if (!modal.classList.contains('active')) return;
        if (e.key === 'Escape') closeBtn.click();
        if (e.key === 'ArrowLeft') prevBtn.click();
        if (e.key === 'ArrowRight') nextBtn.click();
    });

    return modal;
}

fetchPosts();