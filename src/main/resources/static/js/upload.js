document.addEventListener('DOMContentLoaded', function () {
    tinymce.init({
        selector: '#content',
        plugins: 'image code media link',
        toolbar: 'undo redo | bold italic | alignleft aligncenter alignright | image media link code',
        automatic_uploads: true,
        image_dimensions: false,
        images_upload_handler: function (blobInfo, success, failure) {
            let xhr = new XMLHttpRequest();
            xhr.open('POST', '/api/travel/upload-image');
            xhr.onload = function () {
                if (xhr.status !== 200) {
                    failure('HTTP Error: ' + xhr.status);
                    return;
                }

                let json = JSON.parse(xhr.responseText);
                if (!json || typeof json.location != 'string') {
                    failure('Invalid JSON: ' + xhr.responseText);
                    return;
                }
                success(json.location);
            };
            xhr.onerror = function () {
                failure('XHR Error: ' + xhr.status);
            };

            let formData = new FormData();
            formData.append('file', blobInfo.blob(), blobInfo.filename());

            // 이미지 크기 조정
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = new Image();
                img.src = e.target.result;
                img.onload = function() {
                    const canvas = document.createElement('canvas');
                    const ctx = canvas.getContext('2d');

                    // 원하는 크기로 조정
                    const maxWidth = 800;
                    const maxHeight = 600;
                    let width = img.width;
                    let height = img.height;

                    if (width > maxWidth) {
                        height *= maxWidth / width;
                        width = maxWidth;
                    }

                    if (height > maxHeight) {
                        width *= maxHeight / height;
                        height = maxHeight;
                    }

                    canvas.width = width;
                    canvas.height = height;
                    ctx.drawImage(img, 0, 0, width, height);

                    canvas.toBlob(function(blob) {
                        formData.set('file', blob, blobInfo.filename());
                        xhr.send(formData);
                    }, blobInfo.blob().type);
                };
            };
            reader.readAsDataURL(blobInfo.blob());
        }
    });

    document.getElementById('thumbnail').addEventListener('change', previewImages);
    document.querySelector('form').addEventListener('submit', function (event) {
        tinymce.get('content').save();

        const title = document.getElementById('title').value.trim();
        const region = document.getElementById('region').value.trim();
        const statDate = document.getElementById('statDate').value.trim();
        const endDate = document.getElementById('endDate').value.trim();
        const content = document.getElementById('content').value.trim();
        const thumbnail = document.getElementById('thumbnail').files.length > 0;
        const isPublic = document.querySelector('input[name="isPublic"]:checked');

        if (!title || !region || !statDate || !endDate || !thumbnail || !isPublic || !content) {
            alert('모든 필드를 올바르게 입력해 주세요.');
            event.preventDefault();
        } else {
            const isPublicValue = isPublic.value === 'true' ? 1 : 0;
            document.querySelector('input[name="isPublic"]').value = isPublicValue;
        }
    });
});

function previewImages() {
    const imageContainer = document.getElementById('imageContainer');
    imageContainer.innerHTML = ''; // 기존 이미지 제거

    const files = document.getElementById('thumbnail').files;
    Array.from(files).forEach(file => {
        const reader = new FileReader();
        reader.onload = function (e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.className = 'image-preview';
            imageContainer.appendChild(img);
        };
        reader.readAsDataURL(file);
    });
}

function addTag() {
    const input = document.getElementById('themeInput');
    const tagValue = input.value.trim();

    if (tagValue && !document.getElementById(tagValue)) {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.id = tagValue;
        tag.textContent = tagValue;

        const closeBtn = document.createElement('span');
        closeBtn.className = 'tag-close';
        closeBtn.textContent = 'x';
        closeBtn.onclick = function () {
            document.getElementById('tagContainer').removeChild(tag);
            updateHiddenFields();
        };

        tag.appendChild(closeBtn);
        document.getElementById('tagContainer').appendChild(tag);
        input.value = null;
        updateHiddenFields();
    }
}

function updateHiddenFields() {
    const tags = document.querySelectorAll('#tagContainer .tag');
    const hiddenFieldsContainer = document.getElementById('hiddenFieldsContainer');
    hiddenFieldsContainer.innerHTML = '';

    tags.forEach((tag, index) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = `travelThemeList[${index}].name`;
        input.value = tag.id;
        hiddenFieldsContainer.appendChild(input);
    });
}

document.getElementById('themeInput').addEventListener('keydown', function (event) {
    if (event.key === ' ') {
        event.preventDefault();
        addTag();
    }
});

function openMapPopup() {
    const albumElement = document.getElementById('albumElement');
    if (albumElement) {
        const albumId = albumElement.dataset.albumId;  // 서버에서 전달된 albumId 값
        if (!albumId) {
            console.error("albumId is undefined or null.");
            return;
        }
        const url = `/mapview?albumId=${albumId}`;
        const options = "width=800,height=600,scrollbars=yes,resizable=yes";
        window.open(url, "mapPopup", options);
    } else {
        alert('앨범 ID를 확인할 수 없습니다.');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelector('.btn-secondary').addEventListener('click', openMapPopup);
});