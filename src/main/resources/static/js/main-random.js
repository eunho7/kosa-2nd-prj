function toggleSearchSection() {
    const searchSection = document.getElementById('search-section');
    const toggleButton = document.getElementById('toggle-search');

    if (searchSection.style.display === 'none') {
        searchSection.style.display = 'block';
        toggleButton.innerHTML = '▲'; // 위로 화살표로 변경
    } else {
        searchSection.style.display = 'none';
        toggleButton.innerHTML = '▼'; // 아래로 화살표로 변경
    }
}

function toggleInputFields() {
    const themeContainer = document.getElementById("theme-container");
    const regionContainer = document.getElementById("region-container");

    if (document.getElementById("theme-option").checked) {
        themeContainer.style.display = "block";
        regionContainer.style.display = "none";
        // 지역 선택 시 테마 필드를 초기화
        document.getElementById("region-select").value = "";
    } else if (document.getElementById("region-option").checked) {
        themeContainer.style.display = "none";
        regionContainer.style.display = "block";
        // 테마 선택 시 지역 필드를 초기화
        document.getElementById("theme-input").value = "";
    }
}

function toggleImages() {
    const moreImages = document.getElementById('moreImages');
    const toggleIcon = document.getElementById('toggleIcon');

    if (moreImages.style.display === 'none' || moreImages.style.display === '') {
        moreImages.style.display = 'block';
        toggleIcon.classList.add('rotate-up');
    } else {
        moreImages.style.display = 'none';
        toggleIcon.classList.remove('rotate-up');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const moreImages = document.getElementById('moreImages');
    moreImages.style.display = 'none';

    const popupMenu = document.getElementById('popupMenu');
    popupMenu.style.display = 'none';

    // Reset the search options
    document.getElementById('theme-option').checked = false;
    document.getElementById('region-option').checked = false;
    document.getElementById('theme-container').style.display = 'none';
    document.getElementById('region-container').style.display = 'none';
});

function toggleMenu(event) {
    event.preventDefault();
    const popupMenu = document.getElementById('popupMenu');
    if (popupMenu.style.display === 'none' || popupMenu.style.display === '') {
        popupMenu.style.display = 'block';
    } else {
        popupMenu.style.display = 'none';
    }
}

document.addEventListener('click', function (event) {
    const popupMenu = document.getElementById('popupMenu');
    const homeButton = document.getElementById('homeButton');
    const isClickInside = homeButton.contains(event.target);
    if (!isClickInside) {
        popupMenu.style.display = 'none';
    }
});