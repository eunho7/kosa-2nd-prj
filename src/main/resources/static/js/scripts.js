function toggleMenu(event) {
    event.preventDefault();
    const popupMenu = document.getElementById('popupMenu');

    popupMenu.style.display = (popupMenu.style.display === 'block') ? 'none' : 'block';
}

document.addEventListener('click', function (event) {
    const popupMenu = document.getElementById('popupMenu');
    const homeButton = document.getElementById('homeButton');

    const isClickInsideMenu = popupMenu.contains(event.target);
    const isClickOnHomeButton = homeButton.contains(event.target);

    if (!isClickOnHomeButton && !isClickInsideMenu) {
        popupMenu.style.display = 'none';
    }
});
