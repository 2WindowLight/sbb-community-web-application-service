// 다크 모드 버튼 기능
const toggleDarkModeButton = document.getElementById('toggleDarkMode');

// 로컬 저장소에서 다크 모드 상태 복원
const isDarkMode = localStorage.getItem('darkMode') === 'true';
if (isDarkMode) {
    document.body.classList.add('dark-mode');
}

// 버튼 클릭 시 다크 모드 전환
toggleDarkModeButton.addEventListener('click', () => {
    document.body.classList.toggle('dark-mode');
    const darkModeEnabled = document.body.classList.contains('dark-mode');
    localStorage.setItem('darkMode', darkModeEnabled); // 상태 저장
});