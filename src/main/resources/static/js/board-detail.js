function deletePost(postId) {
    if (confirm('정말로 삭제하시겠습니까?')) {
        fetch(`/board/delete/${postId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                console.log('삭제 성공:', response.status);
                window.location.href = '/board/list';  // 삭제 성공 시 목록으로 리디렉션
            } else if (response.status === 403) {
                alert('삭제할 권한이 없습니다.');
            } else {
                console.error('삭제 실패:', response.status, response.statusText);
                alert('삭제에 실패했습니다. 상태 코드: ' + response.status);
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('삭제 요청 중 오류가 발생했습니다.');
        });
    }
}

function deleteReply(replyIdx) {
    if (confirm('정말로 댓글을 삭제하시겠습니까?')) {
        fetch(`/board/replies/delete/${replyIdx}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                console.log('삭제 성공:', response.status);
                window.location.reload();  // 삭제 후 페이지 새로고침
            } else if (response.status === 403) {
                alert('삭제할 권한이 없습니다.');
            } else {
                console.error('삭제 실패:', response.status, response.statusText);
                alert('삭제에 실패했습니다. 상태 코드: ' + response.status);
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('삭제 요청 중 오류가 발생했습니다.');
        });
    }
}

function editReply(replyIdx) {
    const editForm = document.getElementById(`edit-form-${replyIdx}`);
    if (editForm) {
        editForm.style.display = 'block';  // 수정 폼 보이기
    } else {
        console.error(`Edit form with id edit-form-${replyIdx} not found`);
    }
}

function cancelEdit(replyIdx) {
    const editForm = document.getElementById(`edit-form-${replyIdx}`);
    if (editForm) {
        editForm.style.display = 'none';  // 수정 폼 숨기기
    } else {
        console.error(`Edit form with id edit-form-${replyIdx} not found`);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // 수정 버튼이 눌리면 editReply 함수가 호출되도록 설정
    window.editReply = editReply;
    window.cancelEdit = cancelEdit;
});
