document.addEventListener('DOMContentLoaded', function () {
    const darkModeToggle = document.getElementById('darkModeToggle');

    if (darkModeToggle) {
        darkModeToggle.addEventListener('click', function () {
            // 다크 모드 토글
            document.body.classList.toggle('dark-mode');

            // 텍스트만 변경 (버튼이 사라지지 않도록)
            if (document.body.classList.contains('dark-mode')) {
                darkModeToggle.textContent = '라이트 모드';
            } else {
                darkModeToggle.textContent = '다크 모드';
            }
        });
    }
});