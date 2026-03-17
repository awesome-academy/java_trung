document.addEventListener("DOMContentLoaded", function () {
    const imageFileInput = document.getElementById("imageFileInput");
    const imagePreview = document.getElementById("imagePreview");

    if (!imageFileInput || !imagePreview) {
        return;
    }

    const initialSrc = imagePreview.dataset.initialSrc || "";

    imageFileInput.addEventListener("change", function () {
        const file = this.files && this.files[0];

        if (!file) {
            if (initialSrc) {
                imagePreview.src = initialSrc;
                imagePreview.classList.remove("d-none");
            } else {
                imagePreview.src = "";
                imagePreview.classList.add("d-none");
            }
            return;
        }

        imagePreview.src = URL.createObjectURL(file);
        imagePreview.classList.remove("d-none");
    });
});
