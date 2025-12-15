const API_URL = 'http://localhost:8081/api';
let currentUser = null;

// --- ON PAGE LOAD ---
// Check if user is stored in browser memory (LocalStorage)
document.addEventListener("DOMContentLoaded", () => {
    const storedUser = localStorage.getItem('library_user');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
        restoreSession(); // Restore the interface state
    }
});

// --- 1. LOGIN ---
async function handleLogin() {
    const email = document.getElementById('emailInput').value;
    const password = document.getElementById('passwordInput').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) throw new Error("Login failed");

        currentUser = await response.json();

        // SAVE USER TO BROWSER MEMORY!
        localStorage.setItem('library_user', JSON.stringify(currentUser));

        restoreSession(); // Show dashboard panels

    } catch (error) {
        alert("❌ Invalid email or password!");
    }
}

// --- SESSION RESTORATION (If already logged in) ---
function restoreSession() {
    // If we are on the LOGIN page (index.html), hide the login form
    if (document.getElementById('loginSection')) {
        document.getElementById('loginSection').classList.add('hidden');
        document.getElementById('dashboardSection').classList.remove('hidden');
        document.getElementById('welcomeMsg').innerText = `Hello, ${currentUser.fullName}`;

        if (currentUser.role === 'ADMIN') {
            document.getElementById('adminPanel').classList.remove('hidden');
        } else {
            document.getElementById('studentPanel').classList.remove('hidden');
            loadMyBorrows();
        }
        loadBooks();
    }
}

// --- CHECK AUTH ON users.html ---
function checkAuth() {
    if (!currentUser || currentUser.role !== 'ADMIN') {
        alert("⛔ Access Denied! Admins only.");
        window.location.href = 'index.html'; // Redirect to main page
    }
}

function logout() {
    localStorage.removeItem('library_user'); // Clear memory
    window.location.href = 'index.html'; // Redirect to main page
}

// ==========================================
//           BOOK LOGIC (index.html)
// ==========================================

async function loadBooks() {
    if(!document.getElementById('booksTableBody')) return;

    const response = await fetch(`${API_URL}/books`);
    const books = await response.json();
    const tbody = document.getElementById('booksTableBody');
    tbody.innerHTML = '';

    // Сhecking, admin or not (Defense from null)
    const isAdmin = currentUser && currentUser.role === 'ADMIN';

    books.forEach(book => {
        // --- Author's Logik ---
        const authorNames = (book.authors && book.authors.length > 0)
            ? book.authors.map(a => a.name).join(', ')
            : '<span style="color:#b2bec3">Unknown</span>';

        const isFree = book.available;

        const statusBadge = isFree ?
            '<span class="badge status-ok">Available</span>' :
            '<span class="badge status-busy">Borrowed</span>';

        let actionBtn = '';
        if (isAdmin) {
            actionBtn = `<button class="btn-danger" onclick="deleteBook(${book.id})">🗑️</button>`;
        } else if (isFree) {
            actionBtn = `<button class="btn-take" onclick="borrowBook(${book.id})">Borrow</button>`;
        } else {
            actionBtn = `<button disabled style="opacity:0.5">Taken</button>`;
        }

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${book.id}</td>
            <td><b>${book.title}</b></td>
            <td>${authorNames}</td>
            <td>${book.isbn || '-'}</td>
            <td>${statusBadge}</td>
            <td>${actionBtn}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function borrowBook(bookId) {
    if (!currentUser) return alert("Please log in!");
    await fetch(`${API_URL}/borrow?userId=${currentUser.id}&bookId=${bookId}`, { method: 'POST' });
    loadBooks(); loadMyBorrows();
}

async function deleteBook(id) {
    await fetch(`${API_URL}/books/${id}`, { method: 'DELETE' });
    loadBooks();
}

async function addNewBook() {
    const titleInput = document.getElementById('newBookTitle');

    // 👇 ВОТ ЭТИ СТРОКИ САМЫЕ ВАЖНЫЕ
    const authorInput = document.getElementById('newBookAuthor');
    const authorName = authorInput ? authorInput.value.trim() : "";
    // 👆 ----------------------------

    const isbnInput = document.getElementById('newBookIsbn');
    const title = titleInput.value.trim();
    const isbn = isbnInput.value.trim();

    if (!title || !authorName) {
        alert("⚠️ Enter name and author!");
        return;
    }

    await fetch(`${API_URL}/books`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            isbn: isbn,
            authorName: authorName
        })
    });


    titleInput.value = '';
    if(authorInput) authorInput.value = '';
    isbnInput.value = '';

    loadBooks();
}
async function loadMyBorrows() {
    if(!document.getElementById('myBorrowsList')) return;
    const response = await fetch(`${API_URL}/borrow/my/${currentUser.id}`);
    const borrows = await response.json();
    const list = document.getElementById('myBorrowsList');
    list.innerHTML = '';

    borrows.forEach(b => {
        if (!b.returnDate) {
            const li = document.createElement('li');
            li.innerHTML = `<div><b>${b.book.title}</b><br><small>Due: ${b.dueDate}</small></div><button class="btn-secondary" onclick="returnBook(${b.id})">Return</button>`;
            list.appendChild(li);
        }
    });
}

async function returnBook(id) {
    await fetch(`${API_URL}/borrow/return/${id}`, { method: 'POST' });
    loadMyBorrows(); loadBooks();
}

// ==========================================
//        USER LOGIC (users.html)
// ==========================================

async function loadUsers() {
    const tbody = document.getElementById('usersTableBody');
    if(!tbody) return; // If not on users page, exit

    const response = await fetch(`${API_URL}/users`);
    const users = await response.json();
    tbody.innerHTML = '';

    users.forEach(user => {
        const debtStyle = user.totalDebt > 0 ? 'color:red;font-weight:bold' : 'color:green';
        const activeStyle = user.activeBooks > 0 ? 'color:orange;font-weight:bold' : '';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td style="${activeStyle}">${user.activeBooks}</td>
            <td style="${debtStyle}">${user.totalDebt}$</td>
            <td>${user.role !== 'ADMIN' ? `<button class="btn-danger" onclick="deleteUser(${user.id})">❌</button>` : '-'}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function registerUser() {
    const fullName = document.getElementById('newUserName').value;
    const email = document.getElementById('newUserEmail').value;
    const password = document.getElementById('newUserPass').value;
    const role = document.getElementById('newUserRole').value;

    const response = await fetch(`${API_URL}/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, email, password, role })
    });

    if(response.ok) {
        alert("User created!");
        loadUsers();
    } else {
        alert("Creation error (Email taken?)");
    }
}

async function deleteUser(id) {
    if(!confirm("Delete user?")) return;
    const response = await fetch(`${API_URL}/users/${id}`, { method: 'DELETE' });
    if(response.ok) loadUsers();
    else alert(await response.text());
}

// ==========================================
//           🔍 LIVE SEARCH LOGIC
// ==========================================

function filterBooks() {
    const input = document.getElementById('searchInput');
    const filter = input.value.toLowerCase();
    const table = document.getElementById('booksTableBody');
    const tr = table.getElementsByTagName('tr');

    for (let i = 0; i < tr.length; i++) {
        // [0]=ID, [1]=Title, [2]=AUTHOR, [3]=ISBN
        const tdTitle = tr[i].getElementsByTagName('td')[1];
        const tdAuthor = tr[i].getElementsByTagName('td')[2];
        const tdIsbn = tr[i].getElementsByTagName('td')[3];

        if (tdTitle || tdAuthor || tdIsbn) {
            const titleVal = tdTitle ? tdTitle.textContent : "";
            const authorVal = tdAuthor ? tdAuthor.textContent : "";
            const isbnVal = tdIsbn ? tdIsbn.textContent : "";

            // Ищем везде
            if (titleVal.toLowerCase().indexOf(filter) > -1 ||
                authorVal.toLowerCase().indexOf(filter) > -1 ||
                isbnVal.toLowerCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}