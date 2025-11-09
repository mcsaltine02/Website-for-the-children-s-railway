async function fetchPosts() {
    const loading = document.getElementById('loading');
    const errorDiv = document.getElementById('error');
    const postsDiv = document.getElementById('posts');

    try {
        loading.style.display = 'block';
        errorDiv.style.display = 'none';

        const wallUrl = 'http://localhost:8081/vk-proxy';
        const response = await fetch(wallUrl);
        const data = await response.json();
        console.log('VK API Response:', data);
        if (data.error) {
            throw new Error(data.error);
        }
        await displayPosts(data.response.items);
    } catch (error) {
        loading.style.display = 'none';
        errorDiv.textContent = `Ошибка загрузки: ${error.message}. Проверьте:
            1. Действителен ли токен в application.properties`;
        errorDiv.style.display = 'block';
        console.error('Подробности:', error);
    }
}

async function displayPosts(posts) {
    const loading = document.getElementById('loading');
    const container = document.getElementById('posts');
    loading.style.display = 'none';

    if (posts.length === 0) {
        container.innerHTML = '<p>Нет постов в сообществе или доступ ограничен.</p>';
        return;
    }

    const postsHtml = await Promise.all(posts.map(async post => {
        console.log('Post attachments:', post.attachments);
        let hasVideo = false;
        const attachmentsHtml = await Promise.all((post.attachments || []).map(async att => {
            console.log('Attachment:', att);
            if (att.type === 'photo') {
                const size = att.photo.sizes[att.photo.sizes.length - 1];
                return `<img src="${size.url}" alt="Фото" class="post-image">`;
            } else if (att.type === 'video') {
                hasVideo = true;
                const videoUrl = `https://vk.com/video${att.video.owner_id}_${att.video.id}`;
                return `<a href="${videoUrl}" class="post-video-link" target="_blank">Ссылка на видео</a>`;
            }
            return '';
        }));

        // Если текста нет и нет видео, не добавляем post-text
        const textHtml = post.text && post.text.trim() ?
            `<div class="post-text">${post.text}</div>` :
            (hasVideo ? `<div class="post-text">Ссылка на видео</div>` : '');

        return `
        <div class="post">
          <div class="post-meta">
            Дата: ${new Date(post.date * 1000).toLocaleString('ru-RU')} |
             Лайки: ${post.likes?.count || 0} |
             Комментарии: ${post.comments?.count || 0} |
             Репосты: ${post.reposts?.count || 0}
          </div>
          ${textHtml}
          ${attachmentsHtml.join('')}
        </div>
        `;
    }));
    container.innerHTML = postsHtml.join('');
}

fetchPosts();
