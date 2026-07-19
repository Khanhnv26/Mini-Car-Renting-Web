document.addEventListener("DOMContentLoaded", function () {
    var navbar = document.getElementById("publicNavbar");
    if (!navbar) {
        return;
    }
    window.addEventListener("scroll", function () {
        if (window.scrollY > 50) {
            navbar.classList.add("scrolled");
        } else {
            navbar.classList.remove("scrolled");
        }
    });
});
