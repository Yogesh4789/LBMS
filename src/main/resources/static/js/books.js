/**
 * Books Management JavaScript
 */

function openEditBookModal(isbn, title, author, publisher, category, year, totalCopies) {
    document.getElementById("editIsbn").value = isbn;
    document.getElementById("editTitle").value = title;
    document.getElementById("editAuthor").value = author;
    document.getElementById("editPublisher").value = publisher;
    document.getElementById("editCategory").value = category;
    document.getElementById("editPublicationYear").value = year;
    document.getElementById("editTotalCopies").value = totalCopies;

    const modalEl = document.getElementById("editBookModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openDeleteBookModal(isbn, title) {
    document.getElementById("deleteIsbn").value = isbn;
    document.getElementById("deleteBookTitle").textContent = title;

    const modalEl = document.getElementById("deleteBookModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openViewBookModal(isbn, title, author, publisher, category, year, total, avail, status) {
    document.getElementById("viewIsbn").textContent = isbn;
    document.getElementById("viewTitle").textContent = title;
    document.getElementById("viewAuthor").textContent = author;
    document.getElementById("viewPublisher").textContent = publisher;
    document.getElementById("viewCategory").textContent = category;
    document.getElementById("viewYear").textContent = year;
    document.getElementById("viewCopies").textContent = avail + " / " + total;
    document.getElementById("viewStatus").textContent = status;

    const modalEl = document.getElementById("viewBookModal");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openViewBookModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openViewBookModal(
        btn.dataset.isbn,
        btn.dataset.title,
        btn.dataset.author,
        btn.dataset.publisher,
        btn.dataset.category,
        btn.dataset.year,
        btn.dataset.total,
        btn.dataset.available,
        btn.dataset.status
    );
}

function openEditBookModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openEditBookModal(
        btn.dataset.isbn,
        btn.dataset.title,
        btn.dataset.author,
        btn.dataset.publisher,
        btn.dataset.category,
        btn.dataset.year,
        btn.dataset.total
    );
}

function openDeleteBookModalFromBtn(btn) {
    if (!btn || !btn.dataset) return;
    openDeleteBookModal(btn.dataset.isbn, btn.dataset.title);
}

// Client-side instant filter on table rows
document.addEventListener("DOMContentLoaded", function () {
    const tableSearch = document.getElementById("clientTableSearch");
    if (tableSearch) {
        tableSearch.addEventListener("input", function () {
            const query = this.value.toLowerCase();
            const rows = document.querySelectorAll("#booksTableBody tr");
            rows.forEach(function (row) {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? "" : "none";
            });
        });
    }
});

