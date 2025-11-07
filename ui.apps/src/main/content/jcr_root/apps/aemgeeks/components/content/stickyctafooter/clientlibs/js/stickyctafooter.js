/**
 * Sticky CTA Footer Script
 * Handles show/hide functionality for the sticky CTA footer.
 */

document.addEventListener("DOMContentLoaded", () => {
  const stickyFooter = document.getElementById("sticky-footer-desktop");
  const closeBtn = document.getElementById("ctaCloseBtn");

  if (!stickyFooter) return;

  // Handle close button click
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      stickyFooter.style.display = "none";

      // Optional: store user preference in localStorage
      localStorage.setItem("hideStickyFooter", "true");
    });
  }

  // Optional: hide footer permanently after close
  const hideSticky = localStorage.getItem("hideStickyFooter");
  if (hideSticky === "true") {
    stickyFooter.style.display = "none";
  }

  // Optional: Reappear after a delay (uncomment to enable)
  // setTimeout(() => {
  //   stickyFooter.style.display = "flex";
  //   localStorage.removeItem("hideStickyFooter");
  // }, 60000); // Reappears after 60 seconds
});
