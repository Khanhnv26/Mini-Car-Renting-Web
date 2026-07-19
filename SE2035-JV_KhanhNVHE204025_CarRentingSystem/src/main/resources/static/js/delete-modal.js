document.addEventListener("DOMContentLoaded", function () {
    var deleteModal = document.getElementById("deleteModal");
    if (!deleteModal) {
        return;
    }
    deleteModal.addEventListener("show.bs.modal", function (event) {
        var button = event.relatedTarget;
        if (!button) {
            return;
        }
        var url = button.getAttribute("data-delete-url");
        var confirmBtn = deleteModal.querySelector("#confirmDeleteBtn");
        if (confirmBtn && url) {
            confirmBtn.setAttribute("href", url);
        }
    });
});
