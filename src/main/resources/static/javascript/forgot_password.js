document.getElementById('forgot-password-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.querySelector("input[name='username']").value;
    const messageElement = document.getElementById('error-message');
    const sendBtn = document.getElementById('send-btn');
    const spinner = document.getElementById('spinner');

    // Reset previous error messages
    messageElement.style.display = 'none';
    sendBtn.disabled = true; // Disable button to prevent multiple submissions
    spinner.style.display = 'inline-block'; // Show spinner

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/forgot-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: username
        });

        if (response.ok) {
            // window.location.href = "../html/reset_password.html"; // Redirect to reset password page
        } else if (response.status === 404) {
            messageElement.textContent = 'Username not exist';
            messageElement.style.display = 'block';
        } else {
            messageElement.textContent = 'An error occurred. Please try again.';
            messageElement.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        messageElement.textContent = 'An error occurred. Please try again.';
        messageElement.style.display = 'block';
    } finally {
        sendBtn.disabled = false; // Re-enable button
        spinner.style.display = 'none'; // Hide spinner
    }
});
