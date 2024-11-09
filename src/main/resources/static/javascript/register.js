const form = document.getElementById("registrationForm");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = form.querySelector("input[name='username']").value;
    const email = form.querySelector("input[name='email']").value;
    const password = form.querySelector("input[name='password']").value;
    const confirmPassword = form.querySelector("input[name='confirm_password']").value;

    // Reset previous errors before checking
    const usernameError = "Username already exists";
    const passwordError = "Password must be at least 8 characters long";
    const confirmPasswordError = "Confirm password does not match";
    const emailError = "Please enter a valid email address";

    // Check if passwords match
    if (password !== confirmPassword) {
        alert(confirmPasswordError);
        return;
    }

    // Check password length
    if (password.length < 8) {
        alert(passwordError);
        return;
    }

    // Check email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert(emailError);
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username, email: email, password: password })
        });

        if (response.ok) {
            // Successful registration
            console.log('Registration successful');
            const responseData = await response.json();

            // Redirect to login page
            window.location.href = "login.html";
        } else if (response.status === 409) {
            console.error(usernameError);
            alert(usernameError);
        } else {
            // Registration failed
            console.error('Registration failed:', response.statusText);
            alert("Registration failed. Please try again.");
        }
    } catch (error) {
        console.error('Error during registration:', error);
        // Handle error, display message to user
        alert("An error occurred during registration. Please try again.");
    }
});
