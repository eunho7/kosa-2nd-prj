document.addEventListener('DOMContentLoaded', function () {
    const albumContainer = document.getElementById('results-grid');
    let sortType = 'latest';
    let responseData = [];
    let currentPage = 0;
    const itemsPerPage = 9;

    function loadAlbums(sort) {
        fetch(`/api/travel/myTravel?sort=${sort}`)
            .then(response => response.json())
            .then(data => {
                responseData = data;
                currentPage = 0; // 페이지 초기화
                albumContainer.innerHTML = ""; // 기존 데이터 초기화
                loadMoreData(); // 초기 데이터 로드
                document.getElementById("loadMoreBtn").style.display = "block";
            })
            .catch(error => console.error('Error fetching data:', error));
    }

    function appendDataToPage(data) {
        if (data.length === 0) {
            albumContainer.innerHTML = "<p>앨범이 없습니다.</p>";
            return;
        }

        data.forEach(item => {
            const resultItem = document.createElement("div");
            resultItem.classList.add("results-grid","col-md-4", "mb-3"); // 기본 클래스 추가

            resultItem.innerHTML = `
            <div class="result-item2">
                <a href="/api/travel/detail/${item.id}" class="text-decoration-none text-dark">
                    <img src="${item.thumbnail}" alt="Album Image" class="result-image img-fluid">
                    <div>
                        <h5 class="result-title">${item.title}</h5>
                        <p class="result-date">${item.dateRange}</p>
                    </div>
                </a>
            </div>
        `;

            albumContainer.appendChild(resultItem); // albumContainer에 추가
        });
    }

    function loadMoreData() {
        const startIndex = currentPage * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const dataToLoad = responseData.slice(startIndex, endIndex);

        appendDataToPage(dataToLoad);
        currentPage++;

        if (responseData.length <= endIndex) {
            document.getElementById("loadMoreBtn").style.display = "none";
        }
    }

    loadAlbums(sortType);

    document.getElementById("loadMoreBtn").addEventListener("click", function () {
        loadMoreData();
    });

    document.getElementById('latestSort').addEventListener('click', function () {
        loadAlbums('latest');
    });

    document.getElementById('likesSort').addEventListener('click', function () {
        loadAlbums('likes');
    });
});