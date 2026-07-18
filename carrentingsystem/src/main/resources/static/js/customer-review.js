function setReviewRentalId(rentalId) {
    var input = document.getElementById("modalRentalId");
    if (input) {
        input.value = rentalId || "";
    }
    var star = document.getElementById("reviewStar");
    if (star) {
        star.value = "5";
    }
    var comment = document.getElementById("reviewComment");
    if (comment) {
        comment.value = "";
    }
}

function showMyReview(btn) {
    if (!btn) {
        return;
    }
    var star = btn.getAttribute("data-star") || "";
    var comment = btn.getAttribute("data-comment") || "";
    var car = btn.getAttribute("data-car") || "—";
    var starEl = document.getElementById("viewReviewStar");
    var commentEl = document.getElementById("viewReviewComment");
    var carEl = document.getElementById("viewReviewCar");
    if (starEl) {
        starEl.textContent = star ? (star + " sao") : "—";
    }
    if (commentEl) {
        commentEl.textContent = comment || "—";
    }
    if (carEl) {
        carEl.textContent = car;
    }
}

window.setReviewRentalId = setReviewRentalId;
window.showMyReview = showMyReview;

document.addEventListener("DOMContentLoaded", function () {
    var reviewModal = document.getElementById("reviewModal");
    if (reviewModal) {
        reviewModal.addEventListener("show.bs.modal", function (event) {
            var button = event.relatedTarget;
            if (!button) {
                return;
            }
            var rentalId = button.getAttribute("data-rental-id");
            if (rentalId) {
                setReviewRentalId(rentalId);
            }
        });
    }

    var viewModal = document.getElementById("viewReviewModal");
    if (viewModal) {
        viewModal.addEventListener("show.bs.modal", function (event) {
            showMyReview(event.relatedTarget);
        });
    }

    var reviewForm = document.getElementById("reviewForm");
    if (reviewForm) {
        reviewForm.addEventListener("submit", function (e) {
            var idInput = document.getElementById("modalRentalId");
            if (!idInput || !String(idInput.value).trim()) {
                e.preventDefault();
                alert("Không xác định được mã thuê xe. Hãy đóng modal và bấm lại Viết đánh giá.");
            }
        });
    }
});
