function toggleMenu(event) {
    event.preventDefault();
    const popupMenu = document.getElementById('popupMenu');
    if (popupMenu.style.display === 'none' || popupMenu.style.display === '') {
        popupMenu.style.display = 'block';
    } else {
        popupMenu.style.display = 'none';
    }
}

// 페이지 외부 클릭 시 메뉴 닫기
document.addEventListener('click', function (event) {
    const popupMenu = document.getElementById('popupMenu');
    const homeButton = document.getElementById('homeButton');
    const isClickInside = homeButton.contains(event.target);
    if (!isClickInside) {
        popupMenu.style.display = 'none';
    }
});