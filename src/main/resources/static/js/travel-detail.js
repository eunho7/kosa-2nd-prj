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
    let likeCount = parseInt(likeCountElement.textContent, 10);

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
                    if (isLiked) {
                        likeCount++;
                    } else {
                        likeCount--;
                    }
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
        likeCountElement.textContent = likeCount;
        if (isLiked) {
            likeIcon.classList.add('filled');
            likeIcon.classList.remove('empty');
        } else {
            likeIcon.classList.add('empty');
            likeIcon.classList.remove('filled');
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