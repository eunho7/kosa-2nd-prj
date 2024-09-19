document.addEventListener('DOMContentLoaded', function () {
    const albumContainer = document.getElementById('albumContainer');
    let sortType = 'latest';

    function loadAlbums(sort) {
        fetch(`/api/travel/myTravel?sort=${sort}`)
            .then(response => response.json())
            .then(data => {
                if (!data || !data.response) {
                    console.error('Invalid data structure:', data);
                    return;
                }

                console.log('Loaded data:', data);

                albumContainer.innerHTML = '';
                data.response.forEach(res => {
                    const albumCard = `
                    <div class="col-md-6 album-card">
                        <div class="card">
                            <a href="/api/travel/detail/${res.id}" class="text-decoration-none text-dark">
                                <img src="${res.thumbnail}" alt="Album Image" class="album-thumbnail card-img-top">
                                <div class="card-body">
                                    <h5 class="card-title album-title">${res.title}</h5>
                                    <p class="card-text album-date">${res.dateRange}</p>
                                </div>
                            </a>
                        </div>
                    </div>
                    `;
                    albumContainer.innerHTML += albumCard;
                });
            });
    }

    loadAlbums(sortType);

    document.getElementById('latestSort').addEventListener('click', function () {
        window.location.href = '/api/travel/myTravel?sort=latest';
    });

    document.getElementById('likesSort').addEventListener('click', function () {
        window.location.href = '/api/travel/myTravel?sort=likes';
    });
});