document.addEventListener("DOMContentLoaded", function () {
    const imageFileInput = document.getElementById("imageFileInput");
    const imagePreview = document.getElementById("imagePreview");

    if (!imageFileInput || !imagePreview) {
        return;
    }

    const initialSrc = imagePreview.dataset.initialSrc || "";
    let currentObjectURL = null;

    imageFileInput.addEventListener("change", function () {
        const file = this.files && this.files[0];

        if (currentObjectURL) {
            URL.revokeObjectURL(currentObjectURL);
            currentObjectURL = null;
        }

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

        currentObjectURL = URL.createObjectURL(file);
        imagePreview.src = currentObjectURL;
        imagePreview.classList.remove("d-none");
    });
});
