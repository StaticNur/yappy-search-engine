const host = "192.144.12.231";//localhost

document.addEventListener('DOMContentLoaded', function () {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const dataParam = urlParams.get('data');

    if (dataParam) {
        try {
            const data = JSON.parse(decodeURIComponent(dataParam));
            console.log(data);
            parseData(data);
        } catch (e) {
            console.error('Failed to parse data:', e);
        }
    }
});

function parseData(data) {
    console.log(data.uuid);

    var videoContainer = document.querySelector('.video-container-info-page video');

    videoContainer.setAttribute('src', data.url);

    const indexingTimeElement = document.getElementById('indexingTime');
    getIndexingTime(data.uuid).then(indexingTime => {
        console.log(indexingTime);
        if (indexingTime === 0) {
            indexingTimeElement.textContent = '0ms (Это видео импортирован из датасета)';
        } else {
            indexingTimeElement.textContent = `${indexingTime} ms`;
        }
    })
        .catch(error => {
        console.error('Ошибка при получении времени индексации:', error);
        indexingTimeElement.textContent = 'Ошибка при получении времени индексации';
    });

    document.getElementById('uuid').textContent = data.uuid || 'Нет данных';
    document.getElementById('url').textContent = data.url || 'Нет данных';
    document.getElementById('title').textContent = data.title || 'Нет данных';
    document.getElementById('descriptionUser').textContent = data.descriptionUser || 'Нет данных';
    document.getElementById('transcriptionAudio').textContent = data.transcriptionAudio || 'Нет данных';
    document.getElementById('languageAudio').textContent = data.languageAudio || 'Нет данных';
    document.getElementById('descriptionVisual').textContent = data.descriptionVisual || 'Нет данных';

    if (data.tags) {
        document.getElementById('tags').textContent = data.tags.split(',').map(tag => `${tag.trim()}`).join(' ');
    } else {
        document.getElementById('tags').textContent = 'Нет тегов';
    }

    document.getElementById('created').textContent = `Дата публикации: ${data.created}`;
    document.getElementById('popularity').textContent = data.popularity || 'Нет данных';
    document.getElementById('hash').textContent = data.hash || 'Нет данных';
    document.getElementById('embeddingAudio').textContent = data.embeddingAudio ? data.embeddingAudio.join(', ') : 'Нет данных';
    document.getElementById('embeddingVisual').textContent = data.embeddingVisual ? data.embeddingVisual.join(', ') : 'Нет данных';
    document.getElementById('embeddingUserDescription').textContent = data.embeddingUserDescription ? data.embeddingUserDescription.join(', ') : 'Нет данных';
}

async function getIndexingTime(uuid) {
    try {
        const response = await fetch(`http://${host}:8080/indexing-time?uuid=${uuid}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch indexing time');
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching indexing time:', error);
        throw error;
    }
}