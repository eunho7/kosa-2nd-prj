function toggleSearchSection() {
    const searchSection = document.getElementById('search-section');
    if (searchSection.style.display === 'none' || searchSection.style.display === '') {
        searchSection.style.display = 'block';
    } else {
        searchSection.style.display = 'none';
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

document.addEventListener('DOMContentLoaded', () => {
    const swiperContainer = document.getElementById('swiperContainer');
    const imageList = document.querySelectorAll('.swiper-slide img');

    if (imageList.length > 0) {
        swiperContainer.style.display = 'block';

        new Swiper('.swiper-container', {
            loop: true, // 무한 루프
            pagination: {
                el: '.swiper-pagination',
                clickable: true,
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
            slidesPerView: 3, // 한 번에 보여줄 슬라이드 수
            spaceBetween: 10, // 슬라이드 간의 간격
            breakpoints: {
                640: {
                    slidesPerView: 1,
                    spaceBetween: 10,
                },
                768: {
                    slidesPerView: 2,
                    spaceBetween: 20,
                },
                1024: {
                    slidesPerView: 3,
                    spaceBetween: 30,
                },
            },
        });
    } else {
        swiperContainer.style.display = 'none';
    }
});