// Toggle visibility of the search section
function toggleSearchSection() {
    const searchSection = document.getElementById('search-section');
    const toggleButton = document.getElementById('toggle-search');

    if (searchSection.style.display === 'none' || searchSection.style.display === '') {
        searchSection.style.display = 'block';
        toggleButton.innerHTML = '▲'; // Change to up arrow
    } else {
        searchSection.style.display = 'none';
        toggleButton.innerHTML = '▼'; // Change to down arrow
    }
}

// Toggle visibility of input fields based on selected radio button
function toggleInputFields() {
    const themeContainer = document.getElementById("theme-container");
    const regionContainer = document.getElementById("region-container");

    if (document.getElementById("theme-option").checked) {
        themeContainer.style.display = "block";
        regionContainer.style.display = "none";
    } else if (document.getElementById("region-option").checked) {
        themeContainer.style.display = "none";
        regionContainer.style.display = "block";
    } else {
        // Default to hide both if no option is selected
        themeContainer.style.display = "none";
        regionContainer.style.display = "none";
    }
}

// Toggle visibility of more images
function toggleImages() {
    const moreImages = document.getElementById('moreImages');
    const toggleIcon = document.getElementById('toggleIcon');

    if (moreImages.style.display === 'none' || moreImages.style.display === '') {
        moreImages.style.display = 'block';
        toggleIcon.classList.remove('fa-chevron-down');
        toggleIcon.classList.add('fa-chevron-up');
    } else {
        moreImages.style.display = 'none';
        toggleIcon.classList.remove('fa-chevron-up');
        toggleIcon.classList.add('fa-chevron-down');
    }
}

// Initialize the state of the images and popup menu
document.addEventListener('DOMContentLoaded', function() {
    const moreImages = document.getElementById('moreImages');
    moreImages.style.display = 'none'; // Ensure images are hidden initially

    const popupMenu = document.getElementById('popupMenu');
    popupMenu.style.display = 'none'; // Ensure the menu is hidden initially
});

// Toggle visibility of the popup menu
function toggleMenu(event) {
    event.preventDefault(); // Prevent the default action of the link
    const popupMenu = document.getElementById('popupMenu');
    const isMenuVisible = popupMenu.style.display === 'block';

    // Hide all menus if they are currently visible
    const allMenus = document.querySelectorAll('.popup-menu');
    allMenus.forEach(menu => {
        menu.style.display = 'none';
    });

    // Toggle the menu if it was hidden
    if (!isMenuVisible) {
        popupMenu.style.display = 'block';
    }
}

// Close the popup menu if clicking outside
document.addEventListener('click', function(event) {
    const popupMenu = document.getElementById('popupMenu');
    const homeButton = document.getElementById('homeButton');
    const isClickInside = homeButton.contains(event.target);

    if (!isClickInside && popupMenu.style.display === 'block') {
        popupMenu.style.display = 'none';
    }
});