document.addEventListener("DOMContentLoaded", function () {
    const bar = document.querySelector(".sticky-cta-footer");
    if (bar) {
        bar.style.opacity = "0";
        bar.style.transition = "opacity 0.4s ease-in-out";
        setTimeout(() => { bar.style.opacity = "1"; }, 150);
    }
});