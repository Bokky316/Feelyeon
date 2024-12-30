// 관리자 버튼
document.addEventListener("DOMContentLoaded", function () {
    // header script
    // menu slider
    const sideMenu = document.getElementById('sideMenu');
    const menuToggle = document.getElementById('menuToggle');

    function toggleMenu() {
        sideMenu.classList.toggle('open');
    }

    menuToggle.addEventListener('click', toggleMenu);

    document.addEventListener('click', function(event) {
        if (!sideMenu.contains(event.target) && !menuToggle.contains(event.target)) {
            sideMenu.classList.remove('open');
        }
    });
});