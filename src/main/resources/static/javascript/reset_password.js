document.getElementById('reset-password-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const token = document.querySelector("input[name='otp']").value;
    console.log(token);
    const username = document.querySelector("input[name='username']").value;
    const newPassword = document.querySelector("input[name='new-password']").value;
    const confirmPassword = document.querySelector("input[name='confirm-password']").value;
    const messageElement = document.getElementById('reset-password-message');

    // Reset previous error messages
    messageElement.style.display = 'none';

    // Check if the new password and confirmation password match
    if (newPassword !== confirmPassword) {
        messageElement.textContent = 'Passwords do not match.';
        messageElement.style.display = 'block';
        return;
    }

    // Make sure the new password meets certain criteria (e.g., length)
    if (newPassword.length < 8) {
        messageElement.textContent = 'Password must be at least 8 characters.';
        messageElement.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/reset-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username, token: token, newPassword: newPassword })
        });

        if (response.ok) {
            messageElement.textContent = 'Your password has been reset successfully!';
            messageElement.style.display = 'block';
            setTimeout(() => {
                window.location.href = "login.html"; // Redirect to login page after success
            }, 2000);
        } else if (response.status === 400) {
            messageElement.textContent = 'Invalid OTP or password criteria not met.';
            messageElement.style.display = 'block';
        } else {
            messageElement.textContent = 'An error occurred. Please try again.';
            messageElement.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        messageElement.textContent = 'An error occurred. Please try again.';
        messageElement.style.display = 'block';
    }
});
