
const API_URL = "/api/transactions";

let allTransactions = [];
let isDemoMode = true;

// Check whether Spring Boot is running in demo mode
async function checkDemoMode() {
    try {
        const response = await fetch("/api/demo-mode");

        if (!response.ok) {
            throw new Error("Unable to check demo mode.");
        }

        const data = await response.json();

        isDemoMode = data.demoMode === true;

    } catch (error) {
        console.error("Demo mode detection failed:", error);

        // Keep editing disabled if the mode is unknown
        isDemoMode = true;
    }

    // Update the interface
    document.getElementById("demo-banner").hidden = !isDemoMode;

    document.querySelector(".transaction-form").hidden = isDemoMode;

    document.querySelector(
        ".transactions thead th:last-child"
    ).hidden = isDemoMode;

    document.getElementById("edit-modal").hidden = true;

    console.log("Demo mode:", isDemoMode);
}


// Transaction categories
const categories = {
    Income: [
        "Salary",
        "Allowance",
        "Bursary",
        "Other income"
    ],

    Expense: [
        "Food",
        "Transport",
        "Data and airtime",
        "Rent",
        "Utilities",
        "Study",
        "Health",
        "Entertainment",
        "Clothing",
        "Other expense"
    ]
};


// Update categories based on transaction type
function updateCategories() {
    const type = document.getElementById("type").value;
    const categorySelect = document.getElementById("category");

    categorySelect.replaceChildren();

    categories[type].forEach(category => {
        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        categorySelect.appendChild(option);
    });
}


// Format amounts as South African Rand
function formatCurrency(amount) {
    return new Intl.NumberFormat("en-ZA", {
        style: "currency",
        currency: "ZAR"
    }).format(amount);
}


// Load financial summary
async function loadBalance() {
    try {
        const response = await fetch(`${API_URL}/balance`);

        if (!response.ok) {
            throw new Error("Failed to load balance");
        }

        const data = await response.json();

        document.getElementById("total-income").textContent =
            formatCurrency(data.income);

        document.getElementById("total-expenses").textContent =
            formatCurrency(data.expenses);

        document.getElementById("current-balance").textContent =
            formatCurrency(data.balance);

    } catch (error) {
        console.error("Balance error:", error);
    }
}


// Load transaction history
async function loadTransactions() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load transactions");
        }

        const transactions = await response.json();

        allTransactions = transactions;

        // Sort transactions
        const sortOption =
            document.getElementById("transaction-sort").value;

        switch (sortOption) {
            case "date-newest":
                transactions.sort((a, b) =>
                    b.date.localeCompare(a.date) || b.id - a.id
                );
                break;

            case "date-oldest":
                transactions.sort((a, b) =>
                    a.date.localeCompare(b.date) || a.id - b.id
                );
                break;

            case "amount-high":
                transactions.sort((a, b) =>
                    b.amount - a.amount
                );
                break;

            case "amount-low":
                transactions.sort((a, b) =>
                    a.amount - b.amount
                );
                break;

            default:
                break;
        }

        const tableBody =
            document.getElementById("transaction-list");

        tableBody.replaceChildren();

        // Display a message if there are no transactions
        if (transactions.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = isDemoMode ? 5 : 6;
            cell.textContent = "No transactions yet.";

            row.appendChild(cell);
            tableBody.appendChild(row);
            return;
        }

        // Display each transaction
        transactions.forEach(transaction => {
            const row = document.createElement("tr");

            row.dataset.description =
                (transaction.description || "").toLowerCase();

            row.dataset.type = transaction.type;
            row.dataset.category = transaction.category;

            // Transaction information
            const values = [
                transaction.date,
                transaction.type,
                transaction.category,
                transaction.description,
                formatCurrency(transaction.amount)
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            // Editing controls are available only in normal mode
            if (!isDemoMode) {
                const actionsCell = document.createElement("td");

                // Edit button
                const editButton = document.createElement("button");
                editButton.textContent = "Edit";
                editButton.className = "edit-btn";

                editButton.addEventListener("click", () => {
                    editTransaction(transaction);
                });

                actionsCell.appendChild(editButton);

                // Delete button
                const deleteButton = document.createElement("button");
                deleteButton.textContent = "Delete";
                deleteButton.className = "delete-btn";

                deleteButton.addEventListener("click", () => {
                    deleteTransaction(transaction.id);
                });

                actionsCell.appendChild(deleteButton);

                row.appendChild(actionsCell);
            }

            tableBody.appendChild(row);
        });

        // Apply search and filters
        searchTransactions();

    } catch (error) {
        console.error("Transaction error:", error);
    }
}


// Search and filter transactions
function searchTransactions() {
    const searchText = document
        .getElementById("transaction-search")
        .value.trim().toLowerCase();

    const selectedType = document
        .getElementById("transaction-filter")
        .value;

    const selectedCategory = document
        .getElementById("category-filter")
        .value;

    const tableBody = document.getElementById("transaction-list");

    const rows = tableBody.querySelectorAll(
        "tr:not([data-empty-message])"
    );

    let visibleCount = 0;

    rows.forEach(row => {
        const description = row.dataset.description || "";
        const type = row.dataset.type || "";
        const category = row.dataset.category || "";

        const matchesSearch = description.includes(searchText);

        const matchesType =
            selectedType === "All" || type === selectedType;

        const matchesCategory =
            selectedCategory === "All" || category === selectedCategory;

        const visible =
            matchesSearch && matchesType && matchesCategory;

        row.hidden = !visible;

        if (visible) {
            visibleCount++;
        }
    });

    const oldMessage = tableBody.querySelector(
        "[data-empty-message]"
    );

    if (oldMessage) {
        oldMessage.remove();
    }

    if (visibleCount === 0 && rows.length > 0) {
        const row = document.createElement("tr");
        row.dataset.emptyMessage = "true";

        const cell = document.createElement("td");
        cell.colSpan = isDemoMode ? 5 : 6;

        cell.textContent =
            "No matching transactions found. Try another search or filter.";

        cell.className = "empty-results";

        row.appendChild(cell);
        tableBody.appendChild(row);
    }
}


// Load dashboard data
async function loadDashboard() {
    await Promise.all([
        loadBalance(),
        loadTransactions(),
        loadMonthlyReport()
    ]);
}


// Add a transaction
async function addTransaction(event) {
    event.preventDefault();

    if (isDemoMode) {
        alert("This is a read-only demo.");
        return;
    }

    const transaction = {
        type: document.getElementById("type").value,
        amount: Number(document.getElementById("amount").value),
        category: document.getElementById("category").value,
        description: document.getElementById("description").value,
        date: document.getElementById("date").value
    };

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(transaction)
        });

        if (!response.ok) {
            let errorMessage = "Failed to add transaction.";

            if (response.status === 400) {
                errorMessage =
                    "Invalid transaction details. Check your amount, category and date.";
            } else if (response.status === 403) {
                errorMessage = "This is a read-only demo.";
            } else if (response.status >= 500) {
                errorMessage =
                    "A server error occurred. Please try again later.";
            }

            throw new Error(errorMessage);
        }

        alert("Transaction added successfully!");

        document.getElementById("add-transaction-form").reset();
        updateCategories();

        await loadDashboard();
        await loadSpendingAnalytics();

    } catch (error) {
        console.error("Add transaction error:", error);
        alert(error.message);
    }
}


// Populate the category filter
function populateCategoryFilter() {
    const categoryFilter =
        document.getElementById("category-filter");

    categoryFilter.replaceChildren();

    const allOption = document.createElement("option");
    allOption.value = "All";
    allOption.textContent = "All Categories";

    categoryFilter.appendChild(allOption);

    const allCategories = [
        ...categories.Income,
        ...categories.Expense
    ];

    allCategories.forEach(category => {
        const option = document.createElement("option");

        option.value = category;
        option.textContent = category;

        categoryFilter.appendChild(option);
    });
}


// Delete a transaction
async function deleteTransaction(id) {
    if (isDemoMode) {
        alert("Deleting transactions is disabled in demo mode.");
        return;
    }

    const confirmed = confirm(
        "Are you sure you want to delete this transaction?"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Failed to delete transaction");
        }

        alert("Transaction deleted successfully!");

        await loadDashboard();
        await loadSpendingAnalytics();

    } catch (error) {
        alert(error.message);
        console.error("Delete error:", error);
    }
}


// Open the edit transaction modal
function editTransaction(transaction) {
    if (isDemoMode) {
        return;
    }

    document.getElementById("edit-id").value =
        transaction.id;

    document.getElementById("edit-amount").value =
        transaction.amount;

    document.getElementById("edit-description").value =
        transaction.description;

    document.getElementById("edit-date").value =
        transaction.date;

    const categorySelect =
        document.getElementById("edit-category");

    categorySelect.replaceChildren();

    categories[transaction.type].forEach(category => {
        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        categorySelect.appendChild(option);
    });

    categorySelect.value = transaction.category;

    document.getElementById("edit-modal").hidden = false;
}


// Save edited transaction
async function saveEditedTransaction(event) {
    event.preventDefault();

    if (isDemoMode) {
        alert("Editing transactions is disabled in demo mode.");
        return;
    }

    const id = document.getElementById("edit-id").value;

    const updatedTransaction = {
        amount: Number(
            document.getElementById("edit-amount").value
        ),
        category: document.getElementById("edit-category").value,
        description: document.getElementById("edit-description").value,
        date: document.getElementById("edit-date").value
    };

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedTransaction)
        });

        if (!response.ok) {
            throw new Error("Failed to update transaction");
        }

        document.getElementById("edit-modal").hidden = true;

        await loadDashboard();
        await loadSpendingAnalytics();

        alert("Transaction updated successfully!");

    } catch (error) {
        console.error("Edit transaction error:", error);
        alert("Could not update transaction. Please try again.");
    }
}


// Load monthly financial report
async function loadMonthlyReport() {
    const month = document.getElementById("report-month").value;

    if (!month) {
        return;
    }

    try {
        const response = await fetch(
            `/api/transactions/report?month=${encodeURIComponent(month)}`
        );

        if (!response.ok) {
            throw new Error("Failed to load monthly report.");
        }

        const report = await response.json();

        document.getElementById("monthly-income").textContent =
            formatCurrency(report.income);

        document.getElementById("monthly-expenses").textContent =
            formatCurrency(report.expenses);

        document.getElementById("monthly-balance").textContent =
            formatCurrency(report.balance);

    } catch (error) {
        console.error("Monthly report error:", error);
    }
}


// Close edit modal
function closeEditModal() {
    document.getElementById("edit-modal").hidden = true;
}


// Load spending analytics
async function loadSpendingAnalytics() {
    const monthInput = document.getElementById("analyticsMonth");
    const chart = document.getElementById("spendingChart");

    const selectedMonth = monthInput.value;

    if (!selectedMonth) {
        chart.textContent = "Please select a month.";
        return;
    }

    chart.textContent = "Loading spending analytics...";

    try {
        const response = await fetch(
            `/api/transactions/analytics?month=${encodeURIComponent(selectedMonth)}`
        );

        if (!response.ok) {
            throw new Error("Failed to load spending analytics");
        }

        const data = await response.json();
        const spendingCategories = data.categories;

        chart.replaceChildren();

        const entries = Object.entries(spendingCategories);

        if (entries.length === 0) {
            chart.textContent = "No expenses found for this month.";
            return;
        }

        const highestAmount = Math.max(
            ...entries.map(([, amount]) => Number(amount))
        );

        entries.sort((a, b) => Number(b[1]) - Number(a[1]));

        entries.forEach(([category, amount]) => {
            const percentage =
                highestAmount > 0
                    ? (Number(amount) / highestAmount) * 100
                    : 0;

            const container = document.createElement("div");
            container.className = "spending-bar-container";

            const label = document.createElement("div");
            label.className = "spending-bar-label";

            const categoryName = document.createElement("span");
            categoryName.textContent = category;

            const categoryAmount = document.createElement("span");
            categoryAmount.textContent = formatCurrency(amount);

            label.appendChild(categoryName);
            label.appendChild(categoryAmount);

            const track = document.createElement("div");
            track.className = "spending-bar-track";

            const fill = document.createElement("div");
            fill.className = "spending-bar-fill";
            fill.style.width = `${percentage}%`;

            track.appendChild(fill);

            container.appendChild(label);
            container.appendChild(track);

            chart.appendChild(container);
        });

    } catch (error) {
        console.error("Analytics error:", error);
        chart.textContent = "Unable to load spending analytics.";
    }
}


// Initialize dashboard after the page loads
document.addEventListener("DOMContentLoaded", async () => {

    // Check mode before rendering transactions
    await checkDemoMode();

    updateCategories();
    populateCategoryFilter();

    const today = new Date();

    const currentMonth =
        `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}`;

    document.getElementById("report-month").value = currentMonth;
    document.getElementById("analyticsMonth").value = currentMonth;

    // Register event listeners
    document.getElementById("type")
        .addEventListener("change", updateCategories);

    document.getElementById("add-transaction-form")
        .addEventListener("submit", addTransaction);

    document.getElementById("edit-transaction-form")
        .addEventListener("submit", saveEditedTransaction);

    document.getElementById("cancel-edit")
        .addEventListener("click", closeEditModal);

    document.getElementById("transaction-search")
        .addEventListener("input", searchTransactions);

    document.getElementById("transaction-filter")
        .addEventListener("change", searchTransactions);

    document.getElementById("category-filter")
        .addEventListener("change", searchTransactions);

    document.getElementById("transaction-sort")
        .addEventListener("change", loadTransactions);

    document.getElementById("report-month")
        .addEventListener("change", loadMonthlyReport);

    document.getElementById("analyticsMonth")
        .addEventListener("change", loadSpendingAnalytics);

    // Load dashboard and analytics
    await loadDashboard();
    await loadSpendingAnalytics();
});
