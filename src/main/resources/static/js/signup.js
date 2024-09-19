        $(document).ready(function() {
            // 비밀번호 유효성 검사 및 강도 확인
            $('#password').on('input', function () {
                const password = $(this).val();
                const strength = checkPasswordStrength(password);
                $('#passwordStrength').text(strength.message).css('color', strength.color);
            });

            // 비밀번호 확인
            $('#confirmPassword').on('input', function () {
                const password = $('#password').val();
                const confirmPassword = $(this).val();
                if (password !== confirmPassword) {
                    $('#passwordMatch').text('비밀번호가 일치하지 않습니다.').css('color', 'red');
                } else {
                    $('#passwordMatch').text('비밀번호가 일치합니다.').css('color', 'green');
                }
            });

            // 전화번호 유효성 검사
            $('#phone').on('input', function () {
                const phone = $(this).val();
                const phonePattern = /^\d{3}-\d{4}-\d{4}$/;
                if (!phonePattern.test(phone)) {
                    $('#phoneError').text('전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)').show();
                } else {
                    $('#phoneError').hide();
                }
            });

            // 폼 제출 시 유효성 검사
            $('#additionalFields').on('submit', function(event) {
                const password = $('#password').val();
                const confirmPassword = $('#confirmPassword').val();
                const passwordStrength = checkPasswordStrength(password);
                const phone = $('#phone').val();
                const phonePattern = /^\d{3}-\d{4}-\d{4}$/;

                if (password !== confirmPassword) {
                    event.preventDefault();
                    alert("비밀번호가 일치하지 않습니다.");
                    return false;
                }

                if (passwordStrength.message === '약함' || passwordStrength.message === '중간') {
                    event.preventDefault();
                    alert("대문자, 특수기호, 숫자를 포함한 비밀번호를 입력하세요.");
                    return false;
                }

                if (!phonePattern.test(phone)) {
                    event.preventDefault();
                    alert("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
                    return false;
                }
            });

            // 인증번호 확인
            $('#verifyCodeButton').click(function(event) {
                event.preventDefault();
                const inputCode = $('#verificationCode').val();
                const correctCode = $('#hiddenCode').val();

                if (inputCode === correctCode) {
                    alert("인증번호가 확인되었습니다.");
                    $('#email').prop('disabled', true);
                    $('#verificationCode').prop('disabled', true);
                    $('#sendCodeButton').prop('disabled', true);
                    $('#verifyCodeButton').prop('disabled', true);
                    $('#additionalFields').slideDown();
                } else {
                    alert("인증번호가 일치하지 않습니다.");
                }
            });
        });

        // 비밀번호 강도 확인 함수
        function checkPasswordStrength(password) {
            const minLength = 7;
            const maxLength = 18;
            let strength = { message: '약함', color: 'red' };

            if (password.length >= minLength && password.length <= maxLength &&
                /[A-Z]/.test(password) && /[0-9]/.test(password) && /[\W_]/.test(password)) {
                strength = { message: '강함', color: 'green' };
            } else if (password.length >= minLength) {
                strength = { message: '중간', color: 'orange' };
            }
            return strength;
        }