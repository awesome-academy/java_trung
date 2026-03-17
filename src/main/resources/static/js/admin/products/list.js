document.addEventListener("DOMContentLoaded", function () {
    const minInput = document.getElementById("minPriceInput");
    const maxInput = document.getElementById("maxPriceInput");

    if (!minInput || !maxInput) {
        return;
    }

    function syncPriceRange() {
        const minValue = minInput.value.trim();
        const maxValue = maxInput.value.trim();

        maxInput.min = minValue !== "" ? minValue : "0";
        minInput.max = maxValue !== "" ? maxValue : "";

        if (minValue !== "" && maxValue !== "" && Number(minValue) > Number(maxValue)) {
            maxInput.value = minValue;
        }
    }

    minInput.addEventListener("input", syncPriceRange);
    maxInput.addEventListener("input", syncPriceRange);
    syncPriceRange();
});
