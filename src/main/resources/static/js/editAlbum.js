document.addEventListener('DOMContentLoaded', function () {
    // 서버에서 전달된 contentData를 Editor.js에 전달
    const savedData = window.contentData || {}; // 서버에서 넘어온 데이터가 없으면 빈 객체 사용
    const parsedData = JSON.parse(savedData);
    console.log(parsedData);

    window.editor = new EditorJS({
        holder: 'editorjs',
        tools: {
            image: {
                class: ImageTool,
                config: {
                    uploader: {
                        uploadByFile(file) {
                            return new Promise((resolve, reject) => {
                                const formData = new FormData();
                                formData.append('file', file);

                                fetch('/api/travel/edit/upload-image', {
                                    method: 'POST',
                                    body: formData,
                                })
                                    .then(response => response.json())
                                    .then(result => {
                                        resolve({
                                            success: 1,
                                            file: {
                                                url: result.url,
                                            },
                                        });
                                    })
                                    .catch(error => {
                                        reject(error);
                                    });
                            });
                        },
                    },
                },
                // 이미지가 업로드된 후 크기를 조정하는 함수
                onUpload: (response) => {
                    const wrapper = document.createElement('div');
                    wrapper.className = 'image-wrapper'; // .image-wrapper 클래스 추가

                    const img = document.createElement('img');
                    img.src = response.file.url;
                    img.style.width = '100%'; // 이미지 너비
                    img.style.height = '100%'; // 이미지 높이
                    img.style.objectFit = 'cover'; // 비율 유지

                    wrapper.appendChild(img);
                    return wrapper; // wrapper를 반환
                }
            },
        },
        data: parsedData, // 서버에서 전달된 기존 데이터
        onReady: () => {
            console.log('Editor.js initialized with saved content:', savedData);
        },
        onChange: () => {
            console.log('Content changed!');
        }
    });
    console.log("Editor initialized with content");
});

function addTag() {
    const input = document.getElementById('themeInput');
    let tagValue = input.value.trim();
    const tagContainer = document.getElementById('tagContainer');


    if (tagValue && !document.getElementById(tagValue)) {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.id = tagValue;
        tag.textContent = tagValue;

        const closeBtn = document.createElement('span');
        closeBtn.className = 'tag-close';
        closeBtn.textContent = 'x';
        closeBtn.onclick = function () {
            tagContainer.removeChild(tag);
            updateHiddenFields();
        };

        tag.appendChild(closeBtn);
        document.getElementById('tagContainer').appendChild(tag);
        input.value = '';
        updateHiddenFields();
    }

    updateHiddenFields();
}
function removeTag(element) {
    const tag = element.parentElement;

    const tagName = tag.getAttribute('data-tag-name');

    if (tagName) {
        console.log("Tag Name:", tagName);

        // 태그 삭제
        tag.remove(); // 부모 노드(tag)를 제거함

        updateHiddenFields();
    } else {
        console.error("Tag not found");
    }
}
function updateHiddenFields() {
    const tags = document.querySelectorAll('#tagContainer .tag');
    const hiddenFieldsContainer = document.getElementById('hiddenFieldsContainer');
    hiddenFieldsContainer.innerHTML = '';

    Array.from(tags).forEach((tag, index) => {
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

document.querySelector('form').addEventListener('submit', function (event) {
    event.preventDefault();
    editor.save().then((outputData) => {
        console.log('EditorJS JSON Output:', outputData);
        // outputData.content는 Editor.js에서 저장된 내용입니다.
        const contentInput = document.createElement('input');
        contentInput.type = 'hidden';
        contentInput.name = 'content';
        contentInput.value = JSON.stringify(outputData); // JSON.stringify를 사용하여 출력 데이터 변환
        this.appendChild(contentInput);

        // Validate form fields
        const title = document.getElementById('title').value.trim();
        const region = document.getElementById('region').value.trim();
        const statDate = document.getElementById('statDate').value.trim();
        const endDate = document.getElementById('endDate').value.trim();
        const isPublic = document.querySelector('input[name="isPublic"]:checked') !== null;

        if (!title || !region || !statDate || !endDate || !isPublic) {
            alert('모든 필드를 올바르게 입력해 주세요.');
            event.preventDefault();  // 폼 제출 방지
        } else {
            console.log('모든 필드가 유효합니다. 폼을 제출합니다.');
            this.submit(); // 모든 검사가 통과하면 폼 제출
        }
    }).catch((error) => {
        console.error('Saving failed: ', error);
        event.preventDefault();  // 폼 제출 방지
    });
});