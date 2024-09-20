//체크박스 전체 선택 클릭 이chkClicked벤트
function allChecked(target) {
    const checkbox = document.getElementById('allCheckBox');  //전체 체크박스 버튼
    const is_checked = checkbox.checked; //전체 체크박스 버튼 체크 여부

    //전체 체크박스 제외한 모든 체크박스
    if (is_checked) {  // 체크박스 전체 체크
        chkAllChecked()
    } else {         //체크박스 전체 해제
        chkAllUnChecked()
    }
}

//자식 체크박스 클릭 이벤트
function chkClicked() {
    const allCount = document.querySelectorAll(".chk").length; //체크박스 전체개수

    //체크된 체크박스 전체개수
    const query = 'input[name="chk"]:checked'
    const selectedElements = document.querySelectorAll(query)
    const selectedElementsCnt = selectedElements.length;

    //체크박스 전체개수와 체크된 체크박스 전체개수가 같으면 전체 체크박스 체크
    //같지않으면 전체 체크박스 해제
    if (allCount == selectedElementsCnt) {
        document.getElementById('allCheckBox').checked = true;
    } else {
        document.getElementById('allCheckBox').checked = false;
    }
}

//체크박스 전체 체크
function chkAllChecked() {
    document.querySelectorAll(".chk").forEach(function (v, i) {
        v.checked = true;
    });
}

//체크박스 전체 체크 해제
function chkAllUnChecked() {
    document.querySelectorAll(".chk").forEach(function (v, i) {
        v.checked = false;
    });
}

//버튼 2개 다르게 동작
function submitForm(action) {
    const form = document.getElementById('actionForm');
    form.action = action; // 버튼 클릭에 따라 action을 변경
    form.submit(); // 폼 제출
}