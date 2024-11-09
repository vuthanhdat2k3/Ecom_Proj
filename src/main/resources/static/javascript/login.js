// Lấy các phần tử cần thiết
const passwordField = document.querySelector(".input-div.pass .input");
const togglePasswordButton = document.createElement("button");

// Xử lý form đăng nhập
const form = document.querySelector("form");
const passwordError = document.createElement("p");
passwordError.id = "password-error";
passwordError.style.display = "none";
passwordError.style.color = "red";
passwordError.textContent = "Incorrect username or password. Please try again.";
form.appendChild(passwordError);

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = form.querySelector("input[type='text']").value;
    const password = form.querySelector("input[type='password']").value;

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username, password: password })
        });

        if (response.ok) {
            // Đăng nhập thành công
            const responseData = await response.json();
            console.log(responseData); // Xử lý dữ liệu trả về nếu cần
            localStorage.setItem('static', "true");
            localStorage.setItem('userId', responseData.userId);
            localStorage.setItem('token', responseData.token);
            localStorage.setItem('username', username);
            if (responseData.role === "USER") {
                window.location.href = "../../assets/features/trang-user.html";
            } else {
                window.location.href = "../../assets/features/dashboard-admin.html";
            }
        } else {
            // Đăng nhập thất bại
            console.error('Login failed:', response.statusText);
            passwordError.style.display = 'block';
        }
    } catch (error) {
        console.error('Error during login:', error);
    }
});
