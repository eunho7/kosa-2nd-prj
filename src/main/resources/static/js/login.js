
    let currentSlide = 0;
    const slides = document.querySelectorAll('.onboarding-slide');
    const totalSlides = slides.length;

    // 자동 슬라이드 함수
    function autoSlide() {
        currentSlide = (currentSlide + 1) % totalSlides;
        const slidesContainer = document.getElementById('slidesContainer');
        slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
    }

    // 3초마다 슬라이드 자동 전환
    setInterval(autoSlide, 3000);

    // 로그인 화면을 토글하는 함수
    function toggleLogin() {
        const onboardingSection = document.getElementById('onboardingSection');
        const loginSection = document.getElementById('loginSection');

        onboardingSection.style.display = 'none';
        loginSection.style.display = 'flex';
    }

    // 온보딩 화면을 다시 보여주는 함수
    function toggleOnboarding() {
        const onboardingSection = document.getElementById('onboardingSection');
        const loginSection = document.getElementById('loginSection');

        loginSection.style.display = 'none';
        onboardingSection.style.display = 'flex';
    }