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
        if (isLiked) {
            likeIcon.classList.add('filled');
            likeIcon.classList.remove('empty');
            likeCountElement.textContent = parseInt(likeCountElement.textContent) + (likeCountElement.textContent === '0' ? 1 : 0);
        } else {
            likeIcon.classList.add('empty');
            likeIcon.classList.remove('filled');
            likeCountElement.textContent = parseInt(likeCountElement.textContent) - (likeCountElement.textContent === '1' ? 1 : 0);
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