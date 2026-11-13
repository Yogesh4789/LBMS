/**
 * Global Application JavaScript - Library Management System
 */

document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss toast notifications after 5 seconds
    const alerts = document.querySelectorAll(".alert-custom");
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = "opacity 0.4s ease, transform 0.4s ease";
            alert.style.opacity = "0";
            alert.style.transform = "translateX(50px)";
            setTimeout(function () {
                alert.remove();
            }, 400);
        }, 5000);
    });

    // Mobile sidebar toggle
    const toggleBtn = document.getElementById("mobileSidebarToggle");
    const sidebar = document.getElementById("appSidebar");
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener("click", function () {
            sidebar.classList.toggle("show");
        });
    }

    // Keyboard shortcut (Ctrl+K or Cmd+K) to focus navbar search
    document.addEventListener("keydown", function (e) {
        if ((e.ctrlKey || e.metaKey) && e.key === "k") {
            e.preventDefault();
            const searchInput = document.getElementById("globalSearchInput");
            if (searchInput) {
                searchInput.focus();
                searchInput.select();
            }
        }
    });
});

