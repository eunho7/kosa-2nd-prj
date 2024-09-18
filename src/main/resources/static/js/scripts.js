function toggleMenu(event) {
    event.preventDefault();
    const popupMenu = document.getElementById('popupMenu');

    // Toggle the display of the popup menu
    popupMenu.style.display = (popupMenu.style.display === 'block') ? 'none' : 'block';
}

// Close the menu if clicked outside
document.addEventListener('click', function (event) {
    const popupMenu = document.getElementById('popupMenu');
    const homeButton = document.getElementById('homeButton');

    // Check if the click was outside the home button or popup menu
    const isClickInsideMenu = popupMenu.contains(event.target);
    const isClickOnHomeButton = homeButton.contains(event.target);

    // Close the menu if click is outside
    if (!isClickOnHomeButton && !isClickInsideMenu) {
        popupMenu.style.display = 'none';
    }
});

// Initialize the state of the more images container
window.onload = function () {
    document.getElementById('moreImages').style.display = 'none';
};