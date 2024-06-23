const host = "192.144.12.231";//localhost

document.addEventListener('DOMContentLoaded', function () {
    const indexingTimeElement = document.getElementById('indexingTime');
    const uuid = document.getElementById('uuid').textContent;
    getIndexingTime(uuid)
        .then(indexingTime => {
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
});

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