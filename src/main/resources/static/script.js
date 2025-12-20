const API_URL = 'http://localhost:8081/api';
let currentUser = null;
let authToken = null;

// --- ЗАГРУЗКА СТРАНИЦЫ ---
document.addEventListener("DOMContentLoaded", () => {
    const storedUser = localStorage.getItem('library_user');
    const storedToken = localStorage.getItem('library_token');

    if (storedUser && storedToken) {
        currentUser = JSON.parse(storedUser);
        authToken = storedToken;
        restoreSession();
    }
});

// --- УМНАЯ ФУНКЦИЯ ЗАПРОСОВ (С ТОКЕНОМ) ---
async function authFetch(url, options = {}) {
    if (!options.headers) options.headers = {};

    // Добавляем токен
    if (authToken) {
        options.headers['Authorization'] = 'Bearer ' + authToken;
    }
    // Добавляем тип контента JSON
    if (!options.headers['Content-Type']) {
        options.headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(url, options);

    // Если токен протух
    if (response.status === 401 || response.status === 403) {
        alert("Session expired. Login again.");
        logout();
        return null;
    }
    return response;
}

// --- 1. ВХОД (LOGIN) ---
async function handleLogin() {
    const email = document.getElementById('emailInput').value;
    const password = document.getElementById('passwordInput').value;

    try {
        const response = await fetch(API_URL + '/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, password: password })
        });

        if (!response.ok) throw new Error("Login failed");

        const data = await response.json();

        currentUser = data.user;
        authToken = data.token;

        localStorage.setItem('library_user', JSON.stringify(currentUser));
        localStorage.setItem('library_token', authToken);

        restoreSession();

    } catch (error) {
        console.error(error);
        alert("❌ Ошибка входа! Проверь пароль.");
    }
}

// --- ВОССТАНОВЛЕНИЕ ИНТЕРФЕЙСА ---
function restoreSession() {
    if (document.getElementById('loginSection')) {
        document.getElementById('loginSection').classList.add('hidden');
        document.getElementById('dashboardSection').classList.remove('hidden');
        document.getElementById('welcomeMsg').innerText = 'Hello, ' + currentUser.fullName;

        if (currentUser.role === 'ADMIN') {
            document.getElementById('adminPanel').classList.remove('hidden');
        } else {
            document.getElementById('studentPanel').classList.remove('hidden');
            loadMyBorrows();
        }
        loadBooks();
    }
}

function checkAuth() {
    if (!currentUser || currentUser.role !== 'ADMIN') {
        alert("⛔ Только для Админов!");
        window.location.href = 'index.html';
    }
}

function logout() {
    localStorage.removeItem('library_user');
    localStorage.removeItem('library_token');
    window.location.href = 'index.html';
}

// ==========================================
//           ЛОГИКА КНИГ
// ==========================================

async function loadBooks() {
    if(!document.getElementById('booksTableBody')) return;

    const response = await authFetch(API_URL + '/books');
    if (!response) return;

    const books = await response.json();
    const tbody = document.getElementById('booksTableBody');
    tbody.innerHTML = '';

    const isAdmin = currentUser && currentUser.role === 'ADMIN';

    books.forEach(book => {
        let authorName = "Unknown";
        if (book.authors && book.authors.length > 0) {
            authorName = book.authors[0].name;
        }

        const isFree = book.available;

        // Формируем кнопку действия (чтобы не путаться в кавычках)
        let actionBtn = "";
        if (isAdmin) {
            actionBtn = "<button class='btn-danger' onclick='deleteBook(" + book.id + ")'>🗑️</button>";
        } else if (isFree) {
            actionBtn = "<button class='btn-take' onclick='borrowBook(" + book.id + ")'>Borrow</button>";
        } else {
            actionBtn = "<button disabled style='opacity:0.5'>Taken</button>";
        }

        const statusBadge = isFree ?
            "<span class='badge status-ok'>Available</span>" :
            "<span class='badge status-busy'>Borrowed</span>";

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${book.id}</td>
            <td><b>${book.title}</b></td>
            <td>${authorName}</td>
            <td>${book.isbn || '-'}</td>
            <td>${statusBadge}</td>
            <td>${actionBtn}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function borrowBook(bookId) {
    if (!currentUser) return alert("Please log in!");
    await authFetch(API_URL + '/borrow?userId=' + currentUser.id + '&bookId=' + bookId, { method: 'POST' });
    loadBooks();
    loadMyBorrows();
}

async function deleteBook(id) {
    if(confirm("Удалить книгу?")) {
        await authFetch(API_URL + '/books/' + id, { method: 'DELETE' });
        loadBooks();
    }
}

async function addNewBook() {
    const title = document.getElementById('newBookTitle').value;
    const author = document.getElementById('newBookAuthor').value;
    const isbn = document.getElementById('newBookIsbn').value;

    if (!title || !author) {
        alert("⚠️ Введите название и автора!");
        return;
    }

    await authFetch(API_URL + '/books', {
        method: 'POST',
        body: JSON.stringify({
            title: title,
            isbn: isbn,
            authorName: author
        })
    });

    document.getElementById('newBookTitle').value = '';
    document.getElementById('newBookAuthor').value = '';
    document.getElementById('newBookIsbn').value = '';
    loadBooks();
}

// ==========================================
//        ЛОГИКА ПОЛЬЗОВАТЕЛЕЙ
// ==========================================

async function loadMyBorrows() {
    if(!document.getElementById('myBorrowsList')) return;

    const response = await authFetch(API_URL + '/borrow/my/' + currentUser.id);
    if (!response) return;

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
    await authFetch(API_URL + '/borrow/return/' + id, { method: 'POST' });
    loadMyBorrows();
    loadBooks();
}

async function loadUsers() {
    const tbody = document.getElementById('usersTableBody');
    if(!tbody) return;

    const response = await authFetch(API_URL + '/users');
    if (!response) return;

    const users = await response.json();
    tbody.innerHTML = '';

    users.forEach(user => {
        const debtStyle = user.totalDebt > 0 ? 'color:red;font-weight:bold' : 'color:green';

        // Безопасная кнопка удаления
        let deleteBtn = '-';
        if (user.role !== 'ADMIN') {
            deleteBtn = "<button class='btn-danger' onclick='deleteUser(" + user.id + ")'>❌</button>";
        }

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td>${user.activeBooks}</td>
            <td style="${debtStyle}">${user.totalDebt}</td>
            <td>${deleteBtn}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function registerUser() {
    const fullName = document.getElementById('newUserName').value;
    const email = document.getElementById('newUserEmail').value;
    const password = document.getElementById('newUserPass').value;
    const role = document.getElementById('newUserRole').value;

    const response = await authFetch(API_URL + '/users', {
        method: 'POST',
        body: JSON.stringify({ fullName, email, password, role })
    });

    if(response && response.ok) {
        alert("User created!");
        loadUsers();
    } else {
        alert("Error creating user");
    }
}

async function deleteUser(id) {
    if(!confirm("Удалить пользователя?")) return;
    const response = await authFetch(API_URL + '/users/' + id, { method: 'DELETE' });
    if(response && response.ok) loadUsers();
}

// Поиск
function filterBooks() {
    const input = document.getElementById('searchInput');
    const filter = input.value.toLowerCase();
    const rows = document.querySelectorAll('#booksTableBody tr');

    rows.forEach(row => {
        const text = row.innerText.toLowerCase();
        row.style.display = text.includes(filter) ? '' : 'none';
    });
}