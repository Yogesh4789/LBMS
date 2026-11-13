/**
 * Borrowing & Return Management JavaScript
 */

document.addEventListener("DOMContentLoaded", function () {
    // Dynamic due date calculation on Issue Books form
    const daysInput = document.getElementById("borrowingDays");
    const dueDateDisplay = document.getElementById("calculatedDueDateDisplay");
    if (daysInput && dueDateDisplay) {
        function updateDueDate() {
            const days = parseInt(daysInput.value) || 14;
            const target = new Date();
            target.setDate(target.getDate() + days);
            const options = { year: 'numeric', month: 'short', day: 'numeric' };
            dueDateDisplay.textContent = target.toLocaleDateString(undefined, options) + " (" + days + " days loan)";
        }
        daysInput.addEventListener("input", updateDueDate);
        updateDueDate();
    }
});

function openReturnConfirmModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openReturnConfirmModal(
        btn.dataset.txnid,
        btn.dataset.book,
        btn.dataset.member,
        btn.dataset.days,
        btn.dataset.fine
    );
}

function openReturnConfirmModal(txnId, bookTitle, memberName, overdueDays, fineAmount) {
    document.getElementById("returnTxnId").value = txnId;
    document.getElementById("returnDisplayTxnId").textContent = txnId;
    document.getElementById("returnDisplayBook").textContent = bookTitle;
    document.getElementById("returnDisplayMember").textContent = memberName;

    const fineNotice = document.getElementById("returnFineNotice");
    const fineAmountEl = document.getElementById("returnFineAmount");
    if (fineNotice && fineAmountEl) {
        if (overdueDays > 0) {
            fineNotice.classList.remove("d-none");
            fineAmountEl.textContent = "Rs. " + parseFloat(fineAmount).toFixed(2) + " (" + overdueDays + " overdue days @ Rs. 5/day)";
        } else {
            fineNotice.classList.add("d-none");
        }
    }

    const modalEl = document.getElementById("returnConfirmModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

