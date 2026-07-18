document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toast-app").forEach(function (el) {
        if (typeof bootstrap !== "undefined" && bootstrap.Toast) {
            bootstrap.Toast.getOrCreateInstance(el).show();
        }
    });
});
