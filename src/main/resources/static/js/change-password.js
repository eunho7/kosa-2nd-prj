        $(document).ready(function() {
            let passwordStrength = '약함';

            $('#newPassword').on('input', function() {
                passwordStrength = checkPasswordStrength($(this).val());
                $('.strength-meter').text(passwordStrength.message).css('color', passwordStrength.color);
            });

            function checkPasswordStrength(password) {
                let strength = { message: '약함', color: 'red' };

                if (password.length >= 8) {
                    strength.message = '중간';
                    strength.color = 'orange';
                }

                if (/[A-Z]/.test(password) && /[0-9]/.test(password) && /[@$!%*?&#]/.test(password)) {
                    strength.message = '강함';
                    strength.color = 'green';
                }

                return strength;
            }

            $('form').on('submit', function(event) {
                if (passwordStrength.message !== '강함') {
                    event.preventDefault();
                    alert("대문자, 특수기호, 숫자를 포함한 비밀번호를 입력하세요.");
                }
            });
        });