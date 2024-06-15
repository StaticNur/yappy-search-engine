var videos = [];
let currentIndex = 0;

var mySavedVideos = [];
let mySavedVideosCurrentIndex = 0;

var isMyVideo = false;

const host = "192.144.12.231";//localhost

document.addEventListener('DOMContentLoaded', function () {
    const startTime = performance.now();
    try {
        fetch(`http://${host}:8080/recommendations`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => response.json())
            .then(data => {
            if (data) {
                isMyVideo = false;
                currentIndex = 0;
                videos = data.videos;
                updateVideo(currentIndex);
                updateResults(data.videos);
                showMessage(startTime, 'success', `Найдено более ${data.totalHits} видео. Популярность по частоте использования тега`);
            } else {
                clear();
                showMessage(startTime, 'info', 'Рекомендательные видео не найдены');
            }
        }).catch(error => {
            clear();
            showMessage(startTime, 'error', error);
        });
    } catch (error) {
        clear();
        showMessage(startTime, 'error', 'Произошла ошибка при получении популярных видео');
    }
});

/*Загрузка нового видео*/
function showSaveForm(id, surname, name, otchestvo, job, birthday) {
    document.getElementById('saveFormOverlay').style.display = 'block';
}

function saveNewVideo() {
    const url = document.getElementById('url').value;
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const tags = document.getElementById('my-tags').value;

    if (url) {
        const startTime = performance.now();
        fetch(`http://${host}:8080/index`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                url: url,
                title: title,
                description: description,
                tags: tags
            })
        })
            .then(response => {
            console.log("response.status"+response.status);
            if (response.status === 201) {
                return response.json();
            } else {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
        })
            .then(data => {
            isMyVideo = true;
            mySavedVideos.push(data);
            mySavedVideosCurrentIndex = mySavedVideos.length - 1;
            updateVideo(mySavedVideosCurrentIndex);
            updateSavedVideos(data, mySavedVideosCurrentIndex);
            showMessage(startTime, 'success', 'Загрузка видео успешно завершена.');
        })
            .catch(error => {
            showMessage(startTime, 'error', `Произошла ошибка при загрузке видео. ${error.message}`);
        });
        hideSaveForm();
        clearInputFieldWhenSave();
        document.getElementById('urlErrorInput').style.display = 'none';
    } else {
        document.getElementById('urlErrorInput').style.display = 'block';
    }
}
function clearInputFieldWhenSave(){
    document.getElementById('url').value = '';
    document.getElementById('title').value = '';
    document.getElementById('description').value = '';
    document.getElementById('my-tags').value = '';
}

function hideSaveForm() {
    // Скрыть всплывающее окно
    document.getElementById('saveFormOverlay').style.display = 'none';
}

/*Фильтр*/
function showFilterForm() {
    document.getElementById('filterFormOverlay').style.display = 'block';
}
function hideFilterForm() {
    document.getElementById('filterFormOverlay').style.display = 'none';
}

/*Поиск*/
document.getElementById('queryText').addEventListener('click', function() {
    const suggestionsList = document.getElementById('suggestions-list');
    suggestionsList.style.display = 'block';
})
document.getElementById('queryText').addEventListener('input', function() {
    const query = this.value;
    document.getElementById('suggestions-list').style.display = 'block';

    fetch(`http://${host}:8080/search/autocomplete?query=${query}`)
        .then(response => response.json())
        .then(data => {
        const suggestionsList = document.getElementById('suggestions-list');
        suggestionsList.innerHTML = '';
        data.forEach(suggestion => {
            const listItem = document.createElement('li');
            listItem.textContent = suggestion;
            listItem.addEventListener('click', function(event) {
                document.getElementById('queryText').value = suggestion;
                //suggestionsList.innerHTML = '';

                event.preventDefault();
                sendSearchRequest()
            });
            suggestionsList.appendChild(listItem);
        });
    });
});
document.addEventListener('click', function(event) {
    const inputField = document.getElementById('queryText');
    const suggestionsList = document.getElementById('suggestions-list');
    if (!inputField.contains(event.target) && !suggestionsList.contains(event.target)) {
        suggestionsList.style.display = 'none';
    }
});
document.getElementById('queryText').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendSearchRequest();
    }
});
document.getElementById('btnSearch').addEventListener('click', function (event) {
    event.preventDefault();
    sendSearchRequest();
});

function sendSearchRequest() {
    const queryText = document.getElementById('queryText').value;
    const suggestionsList = document.getElementById('suggestions-list');
    suggestionsList.style.display = 'none';

    console.log(`Запрос: ${queryText}`);  // Логирование запроса
    const startTime = performance.now();
    try {
        const typeSearch = document.getElementById('typeSearch').value;
        const dateFilter = document.getElementById('dateFilter').value;
        const sort = document.getElementById('sort').value;
        var sortBy = null;
        var order = null;
        switch (sort) {
            case 'new_video':
                sortBy = 'created';
                order = 'DESC';
                break;
            case 'old_video':
                sortBy = 'created';
                order = 'ASC';
                break;
            case 'high_rating':
                sortBy = 'popularity';
                order = 'DESC';
                break;
            case 'low_rating':
                sortBy = 'popularity';
                order = 'ASC';
                break;
        }
        let date = getDate(dateFilter);

        const searchRequestDto = {
            typeSearch: typeSearch,
            query: queryText,
            sortBy: sortBy,
            order: order,
            page: 0,
            size: 15
        };
        console.log('Запрос: ', JSON.stringify(searchRequestDto));
        console.log('Date: ', date);
        fetch(`http://${host}:8080/search?date=${date}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(searchRequestDto)
        }).then(response => response.json())
            .then(data => {
            if (data) {
                isMyVideo = false;
                currentIndex = 0;
                console.log(data.videos);
                console.log(data.totalHits);
                videos = data.videos;
                updateVideo(currentIndex);
                updateResults(data.videos);
                if(data.totalHits === 10000){
                    showMessage(startTime, 'success', `Более ${data.totalHits} видео соответствуют вашему запросу`);
                }else{
                    showMessage(startTime, 'success', `Всего ${data.totalHits} видео соответствуют вашему запросу`);
                }
            } else {
                clear();
                showMessage(startTime, 'info', 'Видео не найдены');
            }
        }).catch(error => {
            clear();
            showMessage(startTime, 'error', error.message || 'Произошла ошибка при выполнении запроса');
        });
    } catch (error) {
        clear();
        showMessage(startTime, 'error', 'Произошла ошибка при отправке формы');
    }
}
function getDate(dateFilter){
    let date;
    switch (dateFilter) {
        case 'all_time':
            date = '1971-01-01';
            break;
        case 'today':
            const today = new Date();
            date = formatDate(today);
            break;
        case 'this_week':
            const todayWeek = new Date();
            const firstDayOfWeek = new Date(todayWeek.setDate(todayWeek.getDate() - todayWeek.getDay()));
            date = formatDate(firstDayOfWeek);
            break;
        case 'this_month':
            const todayMonth = new Date();
            const firstDayOfMonth = new Date(todayMonth.getFullYear(), todayMonth.getMonth(), 1);
            date = formatDate(firstDayOfMonth);
            break;
        case 'this_year':
            const firstDayOfYear = new Date(new Date().getFullYear(), 0, 1);
            date = formatDate(firstDayOfYear);
            break;
    }
    return date;
}

function formatDate(date) {
    const year = date.getFullYear();
    let month = (date.getMonth() + 1).toString();
    let day = date.getDate().toString();

    if (month.length === 1) {
        month = '0' + month;
    }
    if (day.length === 1) {
        day = '0' + day;
    }
    return `${year}-${month}-${day}`;
}

function executionTime(startTime) {
    const endTime = performance.now();
    return  endTime - startTime;
}

/*Основной контент*/
document.getElementById('prevBtn').addEventListener('click', () => {
    previous();
});

document.getElementById('nextBtn').addEventListener('click', () => {
    next();
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'ArrowLeft') {
        previous();
    } else if (event.key === 'ArrowRight') {
        next();
    }
});
function next() {
    var index = 0;
    if(isMyVideo){
        index = mySavedVideosCurrentIndex;
        if (index < mySavedVideos.length - 1) {
            index++;
        }else{
            index = 0;
        }
        mySavedVideosCurrentIndex = index;
    }else{
        index = currentIndex;
        if (index < videos.length - 1) {
            index++;
        }else{
            index = 0;
        }
        currentIndex = index;
    }
    console.log("currentIndex: "+index)
    updateVideo(index);
    updateActiveCard()
}

function previous() {
    var index = 0;
    if(isMyVideo){
        index = mySavedVideosCurrentIndex;
        if (index > 0) {
            index--;
        }else{
            index = mySavedVideos.length - 1;
        }
        mySavedVideosCurrentIndex = index;
    }else{
        index = currentIndex;
        if (index > 0) {
            index--;
        }else{
            index = videos.length - 1;
        }
        currentIndex = index;
    }
    console.log("currentIndex: "+index)
    updateVideo(index);
    updateActiveCard()
}

function playVideo(videoSrc, popularity, videoTitle, videoDescription, videoTags, videoCreated) {
    // Находим видео элемент внутри контейнера
    var videoContainer = document.querySelector('.video-container video');

    videoContainer.setAttribute('src', videoSrc);

    var tagFrequencyPopularity = document.getElementById('tagFrequency');
    tagFrequencyPopularity.textContent = popularity;

    var videoContainerElement = document.querySelector('.container');// Скроллим к видео контейнеру плавно
    videoContainerElement.scrollIntoView({ behavior: 'smooth', block: 'start' });

    var infoElement = document.getElementById('info');
    infoElement.textContent = videoTitle +" " + videoDescription;

    var tagsElement = document.getElementById('tags');
    tagsElement.textContent = videoTags;

    var dateElement = document.getElementById('date');
    dateElement.textContent = "Дата публикации: "+videoCreated;
}

document.getElementById('info-link').addEventListener('click', function(event) {
    event.preventDefault();
    const url = "info-video.html";
    var encodedQuery;
    if(isMyVideo){
        encodedQuery = encodeURIComponent(JSON.stringify(mySavedVideos[mySavedVideosCurrentIndex]));
    }else{
        encodedQuery = encodeURIComponent(JSON.stringify(videos[currentIndex]));
    }
    console.log("Encoded string: ", encodedQuery);
    window.location.href = url + "?data=" + encodedQuery;
});

function updateVideo(index) {
    var video = [];
    if(isMyVideo){
        video = mySavedVideos[index];
    }else{
        video = videos[index];
    }
    playVideo(video.url, video.popularity, video.title, video.descriptionUser, video.tags, video.created);
}

function updateActiveCard() {
    const cards = document.querySelectorAll('.card');
    const mycards = document.querySelectorAll('.my-card');
    if(isMyVideo){
        mycards.forEach((card, index) => {
            if (index === mySavedVideosCurrentIndex) {
                card.classList.add('active');
            } else {
                card.classList.remove('active');
            }
        });
    }else{
        cards.forEach((card, index) => {
            if (index === currentIndex) {
                card.classList.add('active');
            } else {
                card.classList.remove('active');
            }
        });
    }
}

function updateResults(videos) {
    const resultsContainer = document.querySelector('.cards-container');
    resultsContainer.innerHTML = '';

    videos.forEach((video, index) => {
        const card = document.createElement('div');
        card.className = 'card';
        card.dataset.index = index;
        card.onclick = () => {
            isMyVideo = false;
            currentIndex = index;
            playVideo(video.url, video.popularity, video.title, video.descriptionUser, video.tags, video.created);
            updateActiveCard();
        };

        const urlElement = document.createElement('video');
        urlElement.innerHTML = `<source src="${video.url}" type="video/mp4">Your browser does not support the video tag.`;

        const titleElement = document.createElement('p');
        titleElement.textContent = video.title;
        titleElement.style.display = 'none';

        const descriptionElement = document.createElement('p');
        descriptionElement.textContent = video.descriptionUser;
        descriptionElement.style.display = 'none';

        const tagsElement = document.createElement('p');
        tagsElement.textContent = video.tags;
        tagsElement.style.display = 'none';

        const createdElement = document.createElement('p');
        createdElement.textContent = video.created;
        createdElement.style.display = 'none';

        card.appendChild(urlElement);
        card.appendChild(titleElement);
        card.appendChild(descriptionElement);
        card.appendChild(tagsElement);
        card.appendChild(createdElement);
        resultsContainer.appendChild(card);
    });
    updateActiveCard();
}

/* saved videos */
function updateSavedVideos(video, index) {
    const resultsContainer = document.querySelector('#cards-container');

    const card = document.createElement('div');
    card.className = 'my-card';
    card.dataset.index = index;
    card.onclick = () => {
        isMyVideo = true;
        mySavedVideosCurrentIndex = index;
        playVideo(video.url, video.popularity, video.title, video.descriptionUser, video.tags, video.created);
        updateActiveCard();
    };

    const urlElement = document.createElement('video');
    urlElement.innerHTML = `<source src="${video.url}" type="video/mp4">Your browser does not support the video tag.`;

    const titleElement = document.createElement('p');
    titleElement.textContent = video.title;
    titleElement.style.display = 'none';

    const descriptionElement = document.createElement('p');
    descriptionElement.textContent = video.descriptionUser;
    descriptionElement.style.display = 'none';

    const tagsElement = document.createElement('p');
    tagsElement.textContent = video.tags;
    tagsElement.style.display = 'none';

    const createdElement = document.createElement('p');
    createdElement.textContent = video.created;
    createdElement.style.display = 'none';

    card.appendChild(urlElement);
    card.appendChild(titleElement);
    card.appendChild(descriptionElement);
    card.appendChild(tagsElement);
    card.appendChild(createdElement);
    resultsContainer.appendChild(card);
    updateActiveCard();
}


function showMessage(startTime, type, message) {
    const processTime = document.getElementById("process-time");
    processTime.textContent = message+"; Время: "+executionTime(startTime).toFixed(2)+"ms";

    // Удаление всех классов стилей и добавление нового на основе типа сообщения
    processTime.classList.remove("text-info", "text-success", "text-danger");
    if (type === 'info') {
        processTime.classList.add("text-info");
    } else if (type === 'success') {
        processTime.classList.add("text-success");
    } else if (type === 'error') {
        processTime.classList.add("text-danger");
    }
}
function saveFilter() {
    const typeSearch = document.getElementById("typeSearch");
    const selectedOptionType = typeSearch.options[typeSearch.selectedIndex];
    const typeSearchInfo = document.getElementById("typeSearchInfo");
    typeSearchInfo.textContent = "Тип поиска: " + selectedOptionType.text;
    typeSearchInfo.classList.add("text-info");
    console.log("typeSearch.selectedIndex"+typeSearch.selectedIndex);

    const dateFilter = document.getElementById("dateFilter");
    const selectedOptionDate = dateFilter.options[dateFilter.selectedIndex];
    const dateFilterInfo = document.getElementById("dateFilterInfo");
    dateFilterInfo.textContent = "Фильтр по дате: " + selectedOptionDate.text;
    dateFilterInfo.classList.add("text-info");

    const sort = document.getElementById("sort");
    const selectedOptionSort = sort.options[sort.selectedIndex];
    const sortInfo = document.getElementById("sortInfo");
    sortInfo.textContent = "Сортировка: " + selectedOptionSort.text;
    sortInfo.classList.add("text-info");

    hideFilterForm();
}


function clear() {
    currentIndex = 0;
    videos = [];
    playVideo("Not found", "Not found", "Not found", "Not found", "Not found", "Not found");
    updateResults(videos);
}

document.getElementById("likeButton").addEventListener("click", function(event) {
    // Создаем элемент для подсказкиs
    const tooltip = document.createElement('div');
    tooltip.className = 'tooltip-visible';
    tooltip.innerText = this.title;

    // Добавляем подсказку к контейнеру с относительным позиционированием
    const container = this.parentElement;
    container.appendChild(tooltip);

    // Позиционируем подсказку около элемента
    const rect = this.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();
    const tooltipTop = rect.top - containerRect.top - tooltip.offsetHeight;
    const tooltipLeft = rect.left - containerRect.left;

    tooltip.style.top = `${tooltipTop}px`;
    tooltip.style.left = `${tooltipLeft}px`;

    // Удаляем подсказку через 2 секунды
    setTimeout(() => {
        tooltip.remove();
    }, 2000);
});