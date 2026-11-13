/**
 * Member Management JavaScript
 */

function openEditMemberModal(id, name, email, phone, address) {
    document.getElementById("editMemberId").value = id;
    document.getElementById("editMemberName").value = name;
    document.getElementById("editMemberEmail").value = email;
    document.getElementById("editMemberPhone").value = phone;
    document.getElementById("editMemberAddress").value = address;

    const modalEl = document.getElementById("editMemberModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openToggleStatusModal(id, name, currentStatus) {
    document.getElementById("statusMemberId").value = id;
    document.getElementById("statusMemberName").textContent = name;
    document.getElementById("statusActionName").textContent = currentStatus === "ACTIVE" ? "Deactivate" : "Activate";

    const modalEl = document.getElementById("toggleStatusModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openEditMemberModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openEditMemberModal(btn.dataset.id, btn.dataset.name, btn.dataset.email, btn.dataset.phone, btn.dataset.address);
}

function openToggleStatusModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openToggleStatusModal(btn.dataset.id, btn.dataset.name, btn.dataset.status);
}

// Client-side quick filter for members
document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("clientMemberSearch");
    if (searchInput) {
        searchInput.addEventListener("input", function () {
            const query = this.value.toLowerCase();
            const rows = document.querySelectorAll("#membersTableBody tr");
            rows.forEach(function (row) {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? "" : "none";
            });
        });
    }
});

