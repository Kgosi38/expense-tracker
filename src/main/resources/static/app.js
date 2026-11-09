
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
        .value
        .trim()
        .toLowerCase();

    const selectedType = document
        .getElementById("transaction-filter")
        .value;

    const selectedCategory = document
        .getElementById("category-filter")
        .value;

    const rows = document.querySelectorAll(
        "#transaction-list tr"
    );

    rows.forEach(row => {
        const description = row.dataset.description || "";
        const type = row.dataset.type || "";
        const category = row.dataset.category || "";

        const matchesSearch =
            description.includes(searchText);

        const matchesType =
            selectedType === "All" || type === selectedType;

        const matchesCategory =
            selectedCategory === "All" ||
            category === selectedCategory;

        row.hidden = !(
            matchesSearch &&
            matchesType &&
            matchesCategory
        );
    });
}






// Load dashboard data
async function loadDashboard() {
    await Promise.all([
        loadBalance(),
        loadTransactions()
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
            const errorData = await response.json();
            throw new Error(
                errorData.error || "Failed to add transaction"
            );
        }


alert("Transaction added successfully!");

document.getElementById("add-transaction-form").reset();

// Refresh categories to match the reset transaction type
updateCategories();

await loadDashboard();


    } catch (error) {
        alert(error.message);
        console.error("Add transaction error:", error);
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

        await loadDashboard();

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

        // Close the modal
        document.getElementById("edit-modal").hidden = true;

        // Refresh the dashboard
        await loadDashboard();

        alert("Transaction updated successfully!");

    } catch (error) {
        console.error(error);
        alert("Could not update transaction. Please try again.");
    }
}

function closeEditModal() {
    document.getElementById("edit-modal").hidden = true;
}

document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
    updateCategories();
    populateCategoryFilter();


document.getElementById("category-filter")
    .addEventListener("change", searchTransactions);



document.getElementById("transaction-search")
    .addEventListener("input", searchTransactions);


document.getElementById("transaction-filter")
    .addEventListener("change", searchTransactions);

    // Update category options when transaction type changes
    document.getElementById("type")
        .addEventListener("change", updateCategories);

    // Add a new transaction
    document.getElementById("add-transaction-form")
        .addEventListener("submit", addTransaction);

    // Save edited transaction
    document.getElementById("edit-transaction-form")
        .addEventListener("submit", saveEditedTransaction);

    // Close modal without saving
    document.getElementById("cancel-edit")
        .addEventListener("click", closeEditModal);
});


