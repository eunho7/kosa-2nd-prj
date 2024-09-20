let map;
let service;
let infowindow;
let markers = [];
let path = [];
let polyline;
let markerCounter = 1; // 마커 번호를 저장하는 전역 변수

// URL에서 albumId 값을 추출하는 함수
function getAlbumIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('albumId'); // URL에서 albumId 값을 가져옴
}

function initMap() {
    const seoul = new google.maps.LatLng(37.5665, 126.9780); // 서울 중심 좌표

    map = new google.maps.Map(document.getElementById("map"), {
        center: seoul,
        zoom: 12
    });

    infowindow = new google.maps.InfoWindow();

    // Polyline 객체를 생성하고 지도에 추가
    polyline = new google.maps.Polyline({
        path: path,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });
    polyline.setMap(map);
}

function searchPlace() {
    const placeName = document.getElementById("address").value;

    if (placeName === '') {
        document.getElementById('place-list').innerHTML = ''; // 입력값이 없을 시 목록을 비움
        return;
    }

    const request = {
        query: placeName,
        fields: ['name', 'geometry', 'place_id', 'formatted_address'],
    };

    service = new google.maps.places.PlacesService(map);
    service.findPlaceFromQuery(request, function(results, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
            displayPlaceList(results);
        } else if (status === google.maps.places.PlacesServiceStatus.ZERO_RESULTS) {
            displayNoResults(); // ZERO_RESULTS일 때 처리
        } else {
            console.error('Place search failed: ' + status);
        }
    });
}

function displayPlaceList(places) {
    const placeList = document.getElementById("place-list");
    placeList.innerHTML = ''; // 기존 목록을 지움

    // 검색된 장소들을 목록에 표시
    places.forEach((place, index) => {
        const listItem = document.createElement("li");
        listItem.textContent = `${place.name} - ${place.formatted_address}`;

        listItem.setAttribute("data-place-id", place.place_id);
        listItem.setAttribute("data-index", index);
        listItem.addEventListener("click", () => selectPlace(places[index])); // 마커 번호 전달
        placeList.appendChild(listItem);
    });
}

function displayNoResults() {
    const placeList = document.getElementById("place-list");
    placeList.innerHTML = ''; // 기존 목록을 지움

    // '결과 없음' 메시지 표시
    const noResultsItem = document.createElement("div");
    noResultsItem.classList.add("no-results");
    noResultsItem.textContent = "No places found. Please try another search.";
    placeList.appendChild(noResultsItem);
}

function saveMarkerToDB(markerData) {
    console.log(markerData);
    fetch('/api/markers/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(markerData),
    })
        .then(response => response.json())
        .then(data => {
            console.log('Marker saved:', data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function selectPlace(place) {
    const location = place.geometry.location;
    const albumId = getAlbumIdFromUrl();  // 앨범 ID를 URL에서 추출
    console.log(albumId);

    // 지도 중심을 선택한 장소로 이동
    map.setCenter(location);

    // 마커 추가 (마커 번호를 Label로 설정)
    const marker = new google.maps.Marker({
        map: map,
        position: location,
        label: markerCounter.toString(), // 마커에 순서 번호 표시
    });
    markers.push(marker);

    // 경로에 새로운 좌표 추가
    path.push(location);
    polyline.setPath(path);

    // 마커 번호를 증가시켜 다음 마커에 다른 번호가 적용되도록 함
    markerCounter++;

    // 장소 정보 저장을 위한 데이터
    const markerData = {
        placeName: place.name,
        address: place.formatted_address,
        latitude: location.lat(),
        longitude: location.lng(),
        markerNumber: markerCounter,
        travelBoardId: albumId
    };
    console.log(markerData);
    // 마커를 DB에 저장
    saveMarkerToDB(markerData);

    // 장소 정보창
    infowindow.setContent(place.name + "<br>" + place.formatted_address);
    infowindow.open(map, marker);
}

// 닫기 버튼에 대한 이벤트 리스너 추가
document.addEventListener("DOMContentLoaded", function() {
    const closeBtn = document.getElementById("close-btn");

    closeBtn.addEventListener("click", function() {
        window.close(); // 창을 닫음
    });
});