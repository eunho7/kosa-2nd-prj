document.addEventListener('DOMContentLoaded', function() {
    const travelIdx = document.getElementById('travelIdx').value;
    const userIdx = document.getElementById('userIdx').value;
    const likeIcon = document.getElementById('likeIcon');
    const likeCountElement = document.getElementById('likeCount');
    const initialLikeStatus = document.getElementById('initialLikeStatus').value === 'true';

    if (!travelIdx || !userIdx) {
        console.error('Travel Index or User Index is missing.');
        return;
    }

    let isLiked = initialLikeStatus;
    let likeCount = parseInt(likeCountElement.textContent, 10);

    function toggleLike() {
        const url = `/api/travel/like/${travelIdx}`;
        const method = isLiked ? 'DELETE' : 'POST';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userIdx: userIdx
            }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    isLiked = !isLiked;
                    if (isLiked) {
                        likeCount++;
                    } else {
                        likeCount--;
                    }
                    updateLikeUI();
                } else {
                    alert('좋아요 처리에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('좋아요 처리에 실패했습니다.');
            });
    }

    function updateLikeUI() {
        likeCountElement.textContent = likeCount;
        if (isLiked) {
            likeIcon.classList.add('filled');
            likeIcon.classList.remove('empty');
        } else {
            likeIcon.classList.add('empty');
            likeIcon.classList.remove('filled');
        }
    }

    // 초기 상태 설정
    updateLikeUI();

    likeIcon.addEventListener('click', toggleLike);

    document.getElementById('userListContainer').style.display = 'none';

    likeIcon.addEventListener('mouseenter', function() {
        const userListContainer = document.getElementById('userListContainer');
        userListContainer.style.display = 'block';
    });

    likeIcon.addEventListener('mouseleave', function() {
        const userListContainer = document.getElementById('userListContainer');
        userListContainer.style.display = 'none';
    });

    document.addEventListener('click', function(event) {
        const userListContainer = document.getElementById('userListContainer');
        if (!userListContainer.contains(event.target) && !likeIcon.contains(event.target)) {
            userListContainer.style.display = 'none';
        }
    });
});

// 마커 리스트를 JSON 형식으로 받아옴
// var markersList = document.getElementById('markersList').value;
var markersList = /*[[${markers}]]*/ '[]';
// 마커 데이터를 확인
console.log("Markers List:", markersList);

// JSON 문자열을 실제 객체로 변환
var markers = JSON.parse(markersList);

// 구글 맵에 마커 추가
function initMap() {
    const seoul = { lat: 37.5665, lng: 126.9780 };
    const map = new google.maps.Map(document.getElementById('map'), {
        center: seoul,
        zoom: 10
    });

    // 마커 데이터를 저장할 배열
    const markerPositions = [];

    // 마커 데이터를 반복문으로 추가 (순서를 나타내기 위한 카운터 추가)
    markers.forEach(function(markerData, index) {
        const markerPosition = { lat: markerData.latitude, lng: markerData.longitude };
        markerPositions.push(markerPosition); // 마커 위치 배열에 추가
        addMarker(markerData, markerPosition, map, index + 1); // 순서대로 숫자 추가
    });

    // 마커들을 잇는 선(Polyline)을 그리기 위한 함수 호출
    drawPolyline(markerPositions, map);
}

// 마커를 추가하는 함수 (index 추가)
function addMarker(markerData, position, map, index) {
    const marker = new google.maps.Marker({
        position: position,
        map: map,
        label: {
            text: index.toString(), // 마커에 순서대로 번호 추가
            color: "white", // 레이블 색상
            fontSize: "16px",
            fontWeight: "bold",
        },
        title: markerData.placeName,
        icon: {
            url: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png", // 아이콘을 커스터마이징할 경우 사용
            labelOrigin: new google.maps.Point(15, 10) // 레이블 위치 조정
        }
    });

    const infowindow = new google.maps.InfoWindow({
        content: `<div><strong>${markerData.placeName}</strong><br>${markerData.address}</div>`
    });

    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });
}

// Polyline을 그려주는 함수
function drawPolyline(markerPositions, map) {
    const path = new google.maps.Polyline({
        path: markerPositions, // 마커들의 위치 배열을 경로로 설정
        geodesic: true,
        strokeColor: "#FF0000", // 선의 색상
        strokeOpacity: 1.0,
        strokeWeight: 2, // 선의 두께
    });

    path.setMap(map); // 지도에 Polyline을 추가
}