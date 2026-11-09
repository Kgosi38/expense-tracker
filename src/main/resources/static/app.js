
const API_URL = "/api/transactions";

let allTransactions = [];


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
        // Fetch transactions from the Java backend
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load transactions");
        }

        const transactions = await response.json();

        // Store the latest transactions
        allTransactions = transactions;


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
        // Keep the original order returned by the API
        break;
}


        const tableBody =
            document.getElementById("transaction-list");

        // Clear existing rows
        tableBody.replaceChildren();

        // Display a message if no transactions exist
        if (transactions.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 6;
            cell.textContent = "No transactions yet.";

            row.appendChild(cell);
            tableBody.appendChild(row);
            return;
        }

        // Display each transaction
        transactions.forEach(transaction => {

            const row = document.createElement("tr");

            // Store searchable information
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

            // Actions column
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

            // Add actions to row
            row.appendChild(actionsCell);

            // Add completed row to table
            tableBody.appendChild(row);
        });

        // Apply search and type filters
        searchTransactions();

    } catch (error) {
        console.error("Transaction error:", error);
    }
}






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
        cell.colSpan = 6;
        cell.textContent = "No matching transactions found. Try another search or filter.";
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




async function addTransaction(event) {
    event.preventDefault();

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
            } else if (response.status >= 500) {
                errorMessage =
                    "A server error occurred. Please try again later.";
            }

            throw new Error(errorMessage);
        }

        alert("Transaction added successfully!");

        // Clear the transaction form
        document.getElementById("add-transaction-form").reset();

        // Refresh categories to match the reset transaction type
        updateCategories();

        // Refresh the dashboard
        await loadDashboard();

        // Refresh the Spending Analytics chart
        await loadSpendingAnalytics();

    } catch (error) {
        console.error("Add transaction error:", error);
        alert(error.message);
    }
}

function populateCategoryFilter() {
    const categoryFilter =
        document.getElementById("category-filter");

    // Clear existing options
    categoryFilter.replaceChildren();

    // Add the default option
    const allOption = document.createElement("option");
    allOption.value = "All";
    allOption.textContent = "All Categories";

    categoryFilter.appendChild(allOption);

    // Get all income and expense categories
    const allCategories = [
        ...categories.Income,
        ...categories.Expense
    ];

    // Add each category to the dropdown
    allCategories.forEach(category => {
        const option = document.createElement("option");

        option.value = category;
        option.textContent = category;

        categoryFilter.appendChild(option);
    });
}



async function deleteTransaction(id) {

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

        // Refresh the dashboard and transaction history
        await loadDashboard();

        // Refresh Spending Analytics
        await loadSpendingAnalytics();

    } catch (error) {
        alert(error.message);
        console.error("Delete error:", error);
    }
}



function editTransaction(transaction) {



    // Store the transaction ID
    document.getElementById("edit-id").value =
        transaction.id;

    // Fill in the existing amount
    document.getElementById("edit-amount").value =
        transaction.amount;

    // Fill in the existing description
    document.getElementById("edit-description").value =
        transaction.description;

    // Fill in the existing date
    document.getElementById("edit-date").value =
        transaction.date;

    // Populate categories based on transaction type
    const categorySelect =
        document.getElementById("edit-category");

    categorySelect.replaceChildren();

    categories[transaction.type].forEach(category => {
        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        categorySelect.appendChild(option);
    });

    // Select the transaction's current category
    categorySelect.value = transaction.category;

    // Display the editing modal
    document.getElementById("edit-modal").hidden = false;
}


async function saveEditedTransaction(event) {
    event.preventDefault();

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

        // Close the editing modal
        document.getElementById("edit-modal").hidden = true;

        // Refresh the dashboard and transaction history
        await loadDashboard();

        // Refresh Spending Analytics
        await loadSpendingAnalytics();

        alert("Transaction updated successfully!");

    } catch (error) {
        console.error("Edit transaction error:", error);
        alert("Could not update transaction. Please try again.");
    }
}



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


function closeEditModal() {
    document.getElementById("edit-modal").hidden = true;
}

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


const categories = data.categories;

chart.innerHTML = "";

const entries = Object.entries(categories);

if (entries.length === 0) {
    chart.textContent = "No expenses found for this month.";
    return;
}

const highestAmount = Math.max(
    ...entries.map(([category, amount]) => Number(amount))
);

entries.sort((a, b) => Number(b[1]) - Number(a[1]));

entries.forEach(([category, amount]) => {
    const percentage = (Number(amount) / highestAmount) * 100;

    const container = document.createElement("div");
    container.className = "spending-bar-container";

    const label = document.createElement("div");
    label.className = "spending-bar-label";

    const categoryName = document.createElement("span");
    categoryName.textContent = category;

    const categoryAmount = document.createElement("span");
    categoryAmount.textContent = `R${Number(amount).toFixed(2)}`;

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
        console.error(error);
        chart.textContent = "Unable to load spending analytics.";
    }
}

document.getElementById("analyticsMonth")
    .addEventListener("change", loadSpendingAnalytics);

document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
    updateCategories();
    populateCategoryFilter();

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


const reportMonth = document.getElementById("report-month");

reportMonth.value = new Date().toLocaleDateString("en-CA").slice(0, 7);


const today = new Date();
reportMonth.value =
    `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}`;


loadMonthlyReport();

});



