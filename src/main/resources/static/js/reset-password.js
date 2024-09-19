 $(document).ready(function() {
        let passwordStrength = '약함';

        $('#newPassword').on('input', function() {
            passwordStrength = checkPasswordStrength($(this).val());
            $('#passwordStrength').text(passwordStrength.message).css('color', passwordStrength.color);
        });

        // 폼 제출 시 비밀번호 강도 확인
        $('#resetForm').on('submit', function(event) {
            if (passwordStrength.message !== '강함') {
                event.preventDefault();
                alert("대문자, 특수기호, 숫자를 포함한 비밀번호를 입력하세요.");
            }
        });

        // 비밀번호 강도 확인 함수
        function checkPasswordStrength(password) {
            let strength = { message: '약함', color: 'red' };

            if (password.length >= 7 && password.length <= 18) {
                strength.message = '중간';
                strength.color = 'orange';
            }

            if (/[A-Z]/.test(password) && /[0-9]/.test(password) && /[@$!%*?&#]/.test(password)) {
                strength.message = '강함';
                strength.color = 'green';
            }

            return strength;
        }
    });