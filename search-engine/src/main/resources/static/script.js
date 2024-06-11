var videos = [];
let currentIndex = 0;
const host = "192.144.12.231";//localhost

/*Загрузка нового видео*/
function showSaveForm(id, surname, name, otchestvo, job, birthday) {
    document.getElementById('saveFormOverlay').style.display = 'block';
}
function hideSaveForm() {
    // Скрыть всплывающее окно
    document.getElementById('saveFormOverlay').style.display = 'none';
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
            showMessage(startTime, 'success', 'Загрузка видео успешно завершена.');
        })
            .catch(error => {
            showMessage(startTime, 'error', `Произошла ошибка при загрузке видео. ${error.message}`);
        });

        hideSaveForm();
        document.getElementById('urlErrorInput').style.display = 'none';
    } else {
        document.getElementById('urlErrorInput').style.display = 'block';
    }
}


/*Фильтр*/
function showEditForm(id, surname, name, otchestvo, job, birthday) {
    document.getElementById('editFormOverlay').style.display = 'block';
}
function hideEditForm() {
    // Скрыть всплывающее окно
    document.getElementById('editFormOverlay').style.display = 'none';
}
document.getElementById("dateFilter").addEventListener("change", function() {
    var selectedValue = this.value;
    alert("Вы выбрали: " + selectedValue);
});


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
    const encodedQuery = encodeURIComponent(queryText);
    console.log(`ЗапрencodedQueryс: ${encodedQuery}`);
    const startTime = performance.now();
    try {
        fetch(`http://${host}:8080/search/text/lexicographic?query=${encodedQuery}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => response.json())
            .then(data => {
            if (data && data.length) {
                currentIndex = 0;
                videos = data;
                updateVideo(currentIndex);
                updateResults(data);
                showMessage(startTime, 'success', 'Запрос выполнен успешно');
            } else {
                clear();
                showMessage(startTime, 'info', 'Видео не найдены.');
            }
        }).catch(error => {
            clear();
            showMessage(startTime, 'error', error);
        });
    } catch (error) {
        clear();
        showMessage(startTime, 'error', 'Произошла ошибка при отправке формы.');
    }
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

function playVideo(videoSrc, videoTitle, videoDescription, videoTags, videoCreated) {
    // Находим видео элемент внутри контейнера
    var videoContainer = document.querySelector('.video-container video');

    videoContainer.setAttribute('src', videoSrc);
    videoContainer.play();

    var videoContainerElement = document.querySelector('.container');// Скроллим к видео контейнеру плавно
    videoContainerElement.scrollIntoView({ behavior: 'smooth', block: 'start' });

    var infoElement = document.getElementById('info');
    infoElement.textContent = videoTitle + videoDescription;

    var tagsElement = document.getElementById('tags');
    tagsElement.textContent = videoTags;

    var dateElement = document.getElementById('date');
    dateElement.textContent = "Дата публикации: "+videoCreated;
}

function updateVideo(index) {
    const video = videos[index];
    playVideo(video.url, video.title, video.descriptionUser, video.tags, video.created);
}

function next() {
    if (currentIndex < videos.length - 1) {
        currentIndex++;
    }else{
        currentIndex = 0;
    }
    console.log("currentIndex: "+currentIndex)
    updateVideo(currentIndex);
    updateActiveCard()
}

function previous() {
    if (currentIndex > 0) {
        currentIndex--;
    }else{
        currentIndex = videos.length - 1;
    }
    console.log("currentIndex: "+currentIndex)
    updateVideo(currentIndex);
    updateActiveCard()
}

function updateActiveCard() {
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        if (index === currentIndex) {
            console.log("card.classList.add('active');: ")
            card.classList.add('active');
        } else {
            console.log("card.classList.remove('active');: ")
            card.classList.remove('active');
        }
    });
}

function executionTime(startTime) {
    const endTime = performance.now();
    return  endTime - startTime;
}

function updateResults(videos) {
    const resultsContainer = document.querySelector('.cards-container');
    resultsContainer.innerHTML = '';

    videos.forEach((video, index) => {
        const card = document.createElement('div');
        card.className = 'card';
        card.dataset.index = index;
        card.onclick = () => {
            currentIndex = index;
            playVideo(video.url, video.title, video.descriptionUser, video.tags, video.created);
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

function showMessage(startTime, type, message) {
    const messageElement = document.getElementById("process-time");
    messageElement.textContent = message+"; Время: "+executionTime(startTime).toFixed(2)+"ms";

    // Удаление всех классов стилей и добавление нового на основе типа сообщения
    messageElement.classList.remove("text-info", "text-success", "text-danger");
    if (type === 'info') {
        messageElement.classList.add("text-info");
    } else if (type === 'success') {
        messageElement.classList.add("text-success");
    } else if (type === 'error') {
        messageElement.classList.add("text-danger");
    }
}
function clear() {
    currentIndex = 0;
    videos = [];
    playVideo("Not found", "Not found", "Not found", "Not found", "Not found");
    updateResults(videos);
}

document.getElementById("commentButton").addEventListener("click", function() {
    alert("Эта функция пока не реализована.");
});
document.getElementById("likeButton").addEventListener("click", function() {
    alert("Эта функция находится в стадии разработки.");
});
document.getElementById("shareButton").addEventListener("click", function() {
    alert("Извините, эта функция в данный момент недоступна.");
});